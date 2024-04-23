package com.eva.check.service.flow.impl;

import com.eva.check.common.constant.CacheConstant;
import com.eva.check.service.config.CheckProperties;
import com.eva.check.service.flow.IProcessLogService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

/**
 * redis 执行日志服务
 *
 * @author zengsl
 * @date 2024/4/23 15:42
 */
public class RedisProcessLogServiceImpl implements IProcessLogService {


    public RedisProcessLogServiceImpl(RedisTemplate<String, String> redisTemplate, CheckProperties checkProperties) {
        this.redisTemplate = redisTemplate;
        this.zSetOperations = this.redisTemplate.opsForZSet();
        this.checkProperties = checkProperties;
    }

    private final RedisTemplate<String, String> redisTemplate;
    private final ZSetOperations<String, String> zSetOperations;
    private final CheckProperties checkProperties;

    @Override
    public void log(String processType, Long checkId, String msg) {
        if (!checkProperties.getProcessLog()) {
            return;
        }
        zSetOperations.add(CacheConstant.CHECK_PROCESS_LOG_CACHE_KEY + ":" + checkId, msg, System.currentTimeMillis());
    }
}
