package com.eva.check.service.core;

import com.eva.check.pojo.CheckTask;

/**
 * 数据检测准备服务
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
public interface DuplicateCheckPrepareService {

    /**
     * 执行方法
     *
     * @param checkTask 检测任务
     */
    void execute(CheckTask checkTask);
}
