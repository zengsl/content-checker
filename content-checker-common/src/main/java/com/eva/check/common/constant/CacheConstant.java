package com.eva.check.common.constant;

/**
 * 缓存常量
 * <p>
 * 在RedisConfig#buildInitialCacheConfigurationMap中配置对应缓存的失效时间
 *
 * @author zengsl
 * @date 2024/4/18 10:34
 */
public interface CacheConstant {
    String CACHE_PREFIX = "CC";

    String PARAGRAPH_SENTENCE_CACHE_KEY = CACHE_PREFIX + ":para_sentence";
    String PARAGRAPH_TOKEN_CACHE_KEY = CACHE_PREFIX + ":para_token";
    String SENTENCE_TOKEN_CACHE_KEY = CACHE_PREFIX + ":sentence_paper_token";
    String SENTENCE_CACHE_KEY = CACHE_PREFIX + ":sentence";
    String SENTENCE_PAPER_TOKEN_CACHE_KEY = CACHE_PREFIX + ":sentence_token";
    String SENTENCE_TOKEN_WORD_FREQ_CACHE_KEY = CACHE_PREFIX + ":sentence_token_word_freq";
    String REPORT_CONTENT_CACHE_KEY = CACHE_PREFIX + ":report_content";
    String REPORT_CONTENT_DTO_CACHE_KEY = CACHE_PREFIX + ":report_content_dto";
    /*String CHECK_TASK_CACHE_KEY = CACHE_PREFIX + ":check_task";*/
    String CHECK_TASK_CONTENT_CACHE_KEY = CACHE_PREFIX + ":check_task_content";
    String CHECK_TASK_PARA_CACHE_KEY = CACHE_PREFIX + ":check_task_para";


    String CHECK_PROCESS_LOG_CACHE_KEY = CACHE_PREFIX + ":check_process_log";
}
