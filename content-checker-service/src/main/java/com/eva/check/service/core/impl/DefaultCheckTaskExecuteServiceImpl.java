package com.eva.check.service.core.impl;

import com.eva.check.common.enums.DataType;
import com.eva.check.pojo.CheckRequest;
import com.eva.check.pojo.CheckTask;
import com.eva.check.pojo.dto.MqCheckTask;
import com.eva.check.service.core.CheckTaskExecuteService;
import com.eva.check.service.core.DuplicateCheckPrepareService;
import com.eva.check.service.support.CheckRequestService;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 默认检测任务执行器
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class DefaultCheckTaskExecuteServiceImpl implements CheckTaskExecuteService {
    private final CheckRequestService checkRequestService;
    private final com.eva.check.service.support.CheckTaskService checkTaskService;
    private final DuplicateCheckPrepareService duplicateCheckPrepareService;
    private final Map<Long, Integer> checkRequestMap = Maps.newConcurrentMap();
    private final Map<Long, AtomicInteger> checkTaskCountMap = Maps.newConcurrentMap();


    @Override
    public void startAllTask(Long checkId, List<CheckTask> checkTaskList) {
        // 设置检测请求总任务数
        checkRequestMap.put(checkId, checkTaskList.size());

        // 可以根据不同的内容进行准备工作 分发任务
        // 执行准备工作
        checkTaskList.forEach(checkTask -> {
            // 目前只处理正文内容比对
            if (DataType.FULL_TEXT.getValue().equals(checkTask.getCheckType())) {
                this.duplicateCheckPrepareService.execute(checkTask);
            } else {
                checkTask.setSimilarity(0D);
                this.cancelTask(checkTask);
            }
        });
    }

    @Override
    public void finishTask(CheckTask checkTask) {
        this.checkTaskService.finishTask(checkTask);
        updateCheckRequest(checkTask);
    }

    @Override
    public void cancelTask(CheckTask checkTask) {
        this.checkTaskService.cancelTask(checkTask);
        updateCheckRequest(checkTask);
    }

    private void updateCheckRequest(CheckTask checkTask) {
        // 分布式部署的话需要考虑并发问题 这种缓存需要优化，要设置过期事件等。
        AtomicInteger atomicInteger = checkTaskCountMap.computeIfAbsent(checkTask.getCheckId(), id -> new AtomicInteger(0));
        // 已完成任务数 +1
        int finishedTask;
        Integer total = checkRequestMap.get(checkTask.getCheckId());
        // 重启之后缓存可能是空的
        if (total == null) {
            CheckRequest checkRequest = this.checkRequestService.getByCheckNo(checkTask.getCheckNo());
            log.info("Check request checkNo:{}", checkTask.getCheckNo());
            total = checkRequest.getTaskNum();
            checkRequestMap.put(checkRequest.getCheckId(), total);
            finishedTask = this.checkTaskService.findFinishCheckTask(checkTask.getCheckNo());
        } else {
            finishedTask = atomicInteger.addAndGet(1);
        }

        if (finishedTask >= total) {
            this.checkRequestService.finishAndCollectResult(checkTask.getCheckId());
            // 触发报告生成任务
        }
    }
}
