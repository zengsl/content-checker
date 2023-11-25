package com.eva.check.common.exception;

import com.eva.check.common.enums.common.ErrorCode;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Supplier;

/**
 * 全局异常
 * <p>
 * 1、通过 {@link ErrorCode}接口维护错误代码和错误消息。
 * <p>
 * 2、默认通过{@link ErrorCode#getMsg()}获取错误消息信息。
 * <p>
 * 同时支持错误消息国际化，如需开启则需要通过"创建子类继承"SystemException重写isI18N()为返回true，并维护相关国际化资源
 * <p>
 * 如果LoginError#INVALID_PARAM代表"非法参数"，资源文件中存储错误信息的KEY为LoginError_INVALID_PARAM，例子如下所示：
 * <p>
 * 例： LoginError_INVALID_PARAM=非法参数
 *
 * @author zzz
 * @version V1.0
 * @date 2023/11/25 16:12
 * @modified 2022/12/23 10:33
 */
public class SystemException extends RuntimeException {

    private static final long serialVersionUID = 2192519456758853897L;

    /**
     * 错误代码
     */
    private final ErrorCode errorCode;

    public SystemException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }

    public SystemException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public SystemException(ErrorCode errorCode, Throwable cause, String message) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public SystemException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMsg(), cause);
        this.errorCode = errorCode;
    }

    /**
     * 供Assert调用，lambda构建对象
     */
    public static Supplier<SystemException> withExSupplier(ErrorCode errorCode) {
        return () -> new SystemException(errorCode);
    }

    /**
     * 供Assert调用，lambda构建对象
     */
    public static Supplier<SystemException> withExSupplier(ErrorCode errorCode, String msg) {
        return () -> new SystemException(errorCode, msg);
    }

    /**
     * 数据信息
     */
    private final Map<String, Object> dataInfo = new HashMap<String, Object>();

    public SystemException set(String key, Object value) {
        dataInfo.put(key, value);
        return this;
    }

    public Object get(String key) {
        return dataInfo.get(key);
    }

    public Map<String, Object> getDataInfo() {
        return Map.copyOf(dataInfo);
    }

    /**
     * 通过错误代码对象获取对应的国际化文本信息
     *
     * @param errorCode 错误代码对象
     * @return errorCode对应的国际化文本信息
     */
    public static String getI18NText(ErrorCode errorCode) {
        if (errorCode == null) {
            return null;
        }
        return doGetText(errorCode);
    }

    public String getText() {
        if (this.errorCode == null) {
            return "其他异常";
        }
        if (!isI18N()) {
            return super.getMessage();
        }
        return doGetText(this.errorCode);
    }

    private static String doGetText(ErrorCode errorCode) {
        // 格式：资源文件中存储错误信息的KEY
        // 例如： LoginError_INVALID_PARAM=非法参数
        String key = errorCode.getClass().getSimpleName() + "_" + errorCode;
        ResourceBundle bundle = ResourceBundle.getBundle("resource.system-exception");
        return bundle.getString(key);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public static SystemException wrap(Throwable exception, ErrorCode errorCode) {
        if (exception instanceof SystemException) {
            SystemException se = (SystemException) exception;
            if (errorCode != null && errorCode != se.getErrorCode()) {
                return new SystemException(errorCode, exception, exception.getMessage());
            }
            return se;
        } else {
            return new SystemException(errorCode, exception, exception.getMessage());
        }
    }

    public static SystemException wrap(Throwable exception) {
        return wrap(exception, null);
    }

    /**
     * 获取错误信息（如果是SystemException 异常就取对应错误信息，如果不是或者错误信息为空就使用默认信息）
     *
     * @param e               异常
     * @param defaultErrorMsg 默认错误信息
     * @return 错误信息
     */
    public static String getSystemErrorMsg(Exception e, String defaultErrorMsg) {
        String errorMsg = "";
        if (e instanceof SystemException) {
            errorMsg = ((SystemException) e).getText();
        }
        if (StringUtils.isBlank(errorMsg)) {
            errorMsg = defaultErrorMsg;
        }
        return errorMsg;
    }

    /**
     * 是否开启国际化，默认不开启。
     * 如需开启则可以继承该类，重写此方法。
     */
    protected boolean isI18N() {
        return false;
    }
}
