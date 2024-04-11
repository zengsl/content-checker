package com.eva.check.service.mq.producer.rocket;

import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;

/**
 * RocketMQ 发送实现类
 *
 * @author zengsl
 * @date 2024/4/11 09:34
 */
@RequiredArgsConstructor
public class RocketSendMqServiceImpl   {

    private final RocketMQTemplate rocketMQTemplate;


}
