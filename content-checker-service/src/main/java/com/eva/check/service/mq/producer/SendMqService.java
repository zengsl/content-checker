package com.eva.check.service.mq.producer;

import com.eva.check.service.event.*;

/**
 * MQ发送服务
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
public interface SendMqService {

    /**
     * 开启任务
     *
     * @param checkTaskStartEvent 检测任务开始事件
     */
    void startTask(CheckTaskStartEvent checkTaskStartEvent);

    /**
     * 结束任务
     *
     * @param checkTaskFinishEvent 检测任务结束事件
     */
    void finishTask(CheckTaskFinishEvent checkTaskFinishEvent);

    /**
     * 结束任务
     *
     * @param checkTaskCancelEvent 检测任务取消事件
     */
    void cancelTask(CheckTaskCancelEvent checkTaskCancelEvent);

    void doContentPreCheck(PreCheckEvent preCheckEvent);

    void doParagraphCheck(CheckParagraphEvent checkParagraphEvent);

    void doCollectResult(CollectResultEvent collectResultEvent);

    void doGenerateReport(CollectResultEvent collectResultEvent);

    /*    void sendPreCompare();*/
}
