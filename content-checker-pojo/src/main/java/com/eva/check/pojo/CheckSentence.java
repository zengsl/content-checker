package com.eva.check.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eva.check.pojo.common.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 检测文本句子
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "check_sentence")
@Data
@Builder
public class CheckSentence extends BaseEntity implements Serializable {

    /**
     * 句子主键
     */
    @TableId(type = IdType.AUTO)
    private Long sentenceId;

    /**
     * 句子号
     */
    private Integer sentenceNum;

    /**
     * 段落主键
     */
    private Long paragraphId;

    /**
     * 原始文本内容
     */
    private String originContent;

    /**
     * 文本内容
     */
    private String content;

    private Integer wordCount;

    /**
     * 相似度
     */
    private Double similarity;


    /**
     * 状态: 0 待处理,  1 处理中， 2 已完成 3 失败
     */
    private String status;


    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}