package com.eva.check.service.flow.impl;

import com.eva.check.common.constant.CacheConstant;
import com.eva.check.pojo.CheckTask;
import com.eva.check.service.flow.ICheckTaskBaseFlow;
import com.eva.check.service.flow.IProcessLogService;
import com.eva.check.service.mq.producer.SendMqService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 检测任务基础流程
 *
 * @author zengsl
 * @date 2024/4/11 14:43
 */
@Getter
@RequiredArgsConstructor
abstract public class BaseCheckTaskFlow implements ICheckTaskBaseFlow {

    private final SendMqService sendMqService;
    private final IProcessLogService processLogService;

    @Override
    public void processCancel(CheckTask checkTask) {
        this.processLogService.log(CacheConstant.CHECK_PROCESS_LOG_CACHE_KEY, checkTask.getCheckId(), "检测任务：" + checkTask.getTaskId() + " 取消");
        this.sendMqService.cancelTask(checkTask);
    }

    @Override
    public void processFinish(CheckTask checkTask) {
        this.processLogService.log(CacheConstant.CHECK_PROCESS_LOG_CACHE_KEY, checkTask.getCheckId(), "检测任务：" + checkTask.getTaskId() + " 完成");
        this.sendMqService.finishTask(checkTask);
    }

}
