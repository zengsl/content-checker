package com.eva.check.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 检测报告Controller
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Controller
@RequestMapping("/report")
public class CheckReportController {

    @GetMapping
    public String reportSearch() {


        return "report/reportSearch";
    }
}
