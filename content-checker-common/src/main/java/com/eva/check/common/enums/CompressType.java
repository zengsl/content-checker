package com.eva.check.common.enums;

import com.eva.check.common.enums.common.IBaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum CompressType implements IBaseEnum<String> {

    /**
     * zip压缩格式
     */
    ZIP("zip", "1")

    ;

    private final String name;

    private final String value;

}
