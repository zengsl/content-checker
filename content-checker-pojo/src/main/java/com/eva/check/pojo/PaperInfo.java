package com.eva.check.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.eva.check.pojo.common.BaseEntity;
import lombok.*;


/**
 * 论文信息
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "paper_info")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaperInfo extends BaseEntity {
    /**
     * 论文主键
     */
    @TableId(type = IdType.AUTO)
    private Long paperId;

    private String paperNo;

    /**
     * {@link com.eva.check.common.enums.DataType}
     */
    private String dataType;

    private String title;

    private String author;

    private String content;

    private Integer wordCount;

    private Integer paraCount;

    /**
     * hash值
     */
    private Long hash;

    private String hash1;
    private String hash2;
    private String hash3;
    private String hash4;

    private String publishYear;

    /**
     * {@link com.eva.check.common.enums.DataSource}
     */
    private String dataSource;

}
