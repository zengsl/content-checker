package com.eva.check.pojo;

import com.eva.check.pojo.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 论文扩展信息
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PaperExt extends BaseEntity {

    private Long paperId;

    private  String dataType;

    private  String content;
}
