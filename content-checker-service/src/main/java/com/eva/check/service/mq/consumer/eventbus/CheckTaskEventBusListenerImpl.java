package com.eva.check.service.mq.consumer.eventbus;


import com.eva.check.service.core.CheckTaskExecutor;
import com.eva.check.service.event.CheckTaskCancelEvent;
import com.eva.check.service.event.CheckTaskFinishEvent;
import com.eva.check.service.event.CheckTaskStartEvent;
import com.eva.check.service.mq.producer.eventbus.listener.CheckTaskEventBusListener;
import com.google.common.eventbus.Subscribe;
import lombok.RequiredArgsConstructor;

/**
 * 检测任务事件监听器
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@RequiredArgsConstructor
public class CheckTaskEventBusListenerImpl implements CheckTaskEventBusListener {

    private final CheckTaskExecutor checkTaskExecutor;

    @Subscribe
    @Override
    public void onEvent(CheckTaskStartEvent checkTaskStartEvent) {
        this.checkTaskExecutor.startAllTask(checkTaskStartEvent.getCheckId(), checkTaskStartEvent.getCheckTasks());
    }

    @Subscribe
    @Override
    public void onEvent(CheckTaskFinishEvent checkTaskFinishEvent) {
        this.checkTaskExecutor.finishTask(checkTaskFinishEvent.getCheckTask());
    }

    @Subscribe
    @Override
    public void onEvent(CheckTaskCancelEvent checkTaskCancelEvent) {
        this.checkTaskExecutor.cancelTask(checkTaskCancelEvent.getCheckTask());
    }
}
