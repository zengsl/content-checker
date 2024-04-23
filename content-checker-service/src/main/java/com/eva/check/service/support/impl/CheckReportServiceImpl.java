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
import com.eva.check.pojo.CheckReport;
import com.eva.check.pojo.CheckRequest;
import com.eva.check.pojo.CheckTask;
import com.eva.check.pojo.converter.ReportConverter;
import com.eva.check.pojo.dto.PaperResult;
import com.eva.check.pojo.vo.CheckReportContentDTO;
import com.eva.check.pojo.vo.SimilarPaperVO;
import com.eva.check.service.core.SimilarPaperService;
import com.eva.check.service.support.CheckPaperService;
import com.eva.check.service.support.CheckReportService;
import com.eva.check.service.support.CheckRequestService;
import com.eva.check.service.support.CheckTaskService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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

    @Transactional(readOnly = true)
    @Override
    public CheckReport getByCheckNo(String checkNo) {
        LambdaQueryWrapper<CheckReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CheckReport::getCheckNo, checkNo);
        queryWrapper.orderByDesc(CheckReport::getUpdateTime);
        List<CheckReport> checkReports = this.baseMapper.selectList(queryWrapper);
        return CollUtil.isEmpty(checkReports) ? null : checkReports.get(0);
    }

    @Cacheable(value = CacheConstant.REPORT_CONTENT_MAP_CACHE_KEY, key = "#checkNo")
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


        CheckReportContentDTO checkReportContentDTO = CheckReportContentDTO.builder()
                .finalSimilarity(NumberUtil.decimalFormat("#.##%", checkRequest.getSimilarity()))
                .checkRequest(checkRequest)
                .contentCheckTask(contentCheckTask)
                .reportParagraphs(ReportConverter.INSTANCE.paperResult2paragraphVO(paperResult))
                .similarSentenceResultMap(ReportConverter.INSTANCE.paperResult2SentenceMap(paperResult))
                .allSimilarPaperList(allSimilarPaperList)
                .isDownload(true)
                .build();

        /*Map<String, Object> params = Maps.newHashMap();
        params.put("finalSimilarity", NumberUtil.decimalFormat("#.##%", checkRequest.getSimilarity()));
        params.put("checkRequest", checkRequest);
        params.put("contentCheckTask", contentCheckTask);
        params.put("reportParagraphs", ReportConverter.INSTANCE.paperResult2paragraphVO(paperResult));
        params.put("similarSentenceResultMap", ReportConverter.INSTANCE.paperResult2SentenceMap(paperResult));
        params.put("allSimilarPaperList", allSimilarPaperList);
        params.put("isDownload", true);*/
        return checkReportContentDTO;
    }

    @Transactional(readOnly = true)
    @Override
    public CheckReportContentDTO buildCheckReportContent(CheckRequest checkRequest) {
        return innerBuildCheckReportContent(checkRequest);
    }

}




