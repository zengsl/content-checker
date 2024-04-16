package com.eva.check.service.core.impl;

import com.eva.check.pojo.CheckRequest;
import com.eva.check.service.core.DuplicateCheckPrepareService;
import com.eva.check.service.support.CheckRequestService;
import com.eva.check.service.support.CheckTaskService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 默认检测任务执行器
 * 内存级别
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class DefaultCheckTaskExecuteServiceImpl extends BaseCheckTaskExecuteService {


    public DefaultCheckTaskExecuteServiceImpl(CheckRequestService checkRequestService, CheckTaskService checkTaskService, DuplicateCheckPrepareService duplicateCheckPrepareService) {
        super(checkRequestService, checkTaskService, duplicateCheckPrepareService);
    }

    LoadingCache<Long, Long> checkRequestCache = CacheBuilder.newBuilder().maximumSize(3)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                @Nullable
                public Long load(@Nonnull Long key) {
                    CheckRequest checkRequest = getCheckRequestService().getById(key);
                    return Long.valueOf(checkRequest.getTaskNum());
                }
            });

    LoadingCache<Long, Long> checkTaskCache = CacheBuilder.newBuilder().maximumSize(3)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                @Nullable
                public Long load(@Nonnull Long key) {
                    // 这里初始化的时候减1，因为这里是从数据库查询最新的情况，而执行当前操作前会对checkTask执行finish或者cancel的操作，从数据库查询的时候已经记录进去了当前这次；而如果是直接从缓存中获取的话则需要手动修改缓存进行+1。所以为了统一操作，从数据库中获取时先-1。
                    return getCheckTaskService().findFinishCheckTask(key) - 1;
                }
            });

    @Override
    protected void initTaskTotal(Long checkId, Long total) {
        // 设置检测请求总任务数
        checkRequestCache.put(checkId, total);
    }

    @Override
    protected boolean isTaskFinishedAfterAdd(Long checkId) {

        long finishedTask;
        long total;
        try {
            total = checkRequestCache.get(checkId);
        } catch (ExecutionException e) {
            log.error("获取检测请求总任务数失败", e);
            throw new RuntimeException(e);
        }

        try {
            // TODO 目前并发只有一个任务，暂不考虑多任务下增加的原子性问题
            finishedTask = checkTaskCache.get(checkId) + 1;
            checkTaskCache.put(checkId, finishedTask);
        } catch (ExecutionException e) {
            log.error("获取已完成任务数量失败", e);
            throw new RuntimeException(e);
        }

        return finishedTask >= total;
    }
}
