package com.eva.check.service.mq.producer.eventbus.listener;


import com.eva.check.service.mq.producer.eventbus.event.CheckParagraphEvent;
import com.eva.check.service.mq.producer.eventbus.event.CollectResultEvent;
import com.eva.check.service.mq.producer.eventbus.event.PreCheckEvent;

/**
 * EventBus监听器
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
public interface ContentCheckEventBusListener {

    void processEvent(PreCheckEvent preCheckEvent);

    void processEvent(CheckParagraphEvent checkParagraphEvent);

    void processEvent(CollectResultEvent collectResultEvent);

}
