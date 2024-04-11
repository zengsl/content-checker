package com.eva.check.service.flow;

import com.eva.check.pojo.CheckTask;
import com.eva.check.service.flow.enums.ContentCheckState;

/**
 * 内容检测流程
 *
 * @author zengsl
 * @date 2024/4/11 14:39
 */
public interface IContentCheckTaskBaseFlow extends ICheckTaskBaseFlow {

    /**
     * 推动状态往下执行
     *
     * @param checkTask    执行任务
     * @param currentState 当前任务
     */
    void processStateNext(CheckTask checkTask, ContentCheckState currentState);

}
