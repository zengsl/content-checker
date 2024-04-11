package com.eva.check.service.mq.consumer.rocket;

import com.eva.check.common.constant.MessageQueueConstants;
import com.eva.check.pojo.CheckTask;
import com.eva.check.service.core.DuplicateCheckService;
import com.eva.check.service.mq.common.constant.MqQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @author zengsl
 * @date 2024/4/11 17:19
 */
@Slf4j
public class ContentCheckRocketListenerImpl {

    public ContentCheckRocketListenerImpl() {
    }

    @ConditionalOnProperty(prefix = "content-check",name = "mq", havingValue = MessageQueueConstants.ROCKET_MQ)
    @RocketMQMessageListener(topic = MqQueue.CONTENT_CHECK_TOPIC,
            selectorExpression= MqQueue.CONTENT_PRE_CHECK_TAG,
            consumerGroup = MqQueue.CONTENT_PRE_CHECK_CONSUMER_GROUP
    )
    @Service
    @RequiredArgsConstructor
    public static class ConsumerContentPreCheck implements RocketMQListener<CheckTask> {

        private final DuplicateCheckService duplicateCheckService;

        @Override
        public void onMessage(CheckTask checkTask) {
            log.info("ConsumerContentPreCheck 消费消息:{}", checkTask);
            this.duplicateCheckService.findSimilarParagraph(checkTask);
        }
    }

    @ConditionalOnProperty(prefix = "content-check",name = "mq", havingValue = MessageQueueConstants.ROCKET_MQ)
    @RocketMQMessageListener(topic = MqQueue.CONTENT_CHECK_TOPIC,
            selectorExpression= MqQueue.PARAGRAPH_CHECK_TAG,
            consumerGroup = MqQueue.PARAGRAPH_CHECK_CONSUMER_GROUP
    )
    @Service
    @RequiredArgsConstructor
    public static class ConsumerParagraphCheck implements RocketMQListener<CheckTask> {
        private final DuplicateCheckService duplicateCheckService;

        @Override
        public void onMessage(CheckTask checkTask) {
            log.info("ConsumerParagraphCheck 消费消息:{}", checkTask);
            this.duplicateCheckService.doPragraphCheck(checkTask);
        }
    }

    @ConditionalOnProperty(prefix = "content-check",name = "mq", havingValue = MessageQueueConstants.ROCKET_MQ)
    @Service
    @RocketMQMessageListener(topic = MqQueue.CONTENT_CHECK_TOPIC,
            selectorExpression= MqQueue.COLLECT_RESULT_TAG,
            consumerGroup = MqQueue.COLLECT_RESULT_CONSUMER_GROUP
    )
    @RequiredArgsConstructor
    public static class ConsumerCollectResult implements RocketMQListener<CheckTask> {
        private final DuplicateCheckService duplicateCheckService;

        @Override
        public void onMessage(CheckTask checkTask) {
            log.info("ConsumerCollectResult 消费消息:{}", checkTask);
            this.duplicateCheckService.collectResult(checkTask);
        }
    }

    @ConditionalOnProperty(prefix = "content-check",name = "mq", havingValue = MessageQueueConstants.ROCKET_MQ)
    @Service
    @RocketMQMessageListener(topic = MqQueue.CONTENT_CHECK_TOPIC,
            selectorExpression= MqQueue.GENERATE_REPORT_TAG,
            consumerGroup = MqQueue.GENERATE_REPORT_CONSUMER_GROUP
    )
    public static class ConsumerGenerateReport implements RocketMQListener<CheckTask> {
        @Override
        public void onMessage(CheckTask message) {
            log.info("ConsumerGenerateReport 消费消息:{}", message);
        }
    }
}
