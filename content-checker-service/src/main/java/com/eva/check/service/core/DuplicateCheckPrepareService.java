package com.eva.check.service.core;

import com.eva.check.pojo.CheckTask;

/**
 * 数据检测准备服务
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
public interface DuplicateCheckPrepareService {

    void execute(CheckTask checkTask);
}
