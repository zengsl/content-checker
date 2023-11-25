package com.eva.check.service.mq.producer.eventbus;

import com.eva.check.service.event.*;
import com.eva.check.service.mq.producer.SendMqService;
import com.eva.check.service.mq.producer.eventbus.listener.CheckTaskEventBusListener;
import com.eva.check.service.mq.producer.eventbus.listener.ContentCheckEventBusListener;
import com.google.common.eventbus.EventBus;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * EventBus 发送实现类
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Slf4j
@RequiredArgsConstructor
public class EventBusSendMqServiceImpl implements SendMqService {

    private final EventBus eventBus = new EventBus();
    private final EventBus contentEventBus = new EventBus();

    private final CheckTaskEventBusListener checkTaskEventBusListener;
    private final ContentCheckEventBusListener contentCheckEventBusListener;

    @PostConstruct
    public void init() {
        eventBus.register(checkTaskEventBusListener);
        contentEventBus.register(contentCheckEventBusListener);
    }

    @Override
    public void startTask(CheckTaskStartEvent checkTaskStartEvent) {
        this.eventBus.post(checkTaskStartEvent);
    }

    @Override
    public void finishTask(CheckTaskFinishEvent checkTaskFinishEvent) {
        eventBus.post(checkTaskFinishEvent);
    }

    @Override
    public void cancelTask(CheckTaskCancelEvent checkTaskCancelEvent) {
        eventBus.post(checkTaskCancelEvent);
    }
    @Override
    public void doContentPreCheck(PreCheckEvent preCheckEvent) {
        this.contentEventBus.post(preCheckEvent);
    }

    @Override
    public void doParagraphCheck(CheckParagraphEvent checkParagraphEvent) {
        this.contentEventBus.post(checkParagraphEvent);
    }

    @Override
    public void doCollectResult(CollectResultEvent collectResultEvent) {
        this.contentEventBus.post(collectResultEvent);
    }

    @Override
    public void doGenerateReport(CollectResultEvent collectResultEvent) {

    }
}
