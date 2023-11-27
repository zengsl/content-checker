package com.eva.check.pojo.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 报告详情段落信息
 *
 * @author zzz
 * @date 2023/11/27 21:17
 */
@Data
public class ReportDetailParagraphVO {

    private Long checkParagraphId;

    private Double similarity;

    private String content;

    private Integer paragraphNum;
}
