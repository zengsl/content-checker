package com.eva.check.service.core.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.id.NanoId;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.eva.check.common.enums.*;
import com.eva.check.common.exception.SystemException;
import com.eva.check.common.util.FileUtil;
import com.eva.check.common.util.TextUtil;
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
import com.eva.check.pojo.vo.CheckReportContentDTO;
import com.eva.check.service.config.CheckProperties;
import com.eva.check.service.core.PaperCheckService;
import com.eva.check.service.core.PaperCollectService;
import com.eva.check.service.flow.ICheckTaskFlow;
import com.eva.check.service.support.CheckPaperService;
import com.eva.check.service.support.CheckReportService;
import com.eva.check.service.support.CheckRequestService;
import com.eva.check.service.support.CheckTaskService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
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
    private final CheckProperties checkProperties;
    private final ISpringTemplateEngine templateEngine;
    private final ICheckTaskFlow checkTaskFlow;

    @Transactional(rollbackFor = Exception.class)
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
        if (!save) {
            log.error("Error saving check request");
            return null;
        }
        // 生成checkPaper
        CheckPaper checkPaper = PaperCheckConverter.INSTANCE.paperCheckReq2CheckPaper(paperCheckReq);
        checkPaper.setCheckId(checkRequest.getCheckId());
        checkPaper.setWordCount(TextUtil.countWord(checkPaper.getContent()));
        checkPaper.setParaCount(1);
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

        // 目前不考虑TITLE比对，可能会删除
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

        // 开启任务
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                checkTaskFlow.processAllTask(checkRequest.getCheckId(), checkTaskList);
            }
        });
        return checkRequest.getCheckNo();
    }

    private static void checkParams(PaperCheckReq paperCheckReq) {
        boolean b = paperCheckReq == null || CollectionUtil.isEmpty(paperCheckReq.getPaperExtList()) && StrUtil.isBlank(paperCheckReq.getContent()) && StrUtil.isBlank(paperCheckReq.getTitle());
        if (b) {
            throw new SystemException(PaperErrorCode.PARAM_INVALID);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String createPaperCheckAndCollect(PaperCheckReq paperCheckReq) throws SystemException {
        // 初始化收录请求
        PaperAddReq paperAddReq = PaperCollectConverter.INSTANCE.check2AddReq(paperCheckReq);
        // 生成论文编号
        String paperNo = StringUtils.hasText(paperAddReq.getPaperNo()) ? paperAddReq.getPaperNo() : NanoId.randomNanoId();
        // 入库前设置编号
        paperAddReq.setPaperNo(paperNo);
        // 收录至文档库。 后阶段可以考虑异步执行收录流程，但需保证其可靠性。
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
    public CheckReportContentDTO getPaperCheckReportParams(String checkNo) throws SystemException {
        CheckRequest checkRequest = this.checkRequestService.getByCheckNo(checkNo);
        if (checkRequest == null) {
            return null;
        }
        return this.checkReportService.getCheckReportContent(checkRequest.getCheckNo());
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

        CheckReportContentDTO checkReportContentDTO = this.getPaperCheckReportParams(checkNo);
        checkReportContentDTO.setIsDownload(true);
        Context context = new Context();

        Map<String, Object> params = ReportConverter.INSTANCE.reportContentDto2Map(checkReportContentDTO);
        context.setVariables(params);

        String html = templateEngine.process("report/mainReport", context);
        String datePath = FileUtil.generatePathByDate();
        String fullPath = checkProperties.getReportPath() + File.separator + datePath;
        String fileCode = NanoId.randomNanoId();
        checkReport.setFileCode(fileCode);

        // 按照fileName创建文件夹,如：report/2023/12/11/xxxxxxx
        String reportFolder = fullPath + File.separator + fileCode;
        // 压缩包名称,如：report/2023/12/11/xxxxxxx.zup
        String zipReportFile = reportFolder + ".zip";
        checkReport.setFilePath(datePath + File.separator + fileCode + ".zip");
        /*checkReport.setCompress(CompressType.ZIP.getValue());*/

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
