package com.eva.check.pojo.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ParagraphResult {
    private Long checkParagraphId;
    private String renderContent;
    private Double similarity;
    private String cssClassName;

    private Map<Long, SentenceResult> similarSentenceResultMap;
}
