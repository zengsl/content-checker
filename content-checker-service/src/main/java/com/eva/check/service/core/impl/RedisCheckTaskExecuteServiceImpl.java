package com.eva.check.service.core.impl;

import com.eva.check.common.enums.PaperErrorCode;
import com.eva.check.common.exception.SystemException;
import com.eva.check.pojo.CheckRequest;
import com.eva.check.service.core.DuplicateCheckPrepareService;
import com.eva.check.service.support.CheckReportService;
import com.eva.check.service.support.CheckRequestService;
import com.eva.check.service.support.CheckTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * Redis 检测任务执行器
 *
 * @author zengsl
 * @date 2024/4/16 15:07
 */
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class RedisCheckTaskExecuteServiceImpl extends BaseCheckTaskExecuteService {

    public RedisCheckTaskExecuteServiceImpl(CheckRequestService checkRequestService, CheckTaskService checkTaskService, DuplicateCheckPrepareService duplicateCheckPrepareService, RedisTemplate<String, Integer> redisTemplate, CheckReportService checkReportService) {
        super(checkRequestService, checkTaskService, duplicateCheckPrepareService, checkReportService);
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    private final RedisTemplate<String, Integer> redisTemplate;
    private final HashOperations<String, String, Long> hashOperations;
    private final static String TOTAL_KEY = "total";
    private final static String COUNT_KEY = "count";


    @Override
    protected void initTaskTotal(Long checkId, Long total) {
        hashOperations.put("Content_Check:Id:" + checkId, TOTAL_KEY, total);
        redisTemplate.expire("Content_Check:Id:" + checkId, 3, TimeUnit.HOURS);
    }

    @Override
    protected boolean isTaskFinishedAfterAdd(Long checkId) {
        // 重启之后缓存可能是空的
        Long finishedTask;
        Object total2 = hashOperations.get("Content_Check:Id:" + checkId, TOTAL_KEY);
        Long total = total2 == null ? 0L : Long.parseLong(total2.toString());
        if (total == 0) {
            CheckRequest checkRequest = getCheckRequestService().getById(checkId);
            if (checkRequest == null) {
                log.warn("异常数据，检测请求为空。可能数据库中数据已经清理，但是MQ中进行了重新投递，checkId:{}", checkId);
                throw new SystemException(PaperErrorCode.DATA_NOT_EXIST);
            }
            total = Long.valueOf(checkRequest.getTaskNum());
            initTaskTotal(checkId, total);
            // TODO 目前并发只有一个任务，暂不考虑多任务下增加的原子性问题
            finishedTask = getCheckTaskService().findFinishCheckTask(checkId);
            updateFinishedTask(checkId, finishedTask);
        } else {
            finishedTask = hashOperations.increment("Content_Check:Id:" + checkId, COUNT_KEY, 1L);
        }
        return finishedTask != null && finishedTask >= total;
    }

    private void updateFinishedTask(Long checkId, Long count) {
        hashOperations.put("Content_Check:Id:" + checkId, COUNT_KEY, count);
    }
}
