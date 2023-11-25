package com.eva.check.common.util;


import com.eva.check.common.enums.common.IBaseEnum;


/**
 * 枚举对象工具类
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
public class EnumUtils {

    /**
     * 根据枚举对象value属性值获取枚举对象
     *
     * @param value     枚举值
     * @param enumClass 枚举类
     * @return 枚举对象
     */
    public static <T, E extends IBaseEnum<T>> E getEnumByValue(T value, Class<E> enumClass) {
        E[] enumConstants = enumClass.getEnumConstants();
        for (E enumConstant : enumConstants) {
            if (enumConstant.getValue().equals(value)) {
                return enumConstant;
            }
        }
        return null;
    }


}
