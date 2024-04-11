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

    /**
     * 内容预检，快速查询疑似相似项目
     *
     * @param preCheckEvent 预检事件
     */
    void doContentPreCheck(PreCheckEvent preCheckEvent);

    /**
     * 段落检查，计算相似度
     *
     * @param checkParagraphEvent 段落检测事件
     */
    void doParagraphCheck(CheckParagraphEvent checkParagraphEvent);

    /**
     * 段落检查，计算相似度
     *
     * @param collectResultEvent 收集结果事件
     */
    void doCollectResult(CollectResultEvent collectResultEvent);

    /**
     * 生成报告
     *
     * @param collectResultEvent 收集结果事件
     */
    void doGenerateReport(CollectResultEvent collectResultEvent);

}
