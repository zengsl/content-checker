package com.eva.check.pojo.dto;

import lombok.Data;

/**
 *
 * @author zengsl
 * @date 2023/11/28 22:34
 */
@Data
public
class SentencePairResult {
    private String targetSentence;
    private String targetPart;
    private Double similarity;
    private String formatSimilarity;
    private String cssClassName;
    private String targetTitle;
    private String targetAuthor;
    private String targetPublishYear;
}
