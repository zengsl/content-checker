package com.eva.check.service.core.impl;

import com.eva.check.common.enums.DataType;
import com.eva.check.pojo.CheckTask;
import com.eva.check.service.core.CheckTaskExecuteService;
import com.eva.check.service.core.DuplicateCheckPrepareService;
import com.eva.check.service.support.CheckReportService;
import com.eva.check.service.support.CheckRequestService;
import com.eva.check.service.support.CheckTaskService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 基础检测任务调度器
 *
 * @author zengsl
 * @date 2024/4/16 15:07
 */
@RequiredArgsConstructor
@Getter
abstract public class BaseCheckTaskExecuteService implements CheckTaskExecuteService {

    private final CheckRequestService checkRequestService;
    private final CheckTaskService checkTaskService;
    private final DuplicateCheckPrepareService duplicateCheckPrepareService;
    private final CheckReportService checkReportService;

    @Override
    public void startAllTask(Long checkId, List<CheckTask> checkTaskList) {
        // 设置检测请求总任务数
        initTaskTotal(checkId, (long) checkTaskList.size());

        // 可以根据不同的内容进行准备工作 分发任务
        // 执行准备工作
        checkTaskList.forEach(checkTask -> {
            // 目前只处理正文内容比对
            if (DataType.FULL_TEXT.getValue().equals(checkTask.getCheckType())) {
                // TODO 异步线程
                this.duplicateCheckPrepareService.execute(checkTask);
            } else {
                checkTask.setSimilarity(0D);
                this.cancelTask(checkTask);
            }
        });
    }

    @Override
    public void finishTask(CheckTask checkTask) {
        this.checkTaskService.finishTask(checkTask);
        updateCheckRequest(checkTask.getCheckId());
    }

    @Override
    public void cancelTask(CheckTask checkTask) {
        this.checkTaskService.cancelTask(checkTask);
        updateCheckRequest(checkTask.getCheckId());
    }

    private void updateCheckRequest(Long checkId) {

        if (isTaskFinishedAfterAdd(checkId)) {
            // 结束检测任务，收集检测结果
            this.checkRequestService.finishAndCollectResult(checkId);
            // 生成检测报告
            this.checkReportService.initCheckReport(checkId);
        }
    }

    /**
     * 初始化任务总数
     *
     * @param checkId 检测ID
     * @param total   任务总数
     */
    abstract protected void initTaskTotal(Long checkId, Long total);

    /**
     * 判断任务是否结束
     *
     * @param checkId 检测ID
     * @return boolean
     */
    abstract protected boolean isTaskFinishedAfterAdd(Long checkId);
}
