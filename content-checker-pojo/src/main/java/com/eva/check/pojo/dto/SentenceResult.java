package com.eva.check.pojo.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author zengsl
 */
@Data
public
class SentenceResult implements Serializable {
    @Serial
    private static final long serialVersionUID = -3673511402147803550L;

    private Double similarity;
    private String checkSentence;
    private Integer similarCount;
    private String cssClassName;

    private List<SentencePairResult> sentencePairResultList;
}
