package com.eva.check.web.controller;

import com.eva.check.common.constant.ContentCheckConstant;
import com.eva.check.common.enums.DataType;
import com.eva.check.pojo.dto.PaperAddReq;
import com.eva.check.service.core.PaperCollectService;
import com.eva.check.web.common.R;
import com.eva.check.web.controller.vo.PaperCollectVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 内容收集控制器
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Controller
@RequestMapping("/collect")
@RequiredArgsConstructor
@Validated
public class ContentCollectController {

    private final PaperCollectService paperCollectService;

    @GetMapping
    public String addPage() {
        return "collect/add";
    }


    @GetMapping("batch")
    public String batchImportPage() {
        return "collect/batchImport";
    }

    @PostMapping
    @ResponseBody
    public R<String> submitCollectPage(PaperCollectVO paperCollectVO) {
        PaperAddReq paperAddReq = new PaperAddReq();
        paperAddReq.setPaperNo(paperCollectVO.getPaperNo())
                .setDataType(DataType.FULL_TEXT.getValue())
                .setDataSource(ContentCheckConstant.DATA_SOURCE_DEFAULT)
                .setAuthor(paperCollectVO.getAuthor())
                .setTitle(paperCollectVO.getTitle())
                .setContent(paperCollectVO.getContent())
                .setPublishYear(paperCollectVO.getPublishYear());
        String paperNo = paperCollectService.addNewPaper(paperAddReq);
        return R.ok(paperNo);
    }
}
