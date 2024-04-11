package com.eva.check.service.mq.producer.rocket;

import cn.hutool.core.lang.id.NanoId;
import com.eva.check.common.util.JacksonUtil;
import com.eva.check.pojo.CheckTask;
import com.eva.check.service.mq.common.constant.MqQueue;
import com.eva.check.service.mq.common.event.CheckTaskStartEvent;
import com.eva.check.service.mq.producer.SendMqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.support.MessageBuilder;

import java.util.StringJoiner;

/**
 * RocketMQ 发送实现类
 *
 * @author zengsl
 * @date 2024/4/11 09:34
 */
@Slf4j
@RequiredArgsConstructor
public class RocketSendMqServiceImpl implements SendMqService {

    private final RocketMQTemplate rocketMQTemplate;

    @Override
    public void startTask(CheckTaskStartEvent checkTaskStartEvent) {
        sendMessage(MqQueue.START_TASK_TAG, checkTaskStartEvent);
    }

    @Override
    public void finishTask(CheckTask checkTask) {
        sendMessage(MqQueue.FINISH_TASK_TAG, checkTask);

    }

    @Override
    public void cancelTask(CheckTask checkTask) {
        sendMessage(MqQueue.CANCEL_TASK_TAG, checkTask);

    }

    @Override
    public void doContentPreCheck(CheckTask checkTask) {
        sendMessage(MqQueue.CONTENT_PRE_CHECK_TAG, checkTask);

    }

    @Override
    public void doParagraphCheck(CheckTask checkTask) {
        sendMessage(MqQueue.PARAGRAPH_CHECK_TAG, checkTask);

    }

    @Override
    public void doCollectResult(CheckTask checkTask) {
        sendMessage(MqQueue.COLLECT_RESULT_TAG, checkTask);

    }

    @Override
    public void doGenerateReport(CheckTask checkTask) {
        sendMessage(MqQueue.GENERATE_REPORT_TAG, checkTask);

    }

    <T> void sendMessage(String tag, T checkTask) {
        String messageKey = NanoId.randomNanoId();
        String jsonValue = buildJson(checkTask);
        /*log.info("send message: {}; messageKey:{}", jsonValue, messageKey);*/
        String destination = String.join(":", MqQueue.CONTENT_CHECK_TOPIC, tag);
        this.rocketMQTemplate.send(destination, MessageBuilder.withPayload(jsonValue).setHeader(RocketMQHeaders.KEYS, messageKey).build());
    }

    <T> String buildJson(T checkTask) {
        return JacksonUtil.obj2String(checkTask);
    }
}
