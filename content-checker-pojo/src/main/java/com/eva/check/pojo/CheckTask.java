package com.eva.check.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eva.check.pojo.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 验证任务
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "check_task")
@Data
public class CheckTask extends BaseEntity implements Serializable {


    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 验证任务主键
     */
    @TableId(type = IdType.AUTO)
    private Long taskId;

    /**
     * 验证请求主键
     */
    private Long checkId;

    /**
     * 验证标号
     */
    private String checkNo;

    /**
     * 论文编号
     */
    private String paperNo;

    /**
     * 验证类型: 1 内容 2 title
     */
    private String checkType;

    /**
     * 内容
     */
    private String content;

    /**
     * 状态: 0 待处理,  1 处理中， 2 已完成 3 失败
     */
    private String status;

    /**
     * 相似度
     */
    private Double similarity;
}