package com.eva.check.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 内容收集控制器
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Controller
@RequestMapping("/collect")
public class ContentCollectController {

    @GetMapping
    public String addPage() {

        return "addPage";
    }
}
