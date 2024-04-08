package com.eva.check.web.controller;

import com.eva.check.common.enums.CheckReqSource;
import com.eva.check.pojo.CheckRequest;
import com.eva.check.pojo.dto.PaperCheckReq;
import com.eva.check.service.core.PaperCheckService;
import com.eva.check.web.common.R;
import com.eva.check.web.controller.vo.PaperCheckVO;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 内容检测控制器
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Controller
@RequestMapping("/check")
@RequiredArgsConstructor
@Validated
public class ContentCheckController {

    private final PaperCheckService paperCheckService;

    @GetMapping
    public String checkPage() {
        return "check/add";
    }

    @GetMapping("/resultSearchPage")
    public String resultSearchPage() {
        return "check/checkResultSearch";
    }

    @GetMapping("/getCheckResult")
    @ResponseBody
    public R<CheckRequest> getCheckResult(@NotBlank String checkNo) {
        CheckRequest paperCheckResult = this.paperCheckService.getPaperCheckResult(checkNo);
        paperCheckResult.setCheckId(null);
        return R.ok(paperCheckResult);
    }

    @PostMapping
    @ResponseBody
    public R<String> submitCheckPage(PaperCheckVO paperCheckVO) {
        PaperCheckReq paperCheckReq = new PaperCheckReq();
        paperCheckReq.setReqSource(CheckReqSource.WEB.getValue())
                .setContent(paperCheckVO.getContent())
//                .setPaperNo("test:9")
                .setAuthor(paperCheckVO.getAuthor())
                .setTitle(paperCheckVO.getTitle())
                .setPublishYear(paperCheckVO.getPublishYear())
        ;
        String checkNo;
        if (paperCheckVO.getNeedCollect() != null && paperCheckVO.getNeedCollect()) {
            checkNo = this.paperCheckService.createPaperCheckAndCollect(paperCheckReq);
        } else {
            checkNo = this.paperCheckService.createPaperCheck(paperCheckReq);

        }
        return R.ok(checkNo);
    }
}
