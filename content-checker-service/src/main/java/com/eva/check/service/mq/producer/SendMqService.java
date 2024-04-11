package com.eva.check.service.mq.producer;

import com.eva.check.pojo.CheckTask;
import com.eva.check.service.mq.common.event.CheckTaskStartEvent;

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
     * @param checkTask 检测任务
     */
    void finishTask(CheckTask checkTask);

    /**
     * 结束任务
     *
     * @param checkTask 检测任务
     */
    void cancelTask(CheckTask checkTask);

    /**
     * 内容预检，快速查询疑似相似项目
     *
     * @param checkTask 检测任务
     */
    void doContentPreCheck(CheckTask checkTask);

    /**
     * 段落检查，计算相似度
     *
     * @param checkTask 检测任务
     */
    void doParagraphCheck(CheckTask checkTask);

    /**
     * 段落检查，计算相似度
     *
     * @param checkTask 检测任务
     */
    void doCollectResult(CheckTask checkTask);

    /**
     * 生成报告
     *
     * @param checkTask 检测任务
     */
    void doGenerateReport(CheckTask checkTask);

}
