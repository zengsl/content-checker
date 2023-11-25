package com.eva.check.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eva.check.pojo.common.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 论文分词
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "paper_token")
@Builder
public class PaperToken extends BaseEntity {
    /**
     * 分词主键
     */
    @TableId(type = IdType.AUTO)
    private Long tokenId;

    private Long tokenNum;

    private Long sentenceId;

    private Long paragraphId;

    private String content;
}
