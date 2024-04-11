package com.eva.check.service.core;

import com.eva.check.pojo.CheckTask;

import java.util.List;

/**
 * 检测任务调度器
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
public interface CheckTaskService {
    void startAllTask(Long checkId,List<CheckTask> checkTaskList);
    void finishTask(CheckTask checkTask);
    void cancelTask(CheckTask checkTask);
}
