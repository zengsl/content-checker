package com.eva.check.common.enums;

import com.eva.check.common.enums.common.IBaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 检测请求状态
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Getter
@RequiredArgsConstructor
public enum CheckRequestStatus implements IBaseEnum<String> {

    /**
     * 待处理
     */
    INIT("待处理", "0"),
    DOING("处理中", "1"),
    DONE("完成", "2"),
    FAIL("失败", "3"),
    CANCEL("取消", "4");

    private final String name;

    private final String value;

}
