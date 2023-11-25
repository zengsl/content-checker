package com.eva.check.common.enums;

import com.eva.check.common.enums.common.IBaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 账户类型
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Getter
@RequiredArgsConstructor
public enum AccountType implements IBaseEnum<Integer> {
    /**
     * 单位
     */
    ORG("org", 1),

    /**
     * 个人
     */
    PERSON("person", 2);

    private final String name;

    private final Integer value;
}
