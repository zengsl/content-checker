package com.eva.check.service.mq.producer.eventbus.listener;


import com.eva.check.service.event.CheckTaskCancelEvent;
import com.eva.check.service.event.CheckTaskFinishEvent;
import com.eva.check.service.event.CheckTaskStartEvent;

/**
 * EventBus监听器
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
public interface CheckTaskEventBusListener {

    /**
     * 监听事件
     *
     * @param checkTaskStartEvent 检测任务开始事件
     */
    void onEvent(CheckTaskStartEvent checkTaskStartEvent);

    /**
     * 监听事件
     *
     * @param checkTaskFinishEvent 检测任务结束事件
     */
    void onEvent(CheckTaskFinishEvent checkTaskFinishEvent);

    /**
     * 监听事件
     *
     * @param checkTaskCancelEvent 检测任务取消事件
     */
    void onEvent(CheckTaskCancelEvent checkTaskCancelEvent);
}
