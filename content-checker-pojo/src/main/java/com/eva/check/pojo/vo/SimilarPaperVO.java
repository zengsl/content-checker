package com.eva.check.pojo.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 报告详情论文信息
 *
 * @author zzz
 * @date 2023/11/28 22:24
 */
@Data
public class SimilarPaperVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5710989716270485378L;

    private Long checkPaperId;
    private String checkPaperNo;

    private Long targetPaperId;
    private String targetPaperNo;

    private String targetTitle;

    private String targetAuthor;

    private String targetContent;

    private String targetPublishYear;

    private Double similarity;
    private String formatSimilarity;
    private String cssClassName;
}
