package com.eva.check.web.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.EscapeUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ZipUtil;
import com.eva.check.common.util.FileUtil;
import com.eva.check.pojo.CheckRequest;
import com.eva.check.pojo.CheckTask;
import com.eva.check.pojo.converter.ReportConverter;
import com.eva.check.pojo.dto.PaperResult;
import com.eva.check.service.config.CheckProperties;
import com.eva.check.service.core.SimilarPaperService;
import com.eva.check.service.support.CheckRequestService;
import com.eva.check.service.support.CheckTaskService;
import com.google.common.collect.Maps;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.ISpringTemplateEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Objects;

/**
 * 检测报告Controller
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Controller
@RequestMapping("/report")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CheckReportController {

    private final CheckRequestService checkRequestService;
    private final CheckTaskService checkTaskService;
    private final SimilarPaperService similarPaperService;
    private final CheckProperties checkProperties;
    private final ISpringTemplateEngine templateEngine;

    @GetMapping("/{checkNo}")
    public ModelAndView view(@NotBlank @PathVariable String checkNo) {

        Map<String, Object> params = populateReportParams(checkNo);
        params.put("isDownload", false);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addAllObjects(params);
        modelAndView.setViewName("report/mainReport");
        return modelAndView;
    }


    @GetMapping("/download/{checkNo}")
    public StreamingResponseBody download(@NotBlank @PathVariable String checkNo, HttpServletResponse httpResponse) throws URISyntaxException, IOException {

        Context context = new Context();
        Map<String, Object> params = populateReportParams(checkNo);
        params.put("isDownload", true);
        context.setVariables(params);
        String html = templateEngine.process("report/mainReport", context);
        CheckRequest checkRequest = (CheckRequest) params.get("checkRequest");
        String filePath = FileUtil.generatePathByDate(checkProperties.getReportPath());
        String title = EscapeUtil.escape(checkRequest.getTitle());
        String fileName = title + "_" + checkRequest.getPaperNo();
        // 设置下载zip文件的名称
        httpResponse.setHeader("Content-Disposition", "attachment; filename=\"" + title + ".zip\"");
        // 按照fileName创建文件夹
        String reportFolder = filePath + File.separator + fileName;
        String zipReportFile = reportFolder + ".zip";
        boolean exists = Paths.get(zipReportFile).toFile().exists();
        // TODO 下载记录做存储，压缩包名字使用nanoId随机生成
        // 如果压缩文件已经存在则直接返回以供下载
        if (exists) {
            return outputStream -> {
                FileInputStream fileInputStream = new FileInputStream(zipReportFile);
                IoUtil.copy(fileInputStream, outputStream);
                IoUtil.close(outputStream);
                IoUtil.close(fileInputStream);
            };
        }

        // 按照日期创建文件夹
        Path folderPath = Paths.get(filePath);
        boolean isCreated = folderPath.toFile().mkdirs();
        if (isCreated) {
            log.info("创建文件夹成功,filePath:{}", filePath);
        }


        Path reprotFolderPath = Paths.get(reportFolder);
        boolean mkReportFolder = reprotFolderPath.toFile().mkdirs();
        if (mkReportFolder) {
            log.info("创建文件夹成功,reportFolder:{}", reportFolder);
        }
        // 拷贝静态资源文件
        cn.hutool.core.io.FileUtil.copyContent(Paths.get(Objects.requireNonNull(CheckReportController.class.getClassLoader().getResource("templates/download/static/")).toURI()), reprotFolderPath);
        // 将Html写入文件
        Files.writeString(Paths.get(reportFolder + File.separator + "index.html"), html, StandardOpenOption.CREATE_NEW);
        ZipUtil.zip(reportFolder, zipReportFile);
        File file = new File(reportFolder);
        if (file.delete()) {
            log.info("删除文件夹成功,reportFolder:{}", reportFolder);
        }

        return outputStream -> {
            FileInputStream fileInputStream = new FileInputStream(zipReportFile);
            IoUtil.copy(fileInputStream, outputStream);
            IoUtil.close(outputStream);
            IoUtil.close(fileInputStream);
        };
    }


    private Map<String, Object> populateReportParams(String checkNo) {
        CheckRequest checkRequest = this.checkRequestService.getByCheckNo(checkNo);
        CheckTask contentCheckTask = this.checkTaskService.findContentCheckTask(checkNo);
        PaperResult paperResult = this.similarPaperService.assemblePaperResult(contentCheckTask.getTaskId());

        Map<String, Object> params = Maps.newHashMap();
        params.put("finalSimilarity", NumberUtil.decimalFormat("#.##%", checkRequest.getSimilarity()));
        params.put("checkRequest", checkRequest);
        params.put("contentCheckTask", contentCheckTask);
        params.put("reportParagraphs", ReportConverter.INSTANCE.paperResult2paragraphVO(paperResult));
        params.put("similarSentenceResultMap", ReportConverter.INSTANCE.paperResult2SentenceMap(paperResult));
        params.put("isDownload", true);
        return params;
    }
}
