package com.eva.check.web.controller.vo;

import lombok.Data;

/**
 * 论文收集VO
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Data
public class PaperCollectVO {

    private String title;

    private String author;

    private String paperNo;

    private String content;

    private String publishYear;
}
