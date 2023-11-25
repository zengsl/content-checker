package com.eva.check.common.enums.common;

/**
 * 错误代码接口
 *
 * @author zzz
 * @version V1.0
 * @date 2023/11/25 16:12
 */
public interface ErrorCode {

    /**
     * 获取错误码
     *
     * @return 返回错误代码
     */
    String getCode();

    /**
     * 获取错误消息
     *
     * @return 返回错误消息
     */
    String getMsg();
}
