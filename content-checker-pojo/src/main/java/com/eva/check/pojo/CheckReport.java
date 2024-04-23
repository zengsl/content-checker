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
 * 检测报告 check_report
 *
 * @author zzz
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "check_report")
@Data
public class CheckReport extends BaseEntity implements Serializable {
    /**
     * 报告主键
     */
    @TableId(type = IdType.AUTO)
    private Long reportId;

    /**
     * 报告名
     */
    private String reportName;

    /**
     * 验证标号
     */
    private String checkNo;

    /**
     * 报告内容
     */
    private String content;

    /**
     * 文件编号
     */
    private String fileCode;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 状态
     */
    private String status;

    /**
     * 信息
     */
    private String msg;

    /**
     * 压缩: 1表示zip
     */
    private String compress;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}