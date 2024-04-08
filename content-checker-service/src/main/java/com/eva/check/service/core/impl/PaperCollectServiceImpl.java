package com.eva.check.service.core.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.id.NanoId;
import com.eva.check.common.enums.DataType;
import com.eva.check.common.enums.PaperErrorCode;
import com.eva.check.common.exception.SystemException;
import com.eva.check.common.util.EnumUtils;
import com.eva.check.common.util.ParagraphUtil;
import com.eva.check.common.util.SimHashUtil;
import com.eva.check.common.util.TextUtil;
import com.eva.check.pojo.*;
import com.eva.check.pojo.converter.PaperCollectConverter;
import com.eva.check.pojo.dto.PaperAddReq;
import com.eva.check.service.core.PaperCollectService;
import com.eva.check.service.core.PaperCoreService;
import com.eva.check.service.support.*;
import lombok.RequiredArgsConstructor;
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
        checkParams(paperAddReq);
        PaperInfo paperInfo = PaperCollectConverter.INSTANCE.paperAddReq2Info(paperAddReq);
        // 生成论文编号
        String paperNo = StringUtils.hasText(paperInfo.getPaperNo()) ? paperInfo.getPaperNo() : NanoId.randomNanoId();
        paperInfo.setPaperNo(paperNo);

        // 生成指纹
        SimHashUtil.SimHash simHash = ParagraphUtil.buildFingerprint2(paperInfo.getContent());
        paperInfo.setHash(simHash.getSimHash());
        paperInfo.setHash1(simHash.getSimHash1());
        paperInfo.setHash2(simHash.getSimHash2());
        paperInfo.setHash3(simHash.getSimHash3());
        paperInfo.setHash4(simHash.getSimHash4());
        // 保存论文基本的信息
        boolean save = paperInfoService.save(paperInfo);
        List<PaperExt> paperExtList = PaperCollectConverter.INSTANCE.paperAddReq2Ext(paperAddReq.getPaperExtList(), paperInfo.getPaperId());
        // 保存论文的扩展信息
        boolean saveBatch = paperExtService.saveBatch(paperExtList);
        boolean isSaveSuccess = save && (CollectionUtil.isEmpty(paperExtList) || saveBatch);

        // TODO 直接换成手动ID？

        // TODO 主数据保存成功之后开始 多线程处理文本数据
        if (!isSaveSuccess) {
            throw new SystemException(PaperErrorCode.SAVE_FAIL);
        }

        // 如果文本为空，直接返回，不进行下面的文本分词等相关逻辑
        if (!StringUtils.hasText(paperAddReq.getContent())) {
            return paperNo;
        }
        // 发起一个文章的文本处理任务 paperInfo
        DataType dataType = EnumUtils.getEnumByValue(paperInfo.getDataType(), DataType.class);

        // TODO 目前段落和句子没有什么很大作用
        // 【生成段落】 TODO 这里先考虑只有一段的情况
        PaperParagraph paperParagraph = PaperParagraph.builder()
                .paperId(paperInfo.getPaperId())
                .paragraphNum(1L)
                .content(paperInfo.getContent())
                .paperNo(paperInfo.getPaperNo())
                .hash(simHash.getSimHash())
                .hash1(simHash.getSimHash1())
                .hash2(simHash.getSimHash2())
                .hash3(simHash.getSimHash3())
                .hash4(simHash.getSimHash4())
                .build();
        paperParagraph.setCreateTime(LocalDateTime.now());
        paperParagraph.setUpdateTime(LocalDateTime.now());
        boolean save1 = paperParagraphService.save(paperParagraph);

        this.paperCoreService.collectParagraph(paperParagraph);

        String paragraphContent = paperInfo.getContent();
        // 如果 paragraphContent 长度大与X 则开始分句
        // 【生成句子】
        List<String> sentenceList = TextUtil.smartSplitSentence(paragraphContent);
        AtomicLong num = new AtomicLong();
        sentenceList.forEach(sentence -> {
            // 文本处理 + 分词
            String newSentence = TextUtil.pretreatment(sentence);
            // 分词 + 去除停顿词
            List<String> newKeywordList = TextUtil.segmentAndRemoveStopWord(newSentence);
            long hash = SimHashUtil.hash(newKeywordList);
            List<String> sentenceSimHashList = SimHashUtil.splitSimHash(hash);

            // 汇总整篇文章的关键词
//            paperKeywordList.addAll(newKeywordList);
            // 存句子信息
            PaperSentence paperSentence = PaperSentence.builder()
                    .paragraphId(paperParagraph.getParagraphId())
                    .sentenceNum(num.incrementAndGet())
                    .originContent(sentence)
                    .content(newSentence)
                    .hash(hash)
                    .hash1(sentenceSimHashList.get(0))
                    .hash2(sentenceSimHashList.get(1))
                    .hash3(sentenceSimHashList.get(2))
                    .hash4(sentenceSimHashList.get(3))
                    .build();
            boolean save2 = paperSentenceService.save(paperSentence);
            AtomicLong num2 = new AtomicLong();
            newKeywordList.forEach(keyword -> {
                // 存关键词信息
                PaperToken paperToken = PaperToken.builder()
                        .tokenNum(num2.incrementAndGet())
                        .sentenceId(paperSentence.getSentenceId())
                        .paragraphId(paperParagraph.getParagraphId())
                        .content(keyword)
                        .build();
                boolean save3 = paperTokenService.save(paperToken);
                Assert.isTrue(save3, SystemException.withExSupplier(PaperErrorCode.SAVE_FAIL));
            });

            Assert.isTrue(save2, SystemException.withExSupplier(PaperErrorCode.SAVE_FAIL));
        });


        // TODO 先不考虑扩展信息
        return paperInfo.getPaperNo();
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
