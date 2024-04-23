package com.eva.check.pojo.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 报告详情段落信息
 *
 * @author zzz
 * @date 2023/11/27 21:17
 */
@Data
public class ReportDetailParagraphVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8205572860984670458L;

    private Long checkParagraphId;

    private Double similarity;

    private String renderContent;

    private Integer paragraphNum;
}
