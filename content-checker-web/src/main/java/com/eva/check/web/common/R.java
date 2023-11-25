package com.eva.check.web.common;


import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 响应信息主体
 *
 * @author ruoyi
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class R<T> implements Serializable
{
    @Serial
    private static final long serialVersionUID = 1L;

    /** 成功 */
    public static final int SUCCESS = Constants.SUCCESS;

    /** 失败 */
    public static final int FAIL = Constants.FAIL;

    @Getter
    @Setter
    private int code;

    @Getter
    @Setter
    private String msg;

    @Getter
    @Setter
    private T data;

    public static <T> R<T> ok()
    {
        return restResult(null, SUCCESS, Constants.SUCCESS_MSG);
    }

    public static <T> R<T> ok(T data)
    {
        return restResult(data, SUCCESS, Constants.SUCCESS_MSG);
    }

    public static <T> R<T> ok(T data, String msg)
    {
        return restResult(data, SUCCESS, msg);
    }

    public static <T> R<T> fail() {
        return restResult(null, FAIL, Constants.FAIL_MSG);
    }

    public static <T> R<T> fail(String msg) {
        return restResult(null, FAIL, msg);
    }

    public static <T> R<T> forbidden(String msg) {
        return restResult(null, HttpStatus.FORBIDDEN, msg);
    }

    public static <T> R<T> fail(T data) {
        return restResult(data, FAIL, Constants.FAIL_MSG);
    }

    public static <T> R<T> fail(T data, String msg) {
        return restResult(data, FAIL, msg);
    }

    public static <T> R<T> fail(int code, String msg)
    {
        return restResult(null, code, msg);
    }

    /**
     * 针对不可控异常
     * 例如nullpoint overFlow
     */
    public static <T> R<T> failedWithException(Exception exception) {
        return restResult(null, Constants.FAIL, exception.getMessage());
    }

    /**
     * 针对不可控异常
     * 例如nullpoint overFlow
     */
    public static <T> R<T> failedWithException(int code, String msg) {
        return restResult(null, code, msg);
    }

    public static R<Number> result(int count) {
        return count > 0 ? ok() : fail();
    }

    private static <T> R<T> restResult(T data, int code, String msg)
    {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }

    @JsonIgnore
    public Boolean isSuccess() {
        return R.SUCCESS == getCode();
    }
}
