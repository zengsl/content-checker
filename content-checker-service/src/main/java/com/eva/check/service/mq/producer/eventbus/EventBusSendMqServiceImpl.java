package com.eva.check.service.mq.producer.eventbus;

import com.eva.check.pojo.CheckTask;
import com.eva.check.service.mq.common.event.CheckTaskStartEvent;
import com.eva.check.service.mq.producer.SendMqService;
import com.eva.check.service.mq.producer.eventbus.event.*;
import com.eva.check.service.mq.producer.eventbus.listener.CheckTaskEventBusListener;
import com.eva.check.service.mq.producer.eventbus.listener.ContentCheckEventBusListener;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * EventBus 发送实现类
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Slf4j
@RequiredArgsConstructor
public class EventBusSendMqServiceImpl implements SendMqService {

    private final static int  CPU_CORE_NUM = Runtime.getRuntime().availableProcessors();
    private final EventBus eventBus = new AsyncEventBus(new ThreadPoolExecutor(CPU_CORE_NUM * 2, CPU_CORE_NUM * 2 + 5,
            60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100), new ThreadFactoryBuilder()
            .setNameFormat("checkTaskEventBus-%d")
            .build()));

    private final EventBus contentEventBus = new AsyncEventBus(new ThreadPoolExecutor(CPU_CORE_NUM * 2, CPU_CORE_NUM * 2 + 5,
            60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100), new ThreadFactoryBuilder()
            .setNameFormat("contentCheckEventBus-%d")
            .build()));

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
    public void finishTask(CheckTask checkTask) {
        CheckTaskFinishEvent checkTaskFinishEvent = CheckTaskFinishEvent.builder().checkTask(checkTask).build();
        eventBus.post(checkTaskFinishEvent);
    }

    @Override
    public void cancelTask(CheckTask checkTask) {
        CheckTaskCancelEvent checkTaskCancelEvent = CheckTaskCancelEvent.builder().checkTask(checkTask).build();
        eventBus.post(checkTaskCancelEvent);
    }

    @Override
    public void doContentPreCheck(CheckTask checkTask) {
        PreCheckEvent preCheckEvent = PreCheckEvent.builder()
                .checkTask(checkTask)
                .build();
        this.contentEventBus.post(preCheckEvent);
    }

    @Override
    public void doParagraphCheck(CheckTask checkTask) {
        CheckParagraphEvent checkParagraphEvent = CheckParagraphEvent.builder()
                .checkTask(checkTask)
                .build();
        this.contentEventBus.post(checkParagraphEvent);
    }

    @Override
    public void doCollectResult(CheckTask checkTask) {
        CollectResultEvent event = CollectResultEvent.builder()
                .checkTask(checkTask)
                .build();
        this.contentEventBus.post(event);
    }

    @Override
    public void doGenerateReport(CheckTask checkTask) {

    }
}
