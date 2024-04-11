package com.eva.check.service.mq.consumer.eventbus;

import com.eva.check.service.core.DuplicateCheckService;
import com.eva.check.service.mq.producer.eventbus.event.CheckParagraphEvent;
import com.eva.check.service.mq.producer.eventbus.event.CollectResultEvent;
import com.eva.check.service.mq.producer.eventbus.event.PreCheckEvent;
import com.eva.check.service.mq.producer.eventbus.listener.ContentCheckEventBusListener;
import com.google.common.eventbus.Subscribe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zzz
 * @date 2023/11/25 16:12
 */
@RequiredArgsConstructor
@Slf4j
public class ContentCheckEventBusListenerImpl implements ContentCheckEventBusListener {
    private final DuplicateCheckService duplicateCheckService;

    @Subscribe
    @Override
    public void processEvent(PreCheckEvent preCheckEvent) {
        this.duplicateCheckService.findSimilarParagraph(preCheckEvent.getCheckTask());
    }

    @Subscribe
    @Override
    public void processEvent(CheckParagraphEvent checkParagraphEvent) {
        this.duplicateCheckService.doPragraphCheck(checkParagraphEvent.getCheckTask());
    }

    @Subscribe
    @Override
    public void processEvent(CollectResultEvent collectResultEvent) {
        this.duplicateCheckService.collectResult(collectResultEvent.getCheckTask());
    }
}
