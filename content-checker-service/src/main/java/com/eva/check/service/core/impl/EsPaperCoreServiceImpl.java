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
        int pageSize = 5000;
        Query paperNoQuery = MatchQuery.of(m -> m
                .field("paperNo")
                .query(StringUtils.hasText(paperParagraph.getPaperNo()) ? paperParagraph.getPaperNo() : "-11")
        )._toQuery();

        Query moreLikeThisQuery = MoreLikeThisQuery.of(m -> m
                .like(builder -> builder.text(paperParagraph.getContent()))
                .fields("content")
                .analyzer("ik_smart")
                .minDocFreq(2)
                .minTermFreq(1)
                .minimumShouldMatch(checkProperties.getContentSimilarityThreshold())
                .boost(1F)
        )._toQuery();
        SearchResponse<PaperParagraphDoc> response = null;
        try {
           /* SearchRequest searchRequest = new SearchRequest.Builder().index("paper-paragraph")
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
                            ).size(pageSize),
                    PaperParagraphDoc.class
            );
        } catch (Exception e) {
            log.error("", e);
        }
        if (response == null) {
            return null;
        }

        TotalHits total = response.hits().total();
        assert total != null;
        long totalDataCount = total.value();
        long tempPageNo = totalDataCount % pageSize == 0L ? totalDataCount / pageSize : (totalDataCount / pageSize) + 1L;
        log.info("paperParagraphId:{} 总数量={},总页数={}", paperParagraph.getParagraphId(), totalDataCount, tempPageNo);
        List<Hit<PaperParagraphDoc>> hits = response.hits().hits();
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