package com.eva.check.service.core.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MoreLikeThisQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import com.eva.check.pojo.PaperParagraph;
import com.eva.check.pojo.dto.SimilarPaperParagraph;
import com.eva.check.service.config.CheckProperties;
import com.eva.check.service.core.PaperCoreService;
import com.eva.check.service.es.converter.EsDocConverter;
import com.eva.check.service.es.entity.PaperParagraphDoc;
import com.eva.check.service.es.repository.PaperParagraphRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EsPaperCoreServiceImpl implements PaperCoreService {
    private final CheckProperties checkProperties;

    private final PaperParagraphRepository paperParagraphRepository;


    private final ElasticsearchClient esClient;

    @Override
    public void collectParagraph(PaperParagraph paperParagraph) {
        PaperParagraphDoc paperParagraphDoc = EsDocConverter.INSTANCE.paperParagraph2Doc(paperParagraph);
        // 文档入库，存入Es
        paperParagraphRepository.save(paperParagraphDoc);
    }

    @Override
    public List<SimilarPaperParagraph> findSimilarPaperParagraph(PaperParagraph paperParagraph) {

        //  pageAfter分页,当前的pageSize只是做一个安全防御。按照目前的一般的业务场景来说，不会超过该阈值。
        // 这里设置为30，一般真实的业务场景下，重复的30篇文章也算是比较夸张了。即使由更多的重复文章，也没有比较更多数据的必要了，所以这里设置为30。
        int pageSize = 30;
        Query paperNoQuery = MatchQuery.of(m -> m
                .field("paperNo")
                .query(StringUtils.hasText(paperParagraph.getPaperNo()) ? paperParagraph.getPaperNo() : "-11")
        )._toQuery();
        // https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-mlt-query.html#query-dsl-mlt-query
        Query moreLikeThisQuery = MoreLikeThisQuery.of(m -> m
                .like(builder -> builder.text(paperParagraph.getContent()))
                .fields("content")
                // 用于分析自由格式文本的分析器。默认为与字段中的第一个字段关联的分析器。
                .analyzer("ik_smart")
                // 输入文档中将忽略术语的最小文档频率。默认值为 5。
                .minDocFreq(2)
                // 输入文档中将忽略术语的最大文档频率。这对于忽略频繁使用的单词（如停用词）可能很有用。默认为无界 （Integer.MAX_VALUE，即 2^31-1 或 2147483647）。
                .maxDocFreq(1000)
                // 输入文档中将忽略术语的最小术语频率。默认值为 2。
                .minTermFreq(2)
                // 将选择的最大查询词数。增加此值可提高准确性，但会降低查询执行速度。默认值为 25。
                .maxQueryTerms(25)
                // 字词将被忽略的最小字长。默认值为 0。
                .minWordLength(2)
                //停用词数组。此集合中的任何单词都被视为“无趣”并被忽略。如果分析器允许使用停用词，您可能希望告诉 MLT 显式忽略它们，因为出于文档相似性的目的，假设“停用词从来都不有趣”似乎是合理的。
                // 是否直接在ES中设置好？
//                .stopWords(null)
                // 形成析取查询后，此参数控制必须匹配的术语数。语法与最小值应匹配的语法相同。（默认为“30%”）。
                .minimumShouldMatch(checkProperties.getContentSimilarityThreshold())
                // 设置整个查询的提升值。默认值为 1.0。
                .boost(1F)
        )._toQuery();
        SearchResponse<PaperParagraphDoc> response = null;
        try {
            /*SearchRequest searchRequest = new SearchRequest.Builder().index("paper-paragraph")
                    .query(q -> q
                            .bool(b -> b
                                    .mustNot(paperNoQuery)
                                    .must(moreLikeThisQuery)
                            )
                    ).size(pageSize)
                    .build();*/
            response = esClient.search(s -> s
                            .index("paper-paragraph")
                            .query(q -> q
                                    .bool(b -> b
                                            .mustNot(paperNoQuery)
                                            .must(moreLikeThisQuery)
                                    )
                            )
                            .size(pageSize)
/*
                            .source(builder -> builder.filter(SourceFilter.of(sf -> sf.excludes("content"))))
*/
                    ,
                    PaperParagraphDoc.class
            );
        } catch (Exception e) {
            log.error("调用ES查找疑似文章失败", e);
        }
        if (response == null) {
            return null;
        }

        TotalHits total = response.hits().total();
        assert total != null;
        long totalDataCount = total.value();
        long tempPageNo = totalDataCount % pageSize == 0L ? totalDataCount / pageSize : (totalDataCount / pageSize) + 1L;
        log.info("paperParagraphId:{} 总数量={}, 总页数={}", paperParagraph.getParagraphId(), totalDataCount, tempPageNo);
        List<Hit<PaperParagraphDoc>> hits = response.hits().hits();
        log.info(" 总共匹配数量:{}", hits.size());
        /*for (Hit<PaperParagraphDoc> hit : hits) {
            PaperParagraphDoc source = hit.source();
            log.info("Found product " + source);
            System.err.println(hit.sort());
        }*/



       /* MoreLikeThisQuery query = new MoreLikeThisQuery();
        query.set*/
        /* NativeQuery.builder().*/
       /* QueryBuilders.bool()
                .mustNot(QueryBuilders.match);*/
        /* SearchRequest searchRequest = new SearchRequest("paper-paragraph");*/
        return hits.stream().map(s -> {
            PaperParagraphDoc source = s.source();
            assert source != null;
            return SimilarPaperParagraph.builder()
                    .paragraphId(source.getParagraphId())
                    .paperId(source.getPaperId())
                    .paperNo(source.getPaperNo())
                    .build();
        }).toList();
    }
}
