package com.eva.check.service.support.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eva.check.common.enums.CheckRequestStatus;
import com.eva.check.mapper.CheckRequestMapper;
import com.eva.check.pojo.CheckRequest;
import com.eva.check.pojo.CheckTask;
import com.eva.check.service.core.SimilarityCollectStrategy;
import com.eva.check.service.support.CheckRequestService;
import com.eva.check.service.support.CheckTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author zzz
* @description 针对表【check_request(论文信息)】的数据库操作Service实现
* @createDate 2023-11-13 12:11:00
*/
@Service
@RequiredArgsConstructor
public class CheckRequestServiceImpl extends ServiceImpl<CheckRequestMapper, CheckRequest>
    implements CheckRequestService {

    private final CheckTaskService checkTaskService;
    private final SimilarityCollectStrategy similarityCollectStrategy;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void finish(Long checkId) {
        LambdaUpdateWrapper<CheckRequest> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CheckRequest::getCheckId, checkId);
        updateWrapper.set(CheckRequest::getStatus, CheckRequestStatus.DONE.getValue());
        this.baseMapper.update(updateWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void finishAndCollectResult(Long checkId) {
        List<CheckTask> checkTaskList = this.checkTaskService.findByCheckId(checkId);
        Double similarity = this.similarityCollectStrategy.computeFinalSimilarity(checkTaskList);
        LambdaUpdateWrapper<CheckRequest> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(CheckRequest::getCheckId, checkId);
        updateWrapper.set(CheckRequest::getStatus, CheckRequestStatus.DONE.getValue());
        updateWrapper.set(CheckRequest::getSimilarity, similarity);
        this.baseMapper.update(updateWrapper);
    }

    @Transactional(readOnly = true)
    @Override
    public CheckRequest getByCheckNo(String checkNo) {
        LambdaQueryWrapper<CheckRequest> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CheckRequest::getCheckNo, checkNo);
        return this.baseMapper.selectOne(queryWrapper);
    }
}




