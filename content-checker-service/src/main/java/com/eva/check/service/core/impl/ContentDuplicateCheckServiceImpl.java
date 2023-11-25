package com.eva.check.service.core.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.eva.check.common.enums.CheckParagraphPairStatus;
import com.eva.check.common.enums.CheckSentencePairStatus;
import com.eva.check.common.enums.CheckSentenceStatus;
import com.eva.check.common.enums.DataType;
import com.eva.check.common.util.SimilarUtil;
import com.eva.check.common.util.TextUtil;
import com.eva.check.pojo.*;
import com.eva.check.pojo.dto.SimilarPaperParagraph;
import com.eva.check.service.config.CheckProperties;
import com.eva.check.service.core.DuplicateCheckService;
import com.eva.check.service.core.PaperCoreService;
import com.eva.check.service.event.CheckParagraphEvent;
import com.eva.check.service.event.CheckTaskCancelEvent;
import com.eva.check.service.event.CheckTaskFinishEvent;
import com.eva.check.service.event.CollectResultEvent;
import com.eva.check.service.mq.producer.SendMqService;
import com.eva.check.service.support.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 内容重复检查服务
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ContentDuplicateCheckServiceImpl implements DuplicateCheckService {

    private final PaperCoreService paperCoreService;
    private final PaperSentenceService paperSentenceService;
    private final PaperTokenService tokenService;
    private final CheckTaskService checkTaskService;
    private final CheckSentenceService checkSentenceService;
    private final CheckSentencePairService checkSentencePairService;
    private final CheckParagraphService checkParagraphService;
    private final CheckParagraphPairService checkParagraphPairService;
    private final CheckProperties checkProperties;

    private SendMqService sendMqService;

    private final Map<Long, List<String>> sentenceTokenMap = Maps.newConcurrentMap();
    private final Map<Long, Map<String, Float>> sentenceWordFrequencyPool = Maps.newConcurrentMap();

    @Autowired
    @Lazy
    public void setSendMqService(SendMqService sendMqService) {
        this.sendMqService = sendMqService;
    }

    @Override
    public void findSimilarParagraph(CheckTask checkTask) {
        if (!DataType.FULL_TEXT.getValue().equals(checkTask.getCheckType())) {
            log.warn("[ContentEventBusListenerImpl] 非全文比对类型，不执行预检查");
            return;
        }

        // TODO 可以缓存 待观察
        List<CheckParagraph> checkParagraphList = this.checkParagraphService.getByTaskId(checkTask.getTaskId());
        if (CollectionUtil.isEmpty(checkParagraphList)) {
            log.warn("[ContentEventBusListenerImpl] 无论文段落，不执行预检查");
            CheckTaskCancelEvent checkTaskCancelEvent = CheckTaskCancelEvent.builder().checkTask(checkTask).build();
            this.sendMqService.cancelTask(checkTaskCancelEvent);
            return;
        }

        // 通过simHash快速判断是否重复，获取重复论文列表
        List<CheckParagraphPair> checkParagraphListPair = Lists.newArrayListWithCapacity(16);
        checkParagraphList.forEach(checkParagraph -> {
            PaperParagraph paperParagraph = PaperParagraph.builder().paperNo(checkParagraph.getPaperNo())
                    .content(checkParagraph.getContent())
                    .hash(checkParagraph.getHash())
                    .hash1(checkParagraph.getHash1())
                    .hash2(checkParagraph.getHash2())
                    .hash3(checkParagraph.getHash3())
                    .hash4(checkParagraph.getHash4())
                    .build();

            List<SimilarPaperParagraph> similarPaperList = this.paperCoreService.findSimilarPaperParagraph(paperParagraph);
            // TODO 目前这种方法无法快速查找的可能相似的文档
//            List<SimilarPaperParagraph> similarPaperList = this.paperSimHashIndexService.findSimilarPaper(paperParagraph);
            if (CollUtil.isEmpty(similarPaperList)) {
                return;
            }

            similarPaperList.forEach(e -> {
                CheckParagraphPair list = CheckParagraphPair.builder()
                        .taskId(checkTask.getTaskId())
                        .checkParaId(checkParagraph.getParagraphId())
                        .targetParaId(e.getParagraphId())
                        .status(CheckParagraphPairStatus.INIT.getValue())
                        .similarity(-1D)
                        .build();
                checkParagraphListPair.add(list);
            });
        });

        // 查找相似论文
        if (CollectionUtil.isEmpty(checkParagraphListPair)) {
            // 如果没有相似论文，则直接结束当前任务并设置相似度为0
            checkTask.setSimilarity(0D);
            CheckTaskFinishEvent checkTaskFinishEvent = CheckTaskFinishEvent.builder().checkTask(checkTask).build();
            this.sendMqService.finishTask(checkTaskFinishEvent);
        } else {
            // 生成比对文本对
            this.checkParagraphPairService.initCompareList(checkParagraphListPair);
            // 触发比对事件事件
            CheckParagraphEvent checkParagraphEvent = CheckParagraphEvent.builder()
                    .checkTask(checkTask)
                    .build();
            this.sendMqService.doParagraphCheck(checkParagraphEvent);
        }
    }


    @Override
    public void doPragraphCheck(CheckTask checkTask) {
        // TODO 这里的校验可以用多线程进行
        List<CheckParagraphPair> checkParagraphPairList = this.checkParagraphPairService.getByTaskId(checkTask.getTaskId());
        // 遍历每一对需要比较的段落
        for (CheckParagraphPair checkParagraphPair : checkParagraphPairList) {
            // 获取待检测的段落包含的句子
            List<CheckSentence> checkSentenceList = this.checkSentenceService.getByParagraphId(checkParagraphPair.getCheckParaId());
            if (CollUtil.isEmpty(checkSentenceList)) {
                // 回写CheckParagraphPair
                checkParagraphPair.setSimilarity(0D);
                checkParagraphPair.setStatus(CheckParagraphPairStatus.DONE.getValue());
                continue;
            }

            List<CheckSentencePair> checkSentencePairList = Lists.newArrayListWithCapacity(16);
            List<PaperSentence> paperSentenceList = this.paperSentenceService.getByParagraphId(checkParagraphPair.getTargetParaId());
            // 相似度累加器
            double similarityCounter = 0D;
            // 待检测句子按照每一句进行处理
            for (CheckSentence checkSentence : checkSentenceList) {
                // 获取待检测句子的所有关键词词频 TODO 要提取到别的类中实现
                Map<String, Float> checkSentenceWordFrequency = sentenceWordFrequencyPool.computeIfAbsent(checkSentence.getSentenceId(), s -> Maps.newHashMap());
                if (CollUtil.isEmpty(checkSentenceWordFrequency)) {
                    List<String> checkSentenceTokenList = sentenceTokenMap.computeIfAbsent(checkSentence.getSentenceId(), s -> Lists.newArrayList());
                    if (CollUtil.isEmpty(checkSentenceTokenList)) {
                        // 为空就进行分词并且加入缓存
                        checkSentenceTokenList.addAll(TextUtil.cleanAndSegment(checkSentence.getContent()));
                    }
                    // 计算词频
                    Map<String, Float> charSequenceIntegerMap = SimilarUtil.countWordFrequency(checkSentenceTokenList);
                    if (CollUtil.isEmpty(charSequenceIntegerMap)) {
                        continue;
                    }
                    checkSentenceWordFrequency.putAll(charSequenceIntegerMap);
                }

                for (PaperSentence targetPaperSentence : paperSentenceList) {
                    // 获取当前需要比对的句子包含的词
                    // TODO 这里应该是计算词频
                    List<PaperToken> paperSentenceTokenList = this.tokenService.getTokenBySentenceIdFromCache(targetPaperSentence.getSentenceId());
                    if (CollUtil.isEmpty(paperSentenceTokenList)) {
                        continue;
                    }
                    List<String> paperSentenceWordList = paperSentenceTokenList.stream().map(PaperToken::getContent).toList();
                    // 计算词频
                    Map<String, Float> paperSentenceWordFrequency = SimilarUtil.countWordFrequency(paperSentenceWordList);
                    // 计算相似度
                    double cosineSimilarity = SimilarUtil.getCosineSimilarity(checkSentenceWordFrequency, paperSentenceWordFrequency);
                    if (cosineSimilarity < checkProperties.getSentenceSimilarityThreshold()) {
                        log.debug("相似度小于阈值，不进行相似度计算");
                        continue;
                    }
                    // 相似度累加
                    similarityCounter += cosineSimilarity;
                    // 计算每句的相似度以及相似的词。
                    CheckSentencePair checkSentencePair = CheckSentencePair.builder()
                            .taskId(checkTask.getTaskId())
                            .checkParaId(checkParagraphPair.getCheckParaId())
                            .targetParaId(checkParagraphPair.getTargetParaId())
                            .checkSentenceId(checkSentence.getSentenceId())
                            .targetSentenceId(targetPaperSentence.getSentenceId())
                            .similarity(cosineSimilarity)
                            .status(CheckSentencePairStatus.DONE.getValue())
                            .build();
                    checkSentencePairList.add(checkSentencePair);
                }
            }
            // 设置每对段落的相似度 ，这里没有统计那些句子对没有相似度的情况
            checkParagraphPair.setSimilarity(SimilarUtil.formatSimilarity(similarityCounter / checkSentencePairList.size()));
            checkParagraphPair.setStatus(CheckParagraphPairStatus.DONE.getValue());
            // 按照每一对CheckParagraphPair批量保存CheckSentencePair
            this.checkSentencePairService.saveBatch(checkSentencePairList);
        }

        // 汇总段落检测对的结果
        this.checkParagraphPairService.updateBatchById(checkParagraphPairList);

        // 汇总句子的检测结果

        // 汇总段落的检测结果

        // 发送结果汇总事件。
        CollectResultEvent event = CollectResultEvent.builder()
                .checkTask(checkTask)
                .build();
        this.sendMqService.doCollectResult(event);
    }

            /*ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("collectResult-%d")
                .build();
        ExecutorService executorService = new ThreadPoolExecutor(2, 2,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), threadFactory);*/

    @Override
    public void collectResult(CheckTask checkTask) {

        List<CheckSentence> allCheckSentenceList = Lists.newArrayList();
        List<CheckParagraph> checkParagraphList = this.checkParagraphService.getByTaskId(checkTask.getTaskId());
        double paragraphSimilarityCounter = 0D;
        for (CheckParagraph checkParagraph : checkParagraphList) {
            // 按照段落查找所有检测的句子
            List<CheckSentence> checkSentenceList = this.checkSentenceService.getByParagraphId(checkParagraph.getParagraphId());
            if (CollUtil.isEmpty(checkSentenceList)) {
                finishCheckParagraph(checkParagraph, 0D);
                log.debug("checkParagraphList is empty, ParagraphId: {}", checkParagraph.getParagraphId());
                continue;
            }
            double sentenceSimilarityCounter = 0D;
            for (CheckSentence checkSentence : checkSentenceList) {
                // 查找每个句子的检测对
                List<CheckSentencePair> checkSentencePairList = this.checkSentencePairService.getAllByCheckSentenceId(checkSentence.getSentenceId());
                if (CollUtil.isEmpty(checkSentencePairList)) {
                    finishCheckSentence(checkSentence, 0D);
                    log.debug("checkSentencePairList is empty, CheckSentenceId: {}", checkSentence.getSentenceId());
                    continue;
                }
                double sentenceSimilarity = SimilarUtil.formatSimilarity(checkSentencePairList.stream().mapToDouble(CheckSentencePair::getSimilarity).sum() / checkSentencePairList.size());
                finishCheckSentence(checkSentence, sentenceSimilarity);
                // 累加每个句子的相似度
                sentenceSimilarityCounter += sentenceSimilarity;
            }
            // 收集所有的句子，以便最终批量更新结果
            allCheckSentenceList.addAll(checkSentenceList);
            // 设置段落的相似度,并将结果设置为结束
            finishCheckParagraph(checkParagraph, SimilarUtil.formatSimilarity(sentenceSimilarityCounter / checkSentenceList.size()));
            // 统计所有段落的相似度综合
            paragraphSimilarityCounter += checkParagraph.getSimilarity();
        }

        // 批量更新【检测句子】的结果
        this.checkSentenceService.updateBatchById(allCheckSentenceList);
        // 批量更新【检测段落】的结果
        this.checkParagraphService.updateBatchById(checkParagraphList);
        // 段落相似度综合
        double taskSimilarity = SimilarUtil.formatSimilarity(paragraphSimilarityCounter / checkParagraphList.size());
        checkTask.setSimilarity(taskSimilarity);
        CheckTaskFinishEvent checkTaskFinishEvent = CheckTaskFinishEvent.builder()
                .checkTask(checkTask)
                .build();
        this.sendMqService.finishTask(checkTaskFinishEvent);
//        this.checkTaskService.finishTask(checkTask);
    }


    /**
     * 设置段落的相似度,并将结果设置为结束
     */
    void finishCheckParagraph(CheckParagraph checkParagraph, Double similarity) {
        checkParagraph.setSimilarity(similarity);
        checkParagraph.setStatus(CheckSentenceStatus.DONE.getValue());
    }

    /**
     * 设置句子的相似度,并将结果设置为结束
     */
    void finishCheckSentence(CheckSentence checkSentence, Double similarity) {
        checkSentence.setSimilarity(similarity);
        checkSentence.setStatus(CheckSentenceStatus.DONE.getValue());
    }
}
