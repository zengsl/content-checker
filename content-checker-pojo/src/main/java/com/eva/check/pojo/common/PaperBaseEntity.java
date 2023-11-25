package com.eva.check.pojo.common;

import com.eva.check.common.enums.DataSource;
import com.eva.check.common.enums.DataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 论文基本信息
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PaperBaseEntity extends BaseEntity{

    private String title;

    private String author;

    private String content;

    private String publishYear;

    private List<PaperExtDto> paperExtList;

    /**
     * 用户自定义的文档编号。校验时用于排除自身
     */
    private String paperNo;

    /**
     * {@link com.eva.check.common.enums.DataType}
     */
    @Deprecated
    private String dataType = DataType.FULL_TEXT.getValue();

    /**
     * {@link com.eva.check.common.enums.DataSource}
     */
    private String dataSource = DataSource.INTERNET.getValue();

    @Data
    public static class PaperExtDto {
        private  String dataType;
        private  String content;
    }
}
