package com.eva.check.pojo.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 *
 * @author zengsl
 * @date 2023/11/28 22:34
 */
@Data
public
class SentencePairResult implements Serializable {
    @Serial
    private static final long serialVersionUID = -5143178429159932013L;
    private String targetSentence;
    private String targetPart;
    private Double similarity;
    private String formatSimilarity;
    private String cssClassName;
    private String targetTitle;
    private String targetAuthor;
    private String targetPublishYear;
}
