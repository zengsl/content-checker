package com.eva.check.service.mq.producer.rocket;

import com.eva.check.service.event.*;
import com.eva.check.service.mq.producer.SendMqService;
import lombok.RequiredArgsConstructor;

/**
 * RocketMQ 发送实现类
 *
 * @author zengsl
 * @date 2024/4/11 09:34
 */
@RequiredArgsConstructor
public class RocketSendMqServiceImpl implements SendMqService {
    @Override
    public void startTask(CheckTaskStartEvent checkTaskStartEvent) {

    }

    @Override
    public void finishTask(CheckTaskFinishEvent checkTaskFinishEvent) {

    }

    @Override
    public void cancelTask(CheckTaskCancelEvent checkTaskCancelEvent) {

    }

    @Override
    public void doContentPreCheck(PreCheckEvent preCheckEvent) {

    }

    @Override
    public void doParagraphCheck(CheckParagraphEvent checkParagraphEvent) {

    }

    @Override
    public void doCollectResult(CollectResultEvent collectResultEvent) {

    }

    @Override
    public void doGenerateReport(CollectResultEvent collectResultEvent) {

    }
}
