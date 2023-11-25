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
 * 验证句子对
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "check_sentence_pair")
@Data
@Builder
public class CheckSentencePair extends BaseEntity implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 验证任务主键
     */
    private Long taskId;

    /**
     * 验证段落主键
     */
    private Long checkParaId;

    /**
     * 疑似段落主键
     */
    private Long targetParaId;

    /**
     * 验证句子主键
     */
    private Long checkSentenceId;

    /**
     * 疑似句子主键
     */
    private Long targetSentenceId;

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