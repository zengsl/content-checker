package com.eva.check.common.enums;

import com.eva.check.common.enums.common.IBaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 论文导入的状态
 *
 *
 * @author zzz
 * @date 2024/04/25
 */
@Getter
@RequiredArgsConstructor
public enum ImportPageStatus implements IBaseEnum<String> {

    /**
     * 待处理
     */
    INIT("待处理", "0"),
    DONE("完成", "1"),
    FAIL("失败", "2")

    ;

    private final String name;

    private final String value;
}
