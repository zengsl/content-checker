package com.eva.check.pojo.vo;

import lombok.Data;

/**
 * 报告详情句子信息
 *
 * @author zzz
 * @date 2023/11/28 22:24
 */
@Data
public class SimilarSentenceVO {

    private String checkSentence;

    private String targetSentence;

    private String targetPart;

    private Double similarity;

    private SimilarPaperVO similarPaper;
}
