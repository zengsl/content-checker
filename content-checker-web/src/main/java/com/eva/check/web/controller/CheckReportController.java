package com.eva.check.web.controller;

import cn.hutool.core.io.IoUtil;
import com.eva.check.common.enums.CheckReportStatus;
import com.eva.check.common.enums.PaperErrorCode;
import com.eva.check.common.exception.SystemException;
import com.eva.check.pojo.dto.CheckReportDTO;
import com.eva.check.service.config.CheckProperties;
import com.eva.check.service.core.PaperCheckService;
import com.eva.check.web.common.R;
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

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

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

    private final CheckProperties checkProperties;
    private final PaperCheckService paperCheckService;

    @GetMapping("/{checkNo}")
    public ModelAndView view(@NotBlank @PathVariable String checkNo) {

        Map<String, Object> params = this.paperCheckService.getPaperCheckReportParams(checkNo);
        if (params == null || params.isEmpty()) {
            return new ModelAndView("report/404");
        }
        params.put("isDownload", false);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addAllObjects(params);
        modelAndView.setViewName("report/mainReport");
        return modelAndView;
    }


    @GetMapping("/download-check/{checkNo}")
    public R<String> downloadCheck(@NotBlank @PathVariable String checkNo) {
        CheckReportDTO paperCheckReport = this.paperCheckService.getPaperCheckReport(checkNo);
        return R.ok(paperCheckReport != null ? paperCheckReport.getStatus() : CheckReportStatus.DOING.getValue());
    }


    @GetMapping("/download/{checkNo}")
    public StreamingResponseBody download(@NotBlank @PathVariable String checkNo, HttpServletResponse httpResponse) {

        // this.paperCheckService.generateReport(checkNo);
        CheckReportDTO paperCheckReport = this.paperCheckService.getOrCreateReportFile(checkNo);
        if (!CheckReportStatus.DONE.getValue().equals(paperCheckReport.getStatus())) {
            throw new SystemException(PaperErrorCode.PARAM_INVALID, "报告未生成");
        }
        httpResponse.setHeader("Content-Disposition", "attachment; filename=\"" + paperCheckReport.getReportName() + ".zip\"");
        return outputStream -> {
            FileInputStream fileInputStream = new FileInputStream(checkProperties.getReportPath() + File.separator
                    + paperCheckReport.getFilePath());
            IoUtil.copy(fileInputStream, outputStream);
            IoUtil.close(outputStream);
            IoUtil.close(fileInputStream);
        };

    }

}
