package com.eva.check.service.mq.consumer.rocket;

import com.eva.check.common.constant.MessageQueueConstants;
import com.eva.check.pojo.CheckTask;
import com.eva.check.service.core.CheckTaskExecuteService;
import com.eva.check.service.mq.common.constant.MqQueue;
import com.eva.check.service.mq.common.event.CheckTaskStartEvent;
import com.eva.check.service.support.CheckTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DuplicateKeyException;
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
            consumerGroup = MqQueue.START_TASK_CONSUMER_GROUP,
            maxReconsumeTimes = MqQueue.MAX_RECONSUME_TIMES
    )
    @Service
    @RequiredArgsConstructor
    public static class ConsumerStartTask implements RocketMQListener<CheckTaskStartEvent> {
        private final CheckTaskExecuteService checkTaskExecuteService;

        private final CheckTaskService checkTaskService;

        @Override
        public void onMessage(CheckTaskStartEvent checkTaskStartEvent) {
            log.info("ConsumerStartTask 消费消息:{}", checkTaskStartEvent);
            try {
                checkTaskStartEvent.getCheckTasks().forEach(checkTask -> {
                    String content = checkTaskService.getCheckTaskContentFromCache(checkTask.getTaskId());
                    checkTask.setContent(content);
                });
                // TODO 保障幂等
                this.checkTaskExecuteService.startAllTask(checkTaskStartEvent.getCheckId(), checkTaskStartEvent.getCheckTasks());
            } catch (DuplicateKeyException e) {
                log.warn("疑似因为消息重复消费，导致发生重复Key异常。对该异常进行捕获，防止向外抛出从而引起MQ重试", e);
            }
        }
    }

    @ConditionalOnProperty(prefix = "content-check",name = "mq", havingValue = MessageQueueConstants.ROCKET_MQ)
    @RocketMQMessageListener(topic = MqQueue.CONTENT_CHECK_TOPIC,
            selectorExpression= MqQueue.FINISH_TASK_TAG,
            consumerGroup = MqQueue.FINISH_TASK_CONSUMER_GROUP,
            maxReconsumeTimes = MqQueue.MAX_RECONSUME_TIMES
    )
    @Service
    @RequiredArgsConstructor
    public static class ConsumerFinishTask implements RocketMQListener<CheckTask> {
        private final CheckTaskExecuteService checkTaskExecuteService;

        @Override
        public void onMessage(CheckTask checkTask) {
            log.info("ConsumerFinishTask 消费消息:{}", checkTask);
            try {
                // TODO 保障幂等
                this.checkTaskExecuteService.finishTask(checkTask);
            } catch (DuplicateKeyException e) {
                log.warn("疑似因为消息重复消费，导致发生重复Key异常。对该异常进行捕获，防止向外抛出从而引起MQ重试", e);
            }
        }
    }

    @ConditionalOnProperty(prefix = "content-check",name = "mq", havingValue = MessageQueueConstants.ROCKET_MQ)
    @RocketMQMessageListener(topic = MqQueue.CONTENT_CHECK_TOPIC,
            selectorExpression= MqQueue.CANCEL_TASK_TAG,
            consumerGroup = MqQueue.CANCEL_TASK_CONSUMER_GROUP,
            maxReconsumeTimes = MqQueue.MAX_RECONSUME_TIMES
    )
    @Service
    @RequiredArgsConstructor
    public static class ConsumerCancelTask implements RocketMQListener<CheckTask> {
        private final CheckTaskExecuteService checkTaskExecuteService;

        @Override
        public void onMessage(CheckTask checkTask) {
            log.info("ConsumerCancelTask 消费消息:{}", checkTask);
            try {
                // TODO 保障幂等
                this.checkTaskExecuteService.cancelTask(checkTask);
            } catch (DuplicateKeyException e) {
                log.warn("疑似因为消息重复消费，导致发生重复Key异常。对该异常进行捕获，防止向外抛出从而引起MQ重试", e);
            }
        }
    }
}
