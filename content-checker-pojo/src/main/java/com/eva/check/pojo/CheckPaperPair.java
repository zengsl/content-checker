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
 * 验证文章列表
 *
 * @author zzz
 * @date 2024/04/09
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "check_paper_pair")
@Data
@Builder
public class CheckPaperPair  extends BaseEntity implements Serializable  {

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
     * 验证文章主键
     */
    private Long checkPaperId;

    /**
     * 疑似文章主键
     */
    private Long targetPaperId;

    /**
     * 相似度
     */
    private Double similarity;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
