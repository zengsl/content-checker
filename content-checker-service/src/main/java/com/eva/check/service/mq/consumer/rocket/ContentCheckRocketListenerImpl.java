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
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * 内容检测Rocket监听器
 *
 * @author zengsl
 * @date 2024/4/11 17:19
 */
@Slf4j
public class ContentCheckRocketListenerImpl {

    public ContentCheckRocketListenerImpl() {
    }

    @ConditionalOnProperty(prefix = "content-check", name = "mq", havingValue = MessageQueueConstants.ROCKET_MQ)
    @RocketMQMessageListener(topic = MqQueue.CONTENT_CHECK_TOPIC,
            selectorExpression = MqQueue.CONTENT_PRE_CHECK_TAG,
            consumerGroup = MqQueue.CONTENT_PRE_CHECK_CONSUMER_GROUP,
            maxReconsumeTimes = MqQueue.MAX_RECONSUME_TIMES
    )
    @Service
    @RequiredArgsConstructor
    public static class ConsumerContentPreCheck implements RocketMQListener<CheckTask> {

        private final DuplicateCheckService duplicateCheckService;

        @Override
        public void onMessage(CheckTask checkTask) {
            log.info("ConsumerContentPreCheck 消费消息:{}", checkTask);
            try {
                this.duplicateCheckService.findSimilarParagraph(checkTask);
            } catch (DuplicateKeyException e) {
                log.warn("疑似因为消息重复消费，导致发生重复Key异常。对该异常进行捕获，防止向外抛出从而引起MQ重试", e);
            }
        }
    }

    @ConditionalOnProperty(prefix = "content-check", name = "mq", havingValue = MessageQueueConstants.ROCKET_MQ)
    @RocketMQMessageListener(topic = MqQueue.CONTENT_CHECK_TOPIC,
            selectorExpression = MqQueue.PARAGRAPH_CHECK_TAG,
            consumerGroup = MqQueue.PARAGRAPH_CHECK_CONSUMER_GROUP,
            maxReconsumeTimes = MqQueue.MAX_RECONSUME_TIMES
    )
    @Service
    @RequiredArgsConstructor
    public static class ConsumerParagraphCheck implements RocketMQListener<CheckTask> {
        private final DuplicateCheckService duplicateCheckService;

        @Override
        public void onMessage(CheckTask checkTask) {
            log.info("ConsumerParagraphCheck 消费消息:{}", checkTask);
            try {
                this.duplicateCheckService.doPragraphCheck(checkTask);
            } catch (DuplicateKeyException e) {
                log.warn("疑似因为消息重复消费，导致发生重复Key异常。对该异常进行捕获，防止向外抛出从而引起MQ重试。checkTask:{}", checkTask, e);
            }
        }
    }

    @ConditionalOnProperty(prefix = "content-check", name = "mq", havingValue = MessageQueueConstants.ROCKET_MQ)
    @Service
    @RocketMQMessageListener(topic = MqQueue.CONTENT_CHECK_TOPIC,
            selectorExpression = MqQueue.COLLECT_RESULT_TAG,
            consumerGroup = MqQueue.COLLECT_RESULT_CONSUMER_GROUP,
            maxReconsumeTimes = MqQueue.MAX_RECONSUME_TIMES
    )
    @RequiredArgsConstructor
    public static class ConsumerCollectResult implements RocketMQListener<CheckTask> {
        private final DuplicateCheckService duplicateCheckService;

        @Override
        public void onMessage(CheckTask checkTask) {
            log.info("ConsumerCollectResult 消费消息:{}", checkTask);
            try {
                this.duplicateCheckService.collectResult(checkTask);
            } catch (DuplicateKeyException e) {
                log.warn("疑似因为消息重复消费，导致发生重复Key异常。对该异常进行捕获，防止向外抛出从而引起MQ重试", e);
            }
        }
    }

    @ConditionalOnProperty(prefix = "content-check", name = "mq", havingValue = MessageQueueConstants.ROCKET_MQ)
    @Service
    @RocketMQMessageListener(topic = MqQueue.CONTENT_CHECK_TOPIC,
            selectorExpression = MqQueue.GENERATE_REPORT_TAG,
            consumerGroup = MqQueue.GENERATE_REPORT_CONSUMER_GROUP,
            maxReconsumeTimes = MqQueue.MAX_RECONSUME_TIMES
    )
    public static class ConsumerGenerateReport implements RocketMQListener<CheckTask> {
        @Override
        public void onMessage(CheckTask message) {
            log.info("ConsumerGenerateReport 消费消息:{}", message);
        }
    }
}
