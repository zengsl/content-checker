package com.eva.check.pojo.vo;

import lombok.Data;

/**
 * 报告详情论文信息
 *
 * @author zzz
 * @date 2023/11/28 22:24
 */
@Data
public class SimilarPaperVO {

    private Long checkPaperId;

    private Long targetPaperId;

    private String targetTitle;

    private String targetAuthor;

    private String targetContent;

    private String targetPublishYear;

    private Double similarity;
    private String formatSimilarity;

    private String cssClassName;
}
