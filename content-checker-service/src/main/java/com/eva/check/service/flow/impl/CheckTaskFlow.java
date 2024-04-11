package com.eva.check.service.flow.impl;

import com.eva.check.pojo.CheckTask;
import com.eva.check.pojo.converter.CheckTaskConverter;
import com.eva.check.pojo.dto.MqCheckTask;
import com.eva.check.service.flow.ICheckTaskFlow;
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

    public CheckTaskFlow(SendMqService sendMqService) {
        super(sendMqService);
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
        // 开启任务
        this.getSendMqService().startTask(checkTaskStartEvent);
    }
}
