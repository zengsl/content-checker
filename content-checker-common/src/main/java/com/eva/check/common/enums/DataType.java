package com.eva.check.common.enums;

import com.eva.check.common.enums.common.IBaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 数据类型
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Getter
@RequiredArgsConstructor
public enum DataType implements IBaseEnum<String> {

    /**
     * 全文
     */
    FULL_TEXT("全文", "1"),
    /*ABSTRACT("摘要", "2"),*/
    TITLE("标题", "3");

    private final String name;

    private final String value;

}
