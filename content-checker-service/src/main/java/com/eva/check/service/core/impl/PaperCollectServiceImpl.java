package com.eva.check.service.core.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.StopWatch;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.id.NanoId;
import com.eva.check.common.constant.ContentCheckConstant;
import com.eva.check.common.enums.PaperErrorCode;
import com.eva.check.common.exception.SystemException;
import com.eva.check.common.util.ParagraphUtil;
import com.eva.check.common.util.SimHashUtil;
import com.eva.check.common.util.TextUtil;
import com.eva.check.pojo.*;
import com.eva.check.pojo.converter.PaperCollectConverter;
import com.eva.check.pojo.dto.PaperAddReq;
import com.eva.check.service.core.PaperCollectService;
import com.eva.check.service.core.PaperCoreService;
import com.eva.check.service.support.*;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 论文收集服务
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaperCollectServiceImpl implements PaperCollectService {

    private final PaperInfoService paperInfoService;
    private final PaperParagraphService paperParagraphService;
    private final PaperSentenceService paperSentenceService;
    private final PaperTokenService paperTokenService;
    private final PaperExtService paperExtService;
    private final PaperCoreService paperCoreService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String addNewPaper(PaperAddReq paperAddReq) {
        StopWatch stopWatch = new StopWatch("文章录入任务");
        stopWatch.start("准备工作");
        checkParams(paperAddReq);
        PaperInfo paperInfo = PaperCollectConverter.INSTANCE.paperAddReq2Info(paperAddReq);
        // 生成论文编号
        String paperNo = StringUtils.hasText(paperInfo.getPaperNo()) ? paperInfo.getPaperNo() : NanoId.randomNanoId();
        paperInfo.setPaperNo(paperNo);
        // 目前只当作一段考虑
        paperInfo.setParaCount(1);
        paperInfo.setWordCount(TextUtil.countWord(paperInfo.getContent()));

        stopWatch.stop();
//        stopWatch.start("生成指纹");

        // 生成指纹
        /*SimHashUtil.SimHash simHash = ParagraphUtil.buildFingerprint2(paperInfo.getContent());
        paperInfo.setHash(simHash.getSimHash());
        paperInfo.setHash1(simHash.getSimHash1());
        paperInfo.setHash2(simHash.getSimHash2());
        paperInfo.setHash3(simHash.getSimHash3());
        paperInfo.setHash4(simHash.getSimHash4());*/

//        stopWatch.stop();
        stopWatch.start("文章、段落存储工作");

        // 保存论文基本的信息
        boolean save = paperInfoService.save(paperInfo);
        List<PaperExt> paperExtList = PaperCollectConverter.INSTANCE.paperAddReq2Ext(paperAddReq.getPaperExtList(), paperInfo.getPaperId());
        // 保存论文的扩展信息
        boolean saveBatch = paperExtService.saveBatch(paperExtList);
        boolean isSaveSuccess = save && (CollectionUtil.isEmpty(paperExtList) || saveBatch);

        if (!isSaveSuccess) {
            throw new SystemException(PaperErrorCode.SAVE_FAIL);
        }

        // 如果文本为空，直接返回，不进行下面的文本分词等相关逻辑
        if (!StringUtils.hasText(paperAddReq.getContent())) {
            return paperNo;
        }
        // 发起一个文章的文本处理任务 paperInfo
//        DataType dataType = EnumUtils.getEnumByValue(paperInfo.getDataType(), DataType.class);

        // 【生成段落】 目前按照一段的情况进行处理
        PaperParagraph paperParagraph = PaperParagraph.builder()
                .paperId(paperInfo.getPaperId())
                .paragraphNum(1L)
                .content(paperInfo.getContent())
                .paperNo(paperInfo.getPaperNo())
              /*  .hash(simHash.getSimHash())
                .hash1(simHash.getSimHash1())
                .hash2(simHash.getSimHash2())
                .hash3(simHash.getSimHash3())
                .hash4(simHash.getSimHash4())*/
                .build();
        paperParagraph.setCreateTime(LocalDateTime.now());
        paperParagraph.setUpdateTime(LocalDateTime.now());
        // 设置字数
        paperParagraph.setWordCount(TextUtil.countWord(paperInfo.getContent()));
        String paragraphContent = paperInfo.getContent();
        // 如果 paragraphContent 长度大与X 则开始分句
        // 【生成句子】
        List<String> sentenceList = TextUtil.smartSplitSentence(paragraphContent);
        // 设置句子数量
        paperParagraph.setSentenceCount(sentenceList.size());

        boolean savePara = paperParagraphService.save(paperParagraph);
        if (!savePara) {
            throw new SystemException(PaperErrorCode.SAVE_FAIL);
        }

        stopWatch.stop();
        stopWatch.start("Es收录段落");

        this.paperCoreService.collectParagraph(paperParagraph);

        stopWatch.stop();
        log.info("段落{}基本信息: {}, 总字数：{}", paperParagraph.getParagraphId(), paperParagraph.getParagraphId(), paperParagraph.getWordCount());
        stopWatch.start("处理句子handleSentence");
        handleSentence(sentenceList, paperParagraph.getParagraphId());
        stopWatch.stop();
        log.info("【addNewPaper】方法执行结束 ，耗时：{}s ，详情：{}", stopWatch.getTotalTimeSeconds(), stopWatch.prettyPrint());
        // TODO 先不考虑扩展信息
        return paperInfo.getPaperNo();
    }

    private void handleSentence(List<String> sentenceList, Long paraId) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("批量处理句子，段落paraId:" + paraId);
        AtomicLong num = new AtomicLong();
        List<PaperSentence> paperSentenceList = Lists.newArrayListWithCapacity(sentenceList.size());
        List<List<String>> paperSentenceKeywordList = Lists.newArrayListWithCapacity(sentenceList.size());
        sentenceList.stream().parallel().forEach(sentence -> {
            // 文本处理 + 分词
            String newSentence = TextUtil.pretreatment(sentence);
            // 分词 + 去除停顿词
            List<String> newKeywordList = TextUtil.segmentAndRemoveStopWord(newSentence);
           /* long hash = SimHashUtil.hash(newKeywordList);
            List<String> sentenceSimHashList = SimHashUtil.splitSimHash(hash);*/

            // 存句子信息
            PaperSentence paperSentence = PaperSentence.builder()
                    .paragraphId(paraId)
                    .sentenceNum(num.incrementAndGet())
                    .originContent(sentence)
                    .content(newSentence)
                    .wordCount(TextUtil.countWord(sentence))
                    /*.hash(hash)
                    .hash1(sentenceSimHashList.get(0))
                    .hash2(sentenceSimHashList.get(1))
                    .hash3(sentenceSimHashList.get(2))
                    .hash4(sentenceSimHashList.get(3))*/
                    .build();

            paperSentenceList.add(paperSentence);
            paperSentenceKeywordList.add(newKeywordList);

        });
        // 先存储句子，生成sentenceId，以保证PaperToken可以关联到对应的句子
        boolean sentenceSave = paperSentenceService.saveBatch(paperSentenceList, ContentCheckConstant.SENTENCE_BATCH_SIZE);
        Assert.isTrue(sentenceSave, SystemException.withExSupplier(PaperErrorCode.SAVE_FAIL));

        AtomicLong tokenNum = new AtomicLong();
        List<PaperToken> paperTokenList = Lists.newArrayList();
        for (int i = 0, size = paperSentenceKeywordList.size(); i < size; i++) {
            List<String> keywordList = paperSentenceKeywordList.get(i);
            PaperSentence paperSentence = paperSentenceList.get(i);
            for (String keyword : keywordList) {
                // 存关键词信息
                PaperToken paperToken = PaperToken.builder()
                        .tokenNum(tokenNum.incrementAndGet())
                        .sentenceId(paperSentence.getSentenceId())
                        .paragraphId(paraId)
                        .content(keyword)
                        .build();
                paperTokenList.add(paperToken);
            }
        }

        boolean tokenSave = paperTokenService.saveBatch(paperTokenList, ContentCheckConstant.SENTENCE_BATCH_SIZE);
        Assert.isTrue(tokenSave, SystemException.withExSupplier(PaperErrorCode.SAVE_FAIL));
        stopWatch.stop();
        log.info("待处理句子总量：{}", sentenceList.size());
        log.info("待处理分词总量：{}", paperTokenList.size());
        log.info("段落paraId:{} 批量处理句子与句子分词耗时：{}s", paraId, stopWatch.getTotalTimeSeconds());
    }

    private static void checkParams(PaperAddReq paperAddReq) {
        Assert.notNull(paperAddReq, SystemException.withExSupplier(PaperErrorCode.PARAM_INVALID));
        Assert.notNull(paperAddReq.getDataSource(), SystemException.withExSupplier(PaperErrorCode.PARAM_INVALID));
        Assert.notNull(paperAddReq.getDataType(), SystemException.withExSupplier(PaperErrorCode.PARAM_INVALID));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removePaperByNo(String paperNo) {
        this.paperExtService.removeByPaperNo(paperNo);
        this.paperInfoService.removeByPaperNo(paperNo);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void removePaperById(String paperId) {
        this.paperExtService.removeById(paperId);
        this.paperInfoService.removeById(paperId);
    }


}
