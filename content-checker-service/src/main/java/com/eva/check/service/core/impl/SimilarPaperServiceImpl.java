package com.eva.check.service.core.impl;

import cn.hutool.core.util.NumberUtil;
import com.eva.check.pojo.*;
import com.eva.check.pojo.dto.PaperResult;
import com.eva.check.pojo.dto.ParagraphResult;
import com.eva.check.pojo.dto.SentencePairResult;
import com.eva.check.pojo.dto.SentenceResult;
import com.eva.check.service.config.CheckProperties;
import com.eva.check.service.core.SimilarPaperService;
import com.eva.check.service.core.SimilarTextRender;
import com.eva.check.service.core.SimilarTextRule;
import com.eva.check.service.support.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 相似论文服务
 *
 * @author zzz
 * @date 2023/11/28 22:37
 */
@Service
@RequiredArgsConstructor
public class SimilarPaperServiceImpl implements SimilarPaperService {

    private final SimilarTextRule similarTextRule;

    private final SimilarTextRender similarTextRender;
    private final CheckSentenceService checkSentenceService;
    private final CheckSentencePairService checkSentencePairService;
    private final PaperSentenceService paperSentenceService;
    private final CheckParagraphService checkParagraphService;
    private final PaperInfoService paperInfoService;
    private final CheckProperties checkProperties;

    @Override
    public ParagraphResult assembleParagraphResult(String originalParagraph, Double similarity, Long checkParagraphId) {

        ParagraphResult paragraphResult = new ParagraphResult();
        paragraphResult.setSimilarity(similarity);
        paragraphResult.setCssClassName(this.similarTextRule.computeTextColor(similarity).getCssClass());
        paragraphResult.setCheckParagraphId(checkParagraphId);
        // 不相似则直接返回原文
        /*if (this.similarTextRule.isNotSimilar(similarity)) {
            paragraphResult.setRenderContent(originalParagraph);
            return paragraphResult;
        }*/
        // 按照句子进行处理
        Map<Long, SentenceResult> sentenceResultMap = Maps.newHashMap();
        List<CheckSentence> checkSentences = this.checkSentenceService.getByParagraphId(checkParagraphId);
        int startIndex = 0;
        StringBuilder newText = new StringBuilder();
        for (CheckSentence checkSentence : checkSentences) {
            if (this.similarTextRule.isNotSimilar(checkSentence.getSimilarity())) {
                continue;
            }
            // 每个句子的结果
            SentenceResult sentenceResult = new SentenceResult();
            sentenceResult.setCheckSentence(checkSentence.getContent());
            sentenceResult.setSimilarity(checkSentence.getSimilarity());
            sentenceResult.setCssClassName(this.similarTextRule.computeTextColor(checkSentence.getSimilarity()).getCssClass());
            List<CheckSentencePair> checkSentencePairList = this.checkSentencePairService.getAllByCheckSentenceId(checkSentence.getSentenceId(), checkProperties.getSentenceSimilarityThreshold());
            // 句子对结果的列表
            List<SentencePairResult> sentencePairResultList = Lists.newArrayList();
            // 循环检测句子对列表，填充好句子对结果的列表
            for (CheckSentencePair checkSentencePair : checkSentencePairList) {
                SentencePairResult sentencePairResult = new SentencePairResult();
                sentencePairResult.setFormatSimilarity(NumberUtil.decimalFormat("#.##%", checkSentencePair.getSimilarity()));
                sentencePairResult.setSimilarity(checkSentencePair.getSimilarity());
                PaperSentence paperSentence = this.paperSentenceService.getById(checkSentencePair.getTargetSentenceId());
                sentencePairResult.setTargetSentence(paperSentence.getOriginContent());
                // 计算句子对的相似度所对应的颜色
                sentencePairResult.setCssClassName(this.similarTextRule.computeTextColor(checkSentencePair.getSimilarity()).getCssClass());
                // 拼装论文基本信息
//                checkSentencePair.getTargetParaId()
                PaperInfo targetPaperInfo = this.paperInfoService.getByParagraphId(checkSentencePair.getTargetParaId());
                sentencePairResult.setTargetAuthor(targetPaperInfo.getAuthor());
                sentencePairResult.setTargetTitle(targetPaperInfo.getTitle());
                sentencePairResult.setTargetPublishYear(targetPaperInfo.getPublishYear());
                sentencePairResultList.add(sentencePairResult);
            }
            // 填充句子对结果的列表
            sentenceResult.setSentencePairResultList(sentencePairResultList);
            sentenceResult.setSimilarCount(sentencePairResultList.size());
            sentenceResultMap.put(checkSentence.getSentenceId(), sentenceResult);
            // 渲染句子的内容
            String renderSentence = this.similarTextRender.renderSentence(
                    checkSentence.getOriginContent(),
                    checkSentence.getSimilarity(),
                    checkSentence.getSentenceId().toString());
            // 将渲染后的句子内容替换原段落内容
            //  这里不能直接用replaceAll
//            originalParagraph = originalParagraph.replaceAll(checkSentence.getOriginContent(), renderSentence);

            // 计算句子所在位置
            int position = originalParagraph.indexOf(checkSentence.getOriginContent(), startIndex);
            // 如果子串存在
            if (position != -1) {
                // 先直接拼接startIndex与子串之间的字符串
                newText.append(originalParagraph, startIndex, position);
                // 拼接渲染后的字符串
                newText.append(renderSentence);
                // 更新startIndex，跳过子串长度（已经匹配过的子串不再参与匹配）
                startIndex = position + checkSentence.getOriginContent().length();
            }

        }
        // 当所有句子匹配替换结束之后，拼接最后剩余下来的字符串
        newText.append(originalParagraph, startIndex, originalParagraph.length());

        paragraphResult.setRenderContent(newText.toString());
        paragraphResult.setSimilarSentenceResultMap(sentenceResultMap);
        return paragraphResult;
    }


    @Override
    public PaperResult assemblePaperResult(Long taskId) {
        PaperResult paperResult = new PaperResult();
        List<ParagraphResult> paragraphResultList = Lists.newArrayList();
        List<CheckParagraph> checkParagraphs = this.checkParagraphService.getByTaskId(taskId);
        for (CheckParagraph checkParagraph : checkParagraphs) {
            ParagraphResult paragraphResult = this.assembleParagraphResult(checkParagraph.getContent(), checkParagraph.getSimilarity(), checkParagraph.getParagraphId());
            paragraphResultList.add(paragraphResult);
        }
        paperResult.setParagraphResultList(paragraphResultList);
        return paperResult;
    }
}
