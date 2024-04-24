package com.eva.check.service.support.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eva.check.common.constant.CacheConstant;
import com.eva.check.common.enums.CheckReportStatus;
import com.eva.check.common.enums.CompressType;
import com.eva.check.common.util.JacksonUtil;
import com.eva.check.mapper.CheckReportMapper;
import com.eva.check.pojo.CheckParagraph;
import com.eva.check.pojo.CheckReport;
import com.eva.check.pojo.CheckRequest;
import com.eva.check.pojo.CheckTask;
import com.eva.check.pojo.converter.ReportConverter;
import com.eva.check.pojo.dto.PaperResult;
import com.eva.check.pojo.vo.CheckReportContentDTO;
import com.eva.check.pojo.vo.SimilarPaperVO;
import com.eva.check.service.core.SimilarPaperService;
import com.eva.check.service.support.*;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 针对表【check_report(检测报告)】的数据库操作Service实现
 *
 * @author zengsl
 * @date 2023-11-30 15:33:50
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CheckReportServiceImpl extends ServiceImpl<CheckReportMapper, CheckReport> implements CheckReportService {

    private final CheckRequestService checkRequestService;
    private final CheckTaskService checkTaskService;
    private final SimilarPaperService similarPaperService;
    private final CheckPaperService checkPaperService;
    private final CheckParagraphService checkParagraphService;

    @Transactional(readOnly = true)
    @Override
    public CheckReport getByCheckNo(String checkNo) {
        LambdaQueryWrapper<CheckReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CheckReport::getCheckNo, checkNo);
        queryWrapper.orderByDesc(CheckReport::getUpdateTime);
        List<CheckReport> checkReports = this.baseMapper.selectList(queryWrapper);
        return CollUtil.isEmpty(checkReports) ? null : checkReports.get(0);
    }

    @Cacheable(value = CacheConstant.REPORT_CONTENT_DTO_CACHE_KEY, key = "#checkNo")
    @Transactional(readOnly = true)
    @Override
    public CheckReportContentDTO getCheckReportContent(String checkNo) {
        String reportContent = this.getReportContent(checkNo);
        return JacksonUtil.string2Obj(reportContent, new TypeReference<>() {});
    }

//    @Cacheable(value = CacheConstant.REPORT_CONTENT_CACHE_KEY, key = "#checkNo")
    @Transactional(readOnly = true)
    @Override
    public String getReportContent(String checkNo) {
        LambdaQueryWrapper<CheckReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(CheckReport::getCheckNo, checkNo)
                .select(CheckReport::getContent);
        CheckReport checkReport = this.baseMapper.selectOne(queryWrapper);
        return checkReport == null ? null : checkReport.getContent();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void initCheckReport(Long checkId) {
        CheckRequest checkRequest = this.checkRequestService.getById(checkId);
        if (checkRequest == null) {
            log.info("checkId：{}，未找到对应的检测请求", checkId);
            return;
        }
        initCheckReport(checkRequest);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void initCheckReport(String checkNo) {
        CheckRequest checkRequest = this.checkRequestService.getByCheckNo(checkNo);
        if (checkRequest == null) {
            log.info("通过checkNo：{}，未找到对应的检测请求", checkNo);
            return;
        }
        initCheckReport(checkRequest);
    }

    private void initCheckReport(CheckRequest checkRequest) {
        // 构建检测报告内容
        CheckReportContentDTO reportContent = this.innerBuildCheckReportContent(checkRequest);
        // 构建初始化的CheckReport
        CheckReport checkReport = new CheckReport();
        checkReport.setCheckNo(checkRequest.getCheckNo())
                // 设置检测报告标题
                .setReportName(EscapeUtil.escape(checkRequest.getTitle()))
                .setStatus(CheckReportStatus.INIT.getValue())
                .setContent(JacksonUtil.obj2String(reportContent))
                // 设置压缩类型 下载时进行使用
                .setCompress(CompressType.ZIP.getValue());

        this.getBaseMapper().insert(checkReport);
    }


    private CheckReportContentDTO innerBuildCheckReportContent(CheckRequest checkRequest) {
        CheckTask contentCheckTask = this.checkTaskService.findContentCheckTask(checkRequest.getCheckNo());
        PaperResult paperResult = this.similarPaperService.assemblePaperResult(contentCheckTask.getTaskId());
        List<SimilarPaperVO> allSimilarPaperList = this.checkPaperService.getAllSimilarPaper(contentCheckTask.getPaperId());
        List<CheckParagraph> checkParagraphList = this.checkParagraphService.getByTaskId(contentCheckTask.getTaskId());
        AtomicReference<Integer> wordCount = new AtomicReference<>(0);
        AtomicReference<Integer> sentenceCount = new AtomicReference<>(0);
        checkParagraphList.forEach(checkParagraph -> {
            wordCount.updateAndGet(v -> v + checkParagraph.getWordCount());
            sentenceCount.updateAndGet(v -> v + checkParagraph.getSentenceCount());
        });

        return CheckReportContentDTO.builder()
                .finalSimilarity(NumberUtil.decimalFormat("#.##%", checkRequest.getSimilarity()))
                .checkRequest(checkRequest)
                .wordCount(wordCount.get())
                .sentenceCount(sentenceCount.get())
                .contentCheckTask(contentCheckTask)
                .reportParagraphs(ReportConverter.INSTANCE.paperResult2paragraphVO(paperResult))
                .similarSentenceResultMap(ReportConverter.INSTANCE.paperResult2SentenceMap(paperResult))
                .allSimilarPaperList(allSimilarPaperList)
                .isDownload(true)
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public CheckReportContentDTO buildCheckReportContent(CheckRequest checkRequest) {
        return innerBuildCheckReportContent(checkRequest);
    }

}




