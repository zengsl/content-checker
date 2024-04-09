package com.eva.check.service.core.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.id.NanoId;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.eva.check.common.enums.*;
import com.eva.check.common.exception.SystemException;
import com.eva.check.common.util.FileUtil;
import com.eva.check.pojo.CheckPaper;
import com.eva.check.pojo.CheckReport;
import com.eva.check.pojo.CheckRequest;
import com.eva.check.pojo.CheckTask;
import com.eva.check.pojo.converter.PaperCheckConverter;
import com.eva.check.pojo.converter.PaperCollectConverter;
import com.eva.check.pojo.converter.ReportConverter;
import com.eva.check.pojo.dto.CheckReportDTO;
import com.eva.check.pojo.dto.PaperAddReq;
import com.eva.check.pojo.dto.PaperCheckReq;
import com.eva.check.pojo.dto.PaperResult;
import com.eva.check.pojo.vo.SimilarPaperVO;
import com.eva.check.service.config.CheckProperties;
import com.eva.check.service.core.PaperCheckService;
import com.eva.check.service.core.PaperCollectService;
import com.eva.check.service.core.SimilarPaperService;
import com.eva.check.service.event.CheckTaskStartEvent;
import com.eva.check.service.mq.producer.SendMqService;
import com.eva.check.service.support.CheckPaperService;
import com.eva.check.service.support.CheckReportService;
import com.eva.check.service.support.CheckRequestService;
import com.eva.check.service.support.CheckTaskService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.ISpringTemplateEngine;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 论文检测服务
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PaperCheckServiceImpl implements PaperCheckService {

    private final PaperCollectService paperCollectService;
    private final CheckRequestService checkRequestService;
    private final CheckTaskService checkTaskService;
    private final CheckReportService checkReportService;
    private final CheckPaperService checkPaperService;
    private final SimilarPaperService similarPaperService;
    private final SendMqService sendMqService;
    private final CheckProperties checkProperties;
    private final ISpringTemplateEngine templateEngine;

    @Override
    public String createPaperCheck(PaperCheckReq paperCheckReq) throws SystemException {
        checkParams(paperCheckReq);
        // 拆解验证任务 先只处理正文检测
        CheckRequest checkRequest = PaperCheckConverter.INSTANCE.paperCheckReq2CheckReq(paperCheckReq);
        String checkNo = StringUtils.hasText(paperCheckReq.getCheckNo()) ? paperCheckReq.getCheckNo() : NanoId.randomNanoId();
        // 设置checkNo
        checkRequest.setCheckNo(checkNo);
        checkRequest.setStatus(CheckReqStatus.INIT.getValue());
        // 保存check_request
        boolean save = this.checkRequestService.save(checkRequest);
        // 生成checkPaper
        CheckPaper checkPaper = PaperCheckConverter.INSTANCE.paperCheckReq2CheckPaper(paperCheckReq);
        checkPaper.setCheckId(checkRequest.getCheckId());
        this.checkPaperService.save(checkPaper);

        // 按需生成task
        List<CheckTask> checkTaskList = Lists.newArrayListWithCapacity(16);
        if (StringUtils.hasText(paperCheckReq.getContent())) {
            CheckTask contentCheckTask = new CheckTask();
            contentCheckTask.setCheckId(checkRequest.getCheckId())
                    .setCheckNo(checkRequest.getCheckNo())
                    .setPaperNo(checkRequest.getPaperNo())
                    .setPaperId(checkPaper.getPaperId())
                    .setCheckType(DataType.FULL_TEXT.getValue())
                    .setContent(paperCheckReq.getContent())
                    .setStatus(CheckTaskStatus.INIT.getValue());
            checkTaskList.add(contentCheckTask);
        }

        // TODO 目前不考虑
        if (StringUtils.hasText(paperCheckReq.getContent())) {
            CheckTask titleCheckTask = new CheckTask();
            titleCheckTask.setCheckId(checkRequest.getCheckId())
                    .setCheckNo(checkRequest.getCheckNo())
                    .setPaperNo(checkRequest.getPaperNo())
                    .setPaperId(checkPaper.getPaperId())
                    .setCheckType(DataType.TITLE.getValue())
                    .setContent(paperCheckReq.getTitle())
                    .setStatus(CheckTaskStatus.INIT.getValue());
            checkTaskList.add(titleCheckTask);
        }

        checkTaskService.saveBatch(checkTaskList);
        checkRequest.setTaskNum(checkTaskList.size());
        checkRequest.setStatus(CheckReqStatus.DOING.getValue());
        this.checkRequestService.updateById(checkRequest);

        // 将任务推送MQ 进行异步处理 TODO
        CheckTaskStartEvent checkTaskStartEvent = CheckTaskStartEvent.builder()
                .checkTasks(checkTaskList)
                .checkId(checkRequest.getCheckId())
                .taskNum(checkRequest.getTaskNum())
                .build();
        sendMqService.startTask(checkTaskStartEvent);
        return checkRequest.getCheckNo();
    }

    private static void checkParams(PaperCheckReq paperCheckReq) {
        boolean b = paperCheckReq == null || CollectionUtil.isEmpty(paperCheckReq.getPaperExtList()) && StrUtil.isBlank(paperCheckReq.getContent()) && StrUtil.isBlank(paperCheckReq.getTitle());
        if (b) {
            throw new SystemException(PaperErrorCode.PARAM_INVALID);
        }
    }

    @Override
    public String createPaperCheckAndCollect(PaperCheckReq paperCheckReq) throws SystemException {
        PaperAddReq paperAddReq = PaperCollectConverter.INSTANCE.check2AddReq(paperCheckReq);

        // 生成论文编号
        String paperNo = StringUtils.hasText(paperAddReq.getPaperNo()) ? paperAddReq.getPaperNo() : NanoId.randomNanoId();
        // 入库前设置编号
        paperAddReq.setPaperNo(paperNo);
        // 收录至文档库
        // TODO 可以异步
        paperCollectService.addNewPaper(paperAddReq);
        // 检测前设置编号，以防检测时进行同一文件检测
        paperCheckReq.setPaperNo(paperNo);
        return this.createPaperCheck(paperCheckReq);
    }

    @Override
    public CheckRequest getPaperCheckResult(String checkNo) throws SystemException {
        return this.checkRequestService.getByCheckNo(checkNo);
    }

    @Override
    public CheckReportDTO getPaperCheckReport(String checkNo) throws SystemException {
        CheckReportDTO checkReportDTO = new CheckReportDTO();
        CheckReport checkReport = this.checkReportService.getByCheckNo(checkNo);
        if (checkReport == null) {
            return checkReportDTO;
        }
        checkReportDTO.setReportName(checkReport.getReportName());
        checkReportDTO.setStatus(checkReport.getStatus());
        checkReportDTO.setFilePath(checkReport.getFilePath());
        checkReportDTO.setFileCode(checkReport.getFileCode());
        return checkReportDTO;
    }

    @Override
    public Map<String, Object> getPaperCheckReportParams(String checkNo) throws SystemException {
        CheckRequest checkRequest = this.checkRequestService.getByCheckNo(checkNo);
        if (checkRequest == null) {
            return null;
        }
        CheckTask contentCheckTask = this.checkTaskService.findContentCheckTask(checkNo);
        PaperResult paperResult = this.similarPaperService.assemblePaperResult(contentCheckTask.getTaskId());
        List<SimilarPaperVO> allSimilarPaperList = this.checkPaperService.getAllSimilarPaper(contentCheckTask.getPaperId());
        Map<String, Object> params = Maps.newHashMap();
        params.put("finalSimilarity", NumberUtil.decimalFormat("#.##%", checkRequest.getSimilarity()));
        params.put("checkRequest", checkRequest);
        params.put("contentCheckTask", contentCheckTask);
        params.put("reportParagraphs", ReportConverter.INSTANCE.paperResult2paragraphVO(paperResult));
        params.put("similarSentenceResultMap", ReportConverter.INSTANCE.paperResult2SentenceMap(paperResult));
        params.put("allSimilarPaperList", allSimilarPaperList);
        params.put("isDownload", true);
        return params;
    }

    @Override
    public CheckReportDTO getOrCreateReportFile(String checkNo) throws SystemException {
        CheckReport checkReport = this.checkReportService.getByCheckNo(checkNo);
        if (checkReport == null) {
            checkReport = generateReportFile(checkNo);
        } else {
            checkReport = reGenerateReportFile(checkNo);
        }

        CheckReportDTO checkReportDTO = new CheckReportDTO();
        checkReportDTO.setReportName(checkReport.getReportName());
        checkReportDTO.setStatus(checkReport.getStatus());
        checkReportDTO.setFilePath(checkReport.getFilePath());
        checkReportDTO.setFileCode(checkReport.getFileCode());
        return checkReportDTO;
    }

    @Override
    public CheckReport generateReportFile(String checkNo) throws SystemException {
        CheckReport checkReport = new CheckReport();
        generateReport(checkNo, checkReport);
        // 保存检测报告日志
        this.checkReportService.save(checkReport);
        return checkReport;
    }

    @Override
    public CheckReport reGenerateReportFile(String checkNo) throws SystemException {
        CheckReport checkReport = this.checkReportService.getByCheckNo(checkNo);
        generateReport(checkNo, checkReport);
        // 更新检测报告
        this.checkReportService.updateById(checkReport);
        return checkReport;
    }

    private void generateReport(String checkNo, CheckReport checkReport) {
        checkReport.setCheckNo(checkNo);

        Map<String, Object> params = this.getPaperCheckReportParams(checkNo);
        params.put("isDownload", true);
        Context context = new Context();
        context.setVariables(params);
        String html = templateEngine.process("report/mainReport", context);
        CheckRequest checkRequest = (CheckRequest) params.get("checkRequest");
        String datePath = FileUtil.generatePathByDate();
        String fullPath = checkProperties.getReportPath() + File.separator + datePath;
        String reportName = EscapeUtil.escape(checkRequest.getTitle());
        String fileCode = NanoId.randomNanoId();
        checkReport.setReportName(reportName);
        checkReport.setFileCode(fileCode);

        // 按照fileName创建文件夹,如：report/2023/12/11/xxxxxxx
        String reportFolder = fullPath + File.separator + fileCode;
        // 压缩包名称,如：report/2023/12/11/xxxxxxx.zup
        String zipReportFile = reportFolder + ".zip";
        checkReport.setFilePath(datePath + File.separator + fileCode + ".zip");
        checkReport.setCompress(CompressType.ZIP.getValue());

        // 按照日期创建文件夹
        Path folderPath = Paths.get(fullPath);
        boolean isCreated = folderPath.toFile().mkdirs();
        if (isCreated) {
            log.info("创建文件夹成功,fullPath:{}", fullPath);
        }

        Path reprotFolderPath = Paths.get(reportFolder);
        boolean mkReportFolder = reprotFolderPath.toFile().mkdirs();
        if (mkReportFolder) {
            log.info("创建文件夹成功,reportFolder:{}", reportFolder);
        }

        // 拷贝静态资源文件
        try {
            FileUtil.copyContent(Paths.get(
                            Objects.requireNonNull(
                                            PaperCheckServiceImpl.class.getClassLoader().
                                                    getResource("templates/download/static/"))
                                    .toURI())
                    , reprotFolderPath);
        } catch (URISyntaxException e) {
            log.error("拷贝静态资源文件失败", e);
            checkReport.setStatus(CheckReportStatus.FAIL.getValue());
            checkReport.setMsg(StrUtil.sub(e.getMessage(), 0, 100));
        }
        // 将Html写入文件
        try {
            Files.writeString(Paths.get(reportFolder + File.separator + "index.html"), html, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            log.error("写入Html文件失败", e);
            checkReport.setStatus(CheckReportStatus.FAIL.getValue());
            checkReport.setMsg(StrUtil.sub(e.getMessage(), 0, 100));
        }

        ZipUtil.zip(reportFolder, zipReportFile);

        if (FileUtil.del(reportFolder)) {
            log.info("删除文件夹成功,reportFolder:{}", reportFolder);
        } else {
            checkReport.setMsg("删除文件夹成功失败,reportFolder:" + reportFolder);
        }

        checkReport.setStatus(CheckReportStatus.DONE.getValue());
    }


}
