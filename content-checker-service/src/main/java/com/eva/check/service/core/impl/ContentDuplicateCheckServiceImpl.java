package com.eva.check.service.core.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.eva.check.common.constant.ContentCheckConstant;
import com.eva.check.common.enums.CheckParagraphPairStatus;
import com.eva.check.common.enums.CheckSentencePairStatus;
import com.eva.check.common.enums.CheckSentenceStatus;
import com.eva.check.common.enums.DataType;
import com.eva.check.common.util.SimilarUtil;
import com.eva.check.pojo.*;
import com.eva.check.pojo.dto.SimilarPaperParagraph;
import com.eva.check.service.config.CheckProperties;
import com.eva.check.service.core.DuplicateCheckService;
import com.eva.check.service.core.PaperCoreService;
import com.eva.check.service.flow.IContentCheckTaskBaseFlow;
import com.eva.check.service.flow.enums.ContentCheckState;
import com.eva.check.service.support.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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
    private final PaperTokenService paperTokenService;
    private final CheckSentenceService checkSentenceService;
    private final CheckSentencePairService checkSentencePairService;
    private final CheckParagraphService checkParagraphService;
    private final CheckParagraphPairService checkParagraphPairService;
    private final CheckPaperService checkPaperService;
    private final CheckPaperPairService checkPaperPairService;
    private final CheckProperties checkProperties;

    private IContentCheckTaskBaseFlow contentCheckTaskFlow;

    @Autowired
    @Lazy
    public void setContentCheckTaskFlow(IContentCheckTaskBaseFlow contentCheckTaskFlow) {
        this.contentCheckTaskFlow = contentCheckTaskFlow;
    }

    /**
     * 快速查找疑似项目，生成待比对数据对
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void findSimilarParagraph(CheckTask checkTask) {
        if (!DataType.FULL_TEXT.getValue().equals(checkTask.getCheckType())) {
            log.warn("非全文比对类型，不执行预检查");
            return;
        }

        List<CheckParagraph> checkParagraphList = this.checkParagraphService.getByTaskId(checkTask.getTaskId());
        if (CollectionUtil.isEmpty(checkParagraphList)) {
            log.warn("无论文段落，不执行预检查");
            this.contentCheckTaskFlow.processCancel(checkTask);
            return;
        }

        List<CheckParagraphPair> checkParagraphListPair = Lists.newArrayListWithCapacity(16);
        checkParagraphList.forEach(checkParagraph -> {
            PaperParagraph paperParagraph = PaperParagraph.builder().paperNo(checkParagraph.getPaperNo())
                    .paperId(checkParagraph.getPaperId())
                    .paragraphId(checkParagraph.getParagraphId())
                    .content(checkParagraph.getContent())
                    .hash(checkParagraph.getHash())
                    .hash1(checkParagraph.getHash1())
                    .hash2(checkParagraph.getHash2())
                    .hash3(checkParagraph.getHash3())
                    .hash4(checkParagraph.getHash4())
                    .build();
            // 快速判断是否重复，获取重复论文列表
            List<SimilarPaperParagraph> similarPaperList = this.paperCoreService.findSimilarPaperParagraph(paperParagraph);
            // 目前SimHash这种方法无法快速查找的可能相似的文档
            // List<SimilarPaperParagraph> similarPaperList = this.paperSimHashIndexService.findSimilarPaper(paperParagraph);
            if (CollUtil.isEmpty(similarPaperList)) {
                log.info("未找到相似论文, paperId:{} paraId:{}", paperParagraph.getPaperId(), paperParagraph.getParagraphId());
                return;
            }
            log.info(" 找到相似论文{}份", similarPaperList.size());
            similarPaperList.forEach(e -> {
                CheckParagraphPair list = CheckParagraphPair.builder()
                        .taskId(checkTask.getTaskId())
                        .checkPaperId(checkParagraph.getPaperId())
                        .targetPaperId(e.getPaperId())
                        .checkParaId(checkParagraph.getParagraphId())
                        .targetParaId(e.getParagraphId())
                        .status(CheckParagraphPairStatus.INIT.getValue())
                        .similarity(ContentCheckConstant.SIMILARITY_INIT)
                        .build();
                checkParagraphListPair.add(list);
            });
        });

        // 查找相似论文
        if (CollectionUtil.isEmpty(checkParagraphListPair)) {
            // 如果没有相似论文，则直接结束当前任务并设置相似度为0
            checkTask.setSimilarity(ContentCheckConstant.SIMILARITY_ZERO);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    // 触发预检测任务事件
                    contentCheckTaskFlow.processFinish(checkTask);
                }
            });
        } else {
            // 生成比对段落对 check_paragraph_pair
            this.checkParagraphPairService.initCompareList(checkParagraphListPair);
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    contentCheckTaskFlow.processStateNext(checkTask, ContentCheckState.PRE_CHECK);
                }
            });

        }
    }

    /**
     * 以段落为单元，执行检测
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void doPragraphCheck(CheckTask checkTask) {
        StopWatch stopWatch = new StopWatch("doPragraphCheck");
        stopWatch.start("查询CheckParagraphPair");
        Map<Long, CheckPaperResult> checkPaperResultMap = Maps.newHashMap();
        // TODO 这里的校验可以用多线程进行
        List<CheckParagraphPair> checkParagraphPairList = this.checkParagraphPairService.getByTaskId(checkTask.getTaskId());
        stopWatch.stop();
        stopWatch.start("遍历checkParagraphPairList");
        // 遍历每一对需要比较的段落
        checkParagraphPairList.forEach(checkParagraphPair -> {
            // 获取待检测的段落包含的句子
            List<CheckSentence> checkSentenceList = this.checkSentenceService.getByParagraphId(checkParagraphPair.getCheckParaId());
            if (CollUtil.isEmpty(checkSentenceList)) {
                // 回写CheckParagraphPair
                checkParagraphPair.setSimilarity(0D);
                checkParagraphPair.setStatus(CheckParagraphPairStatus.DONE.getValue());
                return;
            }
            List<CheckSentencePair> checkSentencePairList = Lists.newArrayListWithCapacity(16);
            List<Long> paperSentenceList = this.paperSentenceService.getSentenceIdFromCache(checkParagraphPair.getTargetParaId());
            // 相似度累加器
            AtomicDouble similarityCounter = new AtomicDouble();
            // 待检测句子按照每一句进行处理
            checkSentenceList.forEach(checkSentence -> {
                // 计算词频  获取待检测句子的所有关键词词频
                Map<String, Float> checkSentenceWordFrequency = SimilarUtil.countWordFrequency(checkSentence.getContent());
                if (CollUtil.isEmpty(checkSentenceWordFrequency)) {
                    log.info("当前【检测句】没有找到对应的分词,不进行比对,checkSentenceId: {}", checkSentence.getSentenceId());
                    return;
                }
                paperSentenceList.forEach(targetPaperSentenceId -> {
                    // 获取并计算当前需要比对的句子词频
                    Map<String, Float> paperSentenceWordFrequency = this.paperTokenService.getWordFrequencyFromCache(targetPaperSentenceId);
                    // 计算相似度
                    double cosineSimilarity = SimilarUtil.getCosineSimilarity(checkSentenceWordFrequency, paperSentenceWordFrequency);
                    // 计算出的相似度是否大于等于阈值；相似度小于阈值，不进行相似度计算，但是需要保留比对的结果
                    boolean needCalculate = cosineSimilarity >= checkProperties.getSentenceSimilarityThreshold();
                    if (needCalculate) {
                        // 相似度累加
//                        similarityCounter += cosineSimilarity;
                        similarityCounter.addAndGet(cosineSimilarity);
                    }
                    // 计算每句的相似度以及相似的词。
                    CheckSentencePair checkSentencePair = CheckSentencePair.builder()
                            .taskId(checkTask.getTaskId())
                            .checkParaId(checkParagraphPair.getCheckParaId())
                            .targetParaId(checkParagraphPair.getTargetParaId())
                            .checkSentenceId(checkSentence.getSentenceId())
                            .targetSentenceId(targetPaperSentenceId)
                            .similarity(cosineSimilarity)
                            .status(CheckSentencePairStatus.DONE.getValue())
                            .build();
                    checkSentencePairList.add(checkSentencePair);
                });
            });
            // 设置每对段落的相似度，这里没有统计那些句子对没有相似度的情况
            if (checkSentencePairList.isEmpty()) {
                checkParagraphPair.setSimilarity(SimilarUtil.formatSimilarity(ContentCheckConstant.SIMILARITY_ZERO));
            } else {
                // 段落比对 相似度计算：相似度累加 / 检测句子数量 TODO
                checkParagraphPair.setSimilarity(SimilarUtil.formatSimilarity(similarityCounter.get() / checkSentenceList.size()));
            }
            checkParagraphPair.setStatus(CheckParagraphPairStatus.DONE.getValue());
            // 按照每一对CheckParagraphPair批量保存CheckSentencePair
            this.checkSentencePairService.saveBatch(checkSentencePairList);
            // 统计以paper_pair为单位的相似度数据
            CheckPaperResult checkPaperResult = checkPaperResultMap.computeIfAbsent(checkParagraphPair.getTargetPaperId(), k -> new CheckPaperResult(checkTask.getPaperId(), checkParagraphPair.getTargetPaperId(), 0, 0D));
            checkPaperResult.setParagraphCount(checkPaperResult.getParagraphCount() + 1);
            checkPaperResult.setSimilarCount(checkPaperResult.getSimilarCount() + checkParagraphPair.getSimilarity());

        });

        stopWatch.stop();
        stopWatch.start("汇总paper_pair的结果");

        // 汇总paper_pair的结果
        saveCheckPaperResult(checkTask.getPaperId(), checkTask.getTaskId(), checkPaperResultMap);
        stopWatch.stop();
        stopWatch.start("存储汇总paper_pair的结果");
        // 汇总段落检测对的结果
        this.checkParagraphPairService.updateBatchById(checkParagraphPairList);
        stopWatch.stop();
        log.info("【doPragraphCheck】方法执行结束, checkTaskId:{} ，耗时：{}s ，详情：{}", checkTask.getTaskId(), stopWatch.getTotalTimeSeconds(), stopWatch.prettyPrint());


        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                contentCheckTaskFlow.processStateNext(checkTask, ContentCheckState.PARAGRAPH_CHECK);
            }
        });

    }

    private void saveCheckPaperResult(Long paperId, Long taskId, Map<Long, CheckPaperResult> checkPaperResultMap) {
        if (CollectionUtils.isEmpty(checkPaperResultMap)) {
            return;
        }
        AtomicReference<Double> paperSimilarityCounter = new AtomicReference<>(0D);
        List<CheckPaperPair> checkPaperListPair = Lists.newArrayListWithCapacity(16);
        checkPaperResultMap.forEach(((aLong, checkPaperResult) -> {
            double paperSimilarity = checkPaperResult.getSimilarCount() / checkPaperResult.getParagraphCount();
            CheckPaperPair checkPaperPair = CheckPaperPair.builder().targetPaperId(checkPaperResult.getTargetPaperId())
                    .checkPaperId(checkPaperResult.getCheckPaperId()).taskId(taskId)
                    // 计算相似度
                    .similarity(SimilarUtil.formatSimilarity(paperSimilarity))
                    .build();
            checkPaperListPair.add(checkPaperPair);
            paperSimilarityCounter.updateAndGet(v -> v + paperSimilarity);
        }));
        this.checkPaperPairService.initCompareList(checkPaperListPair);

        // 更新check_paper
        this.checkPaperService.updateSimilarity(paperId
                , SimilarUtil.formatSimilarity(paperSimilarityCounter.get() / checkPaperResultMap.size()));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void collectResult(CheckTask checkTask) {

        List<CheckSentence> allCheckSentenceList = Lists.newArrayList();
        List<CheckParagraph> checkParagraphList = this.checkParagraphService.getByTaskId(checkTask.getTaskId());
        double paragraphSimilarityCounter = 0D;
        // 遍历检测段落
        for (CheckParagraph checkParagraph : checkParagraphList) {

            // 根据当前段落，查找所有检测的句子
            List<CheckSentence> checkSentenceList = this.checkSentenceService.getByParagraphId(checkParagraph.getParagraphId());
            if (CollUtil.isEmpty(checkSentenceList)) {
                finishCheckParagraph(checkParagraph, 0D);
                log.debug("checkParagraphList is empty, ParagraphId: {}", checkParagraph.getParagraphId());
                continue;
            }
            AtomicDouble sentenceSimilarityCounter = new AtomicDouble();
            // 遍历检测句子
            checkSentenceList.forEach(checkSentence -> {
                // 查找每个句子的检测对（当前句子与疑似句子）
                List<CheckSentencePair> checkSentencePairList = this.checkSentencePairService.getAllByCheckSentenceId(checkSentence.getSentenceId());
                if (CollUtil.isEmpty(checkSentencePairList)) {
                    finishCheckSentence(checkSentence, 0D);
                    log.debug("checkSentencePairList is empty, CheckSentenceId: {}", checkSentence.getSentenceId());
                    return;
                }
                // 所有疑似句子的算术平均数作为当前句子的相似度
//                double sentenceSimilarity = SimilarUtil.formatSimilarity(checkSentencePairList.stream().mapToDouble(CheckSentencePair::getSimilarity).sum() / checkSentencePairList.size());
                // 所有疑似句子的最大值作为当前句子的相似度
                double sentenceSimilarity = SimilarUtil.formatSimilarity(checkSentencePairList.stream().mapToDouble(CheckSentencePair::getSimilarity).max().orElse(0));
                finishCheckSentence(checkSentence, sentenceSimilarity);
                // 累加每个句子的相似度
                sentenceSimilarityCounter.addAndGet(sentenceSimilarity);
            });

            // 收集所有的句子，以便最终批量更新结果
            allCheckSentenceList.addAll(checkSentenceList);
            // 设置段落的相似度,并将结果设置为结束
            double similarity = 0D;
            if (checkSentenceList.isEmpty()) {
                log.warn("异常数据，检测句子数为空。可能数据库中数据已经清理，但是MQ中进行了重新投递，taskId:{}， ParagraphId:{}", checkTask.getTaskId(), checkParagraph.getParagraphId());
            } else {
                similarity = sentenceSimilarityCounter.doubleValue() / checkSentenceList.size();
            }
            finishCheckParagraph(checkParagraph, SimilarUtil.formatSimilarity(similarity));
            // 统计所有段落的综合相似度
            paragraphSimilarityCounter += checkParagraph.getSimilarity();
            // 计算每段文本与单一疑似段落的相似度结果 生成check_paper_pair
        }

        // 批量更新【检测句子】的结果
        this.checkSentenceService.updateBatchById(allCheckSentenceList);
        // 批量更新【检测段落】的结果
        this.checkParagraphService.updateBatchById(checkParagraphList);
        // 段落相似度综合
        if (checkParagraphList.isEmpty()) {
            log.warn("异常数据，检测段落数为空。可能数据库中数据已经清理，但是MQ中进行了重新投递，taskId:{}", checkTask.getTaskId());
        } else {
            double taskSimilarity = SimilarUtil.formatSimilarity(paragraphSimilarityCounter / checkParagraphList.size());
            checkTask.setSimilarity(taskSimilarity);
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                contentCheckTaskFlow.processStateNext(checkTask, ContentCheckState.COLLECT_RESULT);
            }
        });

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

    @Data
    static
    class CheckPaperResult {
        private Long checkPaperId;
        private Long targetPaperId;
        private Integer paragraphCount;
        private Double similarCount;

        public CheckPaperResult(Long checkPaperId, Long targetPaperId, Integer paragraphCount, Double similarCount) {
            this.checkPaperId = checkPaperId;
            this.targetPaperId = targetPaperId;
            this.paragraphCount = paragraphCount;
            this.similarCount = similarCount;
        }
    }

}
