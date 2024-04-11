package com.eva.check.service.flow.impl;

import com.eva.check.pojo.CheckTask;
import com.eva.check.service.flow.ICheckTaskBaseFlow;
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

    @Override
    public void processCancel(CheckTask checkTask) {
        this.sendMqService.cancelTask(checkTask);
    }

    @Override
    public void processFinish(CheckTask checkTask) {
        this.sendMqService.finishTask(checkTask);
    }

}
