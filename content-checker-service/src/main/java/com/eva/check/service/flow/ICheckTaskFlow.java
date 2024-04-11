package com.eva.check.service.flow;

import com.eva.check.pojo.CheckTask;

import java.util.List;

/**
 * 检测任务流程接口
 *
 * @author zengsl
 * @date 2024/4/11 15:34
 */
public interface ICheckTaskFlow extends ICheckTaskBaseFlow {

    /**
     * 执行所有检测任务
     *
     * @param checkId       检测ID
     * @param checkTaskList 所有检测任务
     */
    void processAllTask(Long checkId, List<CheckTask> checkTaskList);
}
