package com.eva.check.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eva.check.pojo.common.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 论文段落
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "paper_paragraph")
@Builder
public class PaperParagraph extends BaseEntity {
    /**
     * 段落主键
     */
    @TableId(type = IdType.AUTO)
    private Long paragraphId;

    private Long paragraphNum;

    private Long paperId;

    private String paperNo;

    private String content;

    private Integer wordCount;

    private Integer sentenceCount;

    private Long hash;
    private String hash1;
    private String hash2;
    private String hash3;
    private String hash4;
}
