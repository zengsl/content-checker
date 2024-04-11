package com.eva.check.service.flow;

import com.eva.check.pojo.CheckTask;

/**
 * 检测任务基础流程接口
 *
 * @author zengsl
 * @date 2024/4/11 15:34
 */
public interface ICheckTaskBaseFlow {

    /**
     * 取消任务
     *
     * @param checkTask 执行任务
     */
    void processCancel(CheckTask checkTask);

    /**
     * 结束任务
     *
     * @param checkTask 执行任务
     */
    void processFinish(CheckTask checkTask);
}
