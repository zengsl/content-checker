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

    private String title;

    private String author;

    private String content;

    private String publishYear;
}
