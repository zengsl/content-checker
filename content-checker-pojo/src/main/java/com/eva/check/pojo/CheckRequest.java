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
 * 验证请求
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value ="check_request")
@Data
public class CheckRequest extends BaseEntity implements Serializable {

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 验证请求主键
     */
    @TableId(type = IdType.AUTO)
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
     * 请求来源: 1 web端调用 2 系统API
     */
    private String reqSource;

    /**
     * 标题
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 
     */
    private String publishYear;

    /**
     * 任务数: check_task
     */
    private Integer taskNum;

    /**
     * 状态: 0 待处理,  1 处理中， 2 已完成
     */
    private String status;

    /**
     * 相似度
     */
    private Double similarity;
}