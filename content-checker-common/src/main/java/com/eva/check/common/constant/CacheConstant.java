package com.eva.check.common.constant;

/**
 * 缓存常量
 *
 * @author zengsl
 * @date 2024/4/18 10:34
 */
public interface CacheConstant {
    String CACHE_PREFIX = "Content_Check";
    String PARAGRAPH_SENTENCE_CACHE_KEY = CACHE_PREFIX + ":para_sentence";
    String PARAGRAPH_TOKEN_CACHE_KEY = CACHE_PREFIX + ":para_token";
    String SENTENCE_TOKEN_CACHE_KEY = CACHE_PREFIX + ":sentence_token";
}
