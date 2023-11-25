package com.eva.check.common.enums;

import com.eva.check.common.enums.common.IBaseEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 检测请求来源
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Getter
@RequiredArgsConstructor
public enum CheckReqSource implements IBaseEnum<String> {

    /**
     * WEB端
     */
    WEB("web", "1"),
    API("api", "2")
   ;

    private final String name;

    private final String value;

}
