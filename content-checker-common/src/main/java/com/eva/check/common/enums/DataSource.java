package com.eva.check.common.enums;

import com.eva.check.common.enums.common.IBaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 数据来源
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Getter
@RequiredArgsConstructor
public enum DataSource implements IBaseEnum<String> {

    /**
     * 互联网
     */
    INTERNET("互联网", "1"),
    HISTORY("历史数据", "0"),
    ;

    private final String name;

    private final String value;

}
