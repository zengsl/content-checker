package com.eva.check.common.enums;

import com.eva.check.common.enums.common.ErrorCode;
import lombok.AllArgsConstructor;

/**
 * 错误码
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@AllArgsConstructor
public enum PaperErrorCode implements ErrorCode {

    /**
     * 参数无效
     */
    PARAM_INVALID("param_invalid", "参数无效"),
    REPORT_NOT_EXIST("report_not_exist", "报告不存在"),
    SAVE_FAIL("save_fail", "保存失败");

    private final String code;
    private final String msg;

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
