package com.eva.check.web.controller.vo;

import lombok.Data;

/**
 * 论文检测VO
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Data
public class PaperCheckVO {

    private String title;

    private String author;

    private String content;

    private String publishYear;
}
