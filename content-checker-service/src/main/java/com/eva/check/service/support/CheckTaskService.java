package com.eva.check.service.support;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eva.check.pojo.CheckTask;

import java.util.List;

/**
* @author zzz
* @description 针对表【check_task(论文信息)】的数据库操作Service
* @createDate 2023-11-13 12:11:00
*/
public interface CheckTaskService extends IService<CheckTask> {

    void finishTask(CheckTask checkTask);
    void cancelTask(CheckTask checkTask);

    void dispatchTask(List<CheckTask> checkTaskList);

    List<CheckTask> findByCheckId(Long checkId);
}
