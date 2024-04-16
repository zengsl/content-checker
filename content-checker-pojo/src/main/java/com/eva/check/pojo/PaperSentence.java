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
 * 论文句子
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value ="paper_sentence")
@Builder
public class PaperSentence extends BaseEntity implements Serializable {

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 8155246687510534856L;

    /**
     * 句子主键
     */
    @TableId(type = IdType.AUTO)
    private   Long sentenceId;

    private   Long sentenceNum;

    private   Long paragraphId;

    private   String originContent;

    private   String content;

    private   Long hash;

    private String hash1;
    private String hash2;
    private String hash3;
    private String hash4;
}
