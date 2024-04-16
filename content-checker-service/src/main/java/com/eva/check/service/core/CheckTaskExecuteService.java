package com.eva.check.service.core;

import com.eva.check.pojo.CheckTask;

import java.util.List;

/**
 * 检测任务调度器
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
public interface CheckTaskExecuteService {
    /**
     * 开启所有任务
     *
     * @param checkId       检测ID
     * @param checkTaskList 任务列表
     */
    void startAllTask(Long checkId, List<CheckTask> checkTaskList);

    /**
     * 结束任务
     *
     * @param checkTask       任务
     */
    void finishTask(CheckTask checkTask);

    /**
     * 取消任务
     *
     * @param checkTask       任务
     */
    void cancelTask(CheckTask checkTask);
}
