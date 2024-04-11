package com.eva.check.service.core;

import com.eva.check.pojo.CheckTask;
import com.eva.check.pojo.dto.MqCheckTask;

import java.util.List;

/**
 * 检测任务调度器
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
public interface CheckTaskExecuteService {
    void startAllTask(Long checkId,List<CheckTask> checkTaskList);
    void finishTask(CheckTask checkTask);
    void cancelTask(CheckTask checkTask);
}
