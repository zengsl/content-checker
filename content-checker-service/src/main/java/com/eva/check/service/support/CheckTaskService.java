package com.eva.check.service.support;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.eva.check.common.enums.CheckTaskStatus;
import com.eva.check.common.enums.DataType;
import com.eva.check.pojo.CheckTask;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(rollbackFor = Exception.class)
    default CheckTask findContentCheckTask(String checkNo) {
        return this.findCheckTask(checkNo, DataType.FULL_TEXT.getValue());
    }

    CheckTask findCheckTask(String checkNo, String checkType);

    List<CheckTask> findCheckTask(String checkNo, List<String> statusList);
    List<CheckTask> findCheckTask(Long checkId, List<String> statusList);

    default Integer findFinishCheckTask(String checkNo) {
        List<CheckTask> checkTaskList = this.findCheckTask(checkNo, List.of(CheckTaskStatus.DONE.getValue(), CheckTaskStatus.CANCEL.getValue()));
        return CollectionUtil.isEmpty(checkTaskList) ? 0 : checkTaskList.size();
    }

    default Long findFinishCheckTask(Long checkId) {
        List<CheckTask> checkTaskList = this.findCheckTask(checkId, List.of(CheckTaskStatus.DONE.getValue(), CheckTaskStatus.CANCEL.getValue()));
        return CollectionUtil.isEmpty(checkTaskList) ? 0 :  Long.valueOf(checkTaskList.size());
    }
}
