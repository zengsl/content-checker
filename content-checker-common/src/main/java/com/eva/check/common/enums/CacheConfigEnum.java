package com.eva.check.common.enums;

import lombok.Getter;

import java.time.Duration;

import static com.eva.check.common.constant.CacheConstant.CACHE_PREFIX;

/**
 * 【@Cacheable】无法使用变量配置，所以该枚举运用不了
 *
 * @author zengsl
 * @date 2024/4/23 17:13
 */
@Deprecated
@Getter
public enum CacheConfigEnum {
    /**
     *
     */
    PARAGRAPH_SENTENCE("para_sentence", Duration.ofDays(30)),
    SENTENCE_TOKEN("para_sentence", Duration.ofDays(30)),


    ;

    CacheConfigEnum(String type, Duration ttl) {
        this.type = type;
        this.ttl = ttl;
        this.CACHE_KEY = CACHE_PREFIX + ":" + this.type;
    }

    public final String CACHE_KEY;
    private final String type;
    private final Duration ttl;
    private final Boolean isAuto = true;
}
