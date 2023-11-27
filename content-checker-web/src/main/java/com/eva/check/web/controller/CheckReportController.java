package com.eva.check.web.controller;

import cn.hutool.core.util.NumberUtil;
import com.eva.check.pojo.CheckParagraph;
import com.eva.check.pojo.CheckRequest;
import com.eva.check.pojo.CheckSentence;
import com.eva.check.pojo.CheckTask;
import com.eva.check.pojo.vo.ReportDetailParagraphVO;
import com.eva.check.service.core.SimilarTextRender;
import com.eva.check.service.support.CheckParagraphService;
import com.eva.check.service.support.CheckRequestService;
import com.eva.check.service.support.CheckSentenceService;
import com.eva.check.service.support.CheckTaskService;
import com.google.common.collect.Lists;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.NumberUtils;

import java.util.ArrayList;
import java.util.List;

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
public class CheckReportController {

    private final CheckRequestService checkRequestService;
    private final CheckTaskService checkTaskService;
    private final CheckParagraphService checkParagraphService;
    private final SimilarTextRender similarTextRender;

    @GetMapping("/{checkNo}")
    public ModelAndView reportSearch(@NotBlank @PathVariable String checkNo) {


        CheckRequest checkRequest = this.checkRequestService.getByCheckNo(checkNo);
        CheckTask contentCheckTask = this.checkTaskService.findContentCheckTask(checkNo);
        List<CheckParagraph> checkParagraphs = this.checkParagraphService.getByTaskId(contentCheckTask.getTaskId());
        List<ReportDetailParagraphVO> reportParagraphs = Lists.newArrayList();
        for (CheckParagraph checkParagraph : checkParagraphs) {
            ReportDetailParagraphVO reportDetailParagraphVO = new ReportDetailParagraphVO();
            reportDetailParagraphVO.setCheckParagraphId(checkParagraph.getParagraphId());
            reportDetailParagraphVO.setSimilarity(checkParagraph.getSimilarity());
            reportDetailParagraphVO.setParagraphNum(checkParagraph.getParagraphNum());
            String renderText = this.similarTextRender.render(checkParagraph.getContent(), checkParagraph.getSimilarity(), checkParagraph.getParagraphId());
            reportDetailParagraphVO.setContent(renderText);
            reportParagraphs.add(reportDetailParagraphVO);
        }


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("finalSimilarity", NumberUtil.decimalFormat("#.##%",checkRequest.getSimilarity()));
        modelAndView.addObject("checkRequest", checkRequest);
        modelAndView.addObject("contentCheckTask", contentCheckTask);
        modelAndView.addObject("reportParagraphs", reportParagraphs);
        modelAndView.setViewName("report/mainReport");
        return modelAndView;
    }
}
