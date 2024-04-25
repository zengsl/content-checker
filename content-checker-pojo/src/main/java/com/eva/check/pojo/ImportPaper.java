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
import java.util.Date;

/**
 * 导入论文表
 *
 * @TableName import_paper
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "import_paper")
@Data
public class ImportPaper extends BaseEntity implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long importId;

    /**
     * 业务调用方可设置的论文编号
     */
    private String paperNo;

    /**
     * 标题
     */
    private String title;

    /**
     * 作者
     */
    private String author;

    /**
     * 内容
     */
    private String content;

    /**
     * 发布年份
     */
    private String publishYear;

    /**
     * 状态：0待处理， 2成功 ，3失败
     */
    private String status;

    /**
     * 成功或者失败消息
     */
    private String msg;


    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}