package com.eva.check.service.flow.impl;

import com.eva.check.common.constant.CacheConstant;
import com.eva.check.pojo.CheckTask;
import com.eva.check.service.flow.ICheckTaskFlow;
import com.eva.check.service.flow.IProcessLogService;
import com.eva.check.service.mq.common.event.CheckTaskStartEvent;
import com.eva.check.service.mq.producer.SendMqService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 检测任务流程
 *
 * @author zengsl
 * @date 2024/4/11 14:55
 */
@Service
public class CheckTaskFlow extends BaseCheckTaskFlow implements ICheckTaskFlow {

    public CheckTaskFlow(SendMqService sendMqService, IProcessLogService processLogService) {
        super(sendMqService, processLogService);
    }

    @Override
    public void processAllTask(Long checkId, List<CheckTask> checkTaskList) {
//        List<MqCheckTask> mqCheckTaskList = CheckTaskConverter.INSTANCE.checkTask2MqCheckTask(checkTaskList);
        // 将任务推送MQ 进行异步处理
        CheckTaskStartEvent checkTaskStartEvent = CheckTaskStartEvent.builder()
                .checkTasks(checkTaskList)
                .checkId(checkId)
                .taskNum(checkTaskList.size())
                .build();
        this.getProcessLogService().log(CacheConstant.CHECK_PROCESS_LOG_CACHE_KEY, checkId, "检测请求：" + checkId + " 开启所有任务");
        // 开启任务
        this.getSendMqService().startTask(checkTaskStartEvent);
    }
}
