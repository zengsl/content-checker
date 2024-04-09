package com.eva.check.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eva.check.pojo.common.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 检测文本
 *
 * @author zzz
 * @date 2024/04/09
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "check_paper")
@Data
@Builder
public class CheckPaper extends BaseEntity implements Serializable {

    /**
     * 段落主键
     */
    @TableId(type = IdType.AUTO)
    private Long paperId;

    /**
     * 检测数据主键
     */
    private Long checkId;

    /**
     * 编号
     */
    private String paperNo;

    /**
     * 标题
     */
    private String title;

    /**
     * 文本内容
     */
    private String content;

    /**
     * 相似度
     */
    private Double similarity;
}
