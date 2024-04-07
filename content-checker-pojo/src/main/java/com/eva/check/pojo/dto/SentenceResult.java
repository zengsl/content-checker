package com.eva.check.pojo.dto;

import lombok.Data;

import java.util.List;

/**
 * @author zengsl
 */
@Data
public
class SentenceResult {
    private Double similarity;
    private String checkSentence;
    private Integer similarCount;
    private String cssClassName;

    private List<SentencePairResult> sentencePairResultList;
}
