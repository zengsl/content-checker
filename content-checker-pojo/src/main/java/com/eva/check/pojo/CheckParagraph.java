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
 * 检测文本段落
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "check_paragraph")
@Data
@Builder
public class CheckParagraph extends BaseEntity implements Serializable {

    /**
     * 段落主键
     */
    @TableId(type = IdType.AUTO)
    private Long paragraphId;

    /**
     * 段落号
     */
    private Integer paragraphNum;

    /**
     * 检测任务主键
     */
    private Long taskId;

    /**
     * 检测数据主键
     */
    private Long checkId;

    /**
     * 编号
     */
    private String paperNo;

    /**
     * 论文主键
     */
    private Long paperId;

    /**
     * 文本内容
     */
    private String content;

    /**
     * 相似度
     */
    private Double similarity;


    /**
     * 状态: 0 待处理,  1 处理中， 2 已完成 3 失败
     */
    private String status;


    /**
     * hash值
     */
    private Long hash;

    /**
     * 第一段hash值
     */
    private String hash1;

    /**
     * 第二段hash值
     */
    private String hash2;

    /**
     * 第三段hash值
     */
    private String hash3;

    /**
     * 第四段hash值
     */
    private String hash4;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}