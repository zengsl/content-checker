package com.eva.check.service.mq.consumer.rocket;

import com.eva.check.common.constant.MessageQueueConstants;
import com.eva.check.pojo.CheckTask;
import com.eva.check.service.core.CheckTaskExecuteService;
import com.eva.check.service.mq.common.constant.MqQueue;
import com.eva.check.service.mq.common.event.CheckTaskStartEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @author zengsl
 * @date 2024/4/11 17:35
 */
@Slf4j
@RequiredArgsConstructor
public class CheckTaskRocketListenerImpl {


    @ConditionalOnProperty(prefix = "content-check",name = "mq", havingValue = MessageQueueConstants.ROCKET_MQ)
    @RocketMQMessageListener(topic = MqQueue.CONTENT_CHECK_TOPIC,
            selectorExpression= MqQueue.START_TASK_TAG,
            consumerGroup = MqQueue.START_TASK_CONSUMER_GROUP
    )
    @Service
    @RequiredArgsConstructor
    public static class ConsumerStartTask implements RocketMQListener<CheckTaskStartEvent> {
        private final CheckTaskExecuteService checkTaskExecuteService;

        @Override
        public void onMessage(CheckTaskStartEvent checkTaskStartEvent) {
            log.info("ConsumerStartTask 消费消息:{}", checkTaskStartEvent);
            this.checkTaskExecuteService.startAllTask(checkTaskStartEvent.getCheckId(), checkTaskStartEvent.getCheckTasks());
        }
    }

    @ConditionalOnProperty(prefix = "content-check",name = "mq", havingValue = MessageQueueConstants.ROCKET_MQ)
    @RocketMQMessageListener(topic = MqQueue.CONTENT_CHECK_TOPIC,
            selectorExpression= MqQueue.FINISH_TASK_TAG,
            consumerGroup = MqQueue.FINISH_TASK_CONSUMER_GROUP
    )
    @Service
    @RequiredArgsConstructor
    public static class ConsumerFinishTask implements RocketMQListener<CheckTask> {
        private final CheckTaskExecuteService checkTaskExecuteService;

        @Override
        public void onMessage(CheckTask checkTask) {
            log.info("ConsumerFinishTask 消费消息:{}", checkTask);
            this.checkTaskExecuteService.finishTask(checkTask);
        }
    }

    @ConditionalOnProperty(prefix = "content-check",name = "mq", havingValue = MessageQueueConstants.ROCKET_MQ)
    @RocketMQMessageListener(topic = MqQueue.CONTENT_CHECK_TOPIC,
            selectorExpression= MqQueue.CANCEL_TASK_TAG,
            consumerGroup = MqQueue.CANCEL_TASK_CONSUMER_GROUP
    )
    @Service
    @RequiredArgsConstructor
    public static class ConsumerCancelTask implements RocketMQListener<CheckTask> {
        private final CheckTaskExecuteService checkTaskExecuteService;

        @Override
        public void onMessage(CheckTask checkTask) {
            log.info("ConsumerCancelTask 消费消息:{}", checkTask);
            this.checkTaskExecuteService.cancelTask(checkTask);
        }
    }
}
