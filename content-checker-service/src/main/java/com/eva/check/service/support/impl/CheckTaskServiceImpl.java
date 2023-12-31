package com.eva.check.service.support.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eva.check.common.enums.CheckTaskStatus;
import com.eva.check.mapper.CheckTaskMapper;
import com.eva.check.pojo.CheckTask;
import com.eva.check.service.support.CheckTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 针对表【check_task(论文信息)】的数据库操作Service实现
 *
 * @author zzz
 * @date 2023-11-13 12:11:00
 */
@Service
@RequiredArgsConstructor
public class CheckTaskServiceImpl extends ServiceImpl<CheckTaskMapper, CheckTask> implements CheckTaskService {


    @Override
    public void dispatchTask(List<CheckTask> checkTaskList) {

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void finishTask(CheckTask checkTask) {
        checkTask.setStatus(CheckTaskStatus.DONE.getValue());
        this.updateById(checkTask);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void cancelTask(CheckTask checkTask) {
        checkTask.setStatus(CheckTaskStatus.CANCEL.getValue());
        this.updateById(checkTask);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CheckTask> findByCheckId(Long checkId) {
        LambdaQueryWrapper<CheckTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CheckTask::getCheckId, checkId);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CheckTask findCheckTask(String checkNo, String checkType) {
        LambdaQueryWrapper<CheckTask> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CheckTask::getCheckNo, checkNo);
        queryWrapper.eq(CheckTask::getCheckType, checkType);
        return this.baseMapper.selectOne(queryWrapper);
    }
}




