package com.eva.check.service.support.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eva.check.mapper.CheckParagraphPairMapper;
import com.eva.check.pojo.CheckParagraphPair;
import com.eva.check.service.support.CheckParagraphPairService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 针对表【check_paragraph_list(验证段落列表)】的数据库操作Service实现
 *
 * @author zzz
 * @date 2023-11-15 16:42:04
 */
@Service
public class CheckParagraphPairServiceImpl extends ServiceImpl<CheckParagraphPairMapper, CheckParagraphPair>
        implements CheckParagraphPairService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void initCompareList(List<CheckParagraphPair> checkParagraphListPairs) {
        this.saveBatch(checkParagraphListPairs);
    }

    @Transactional(readOnly = true)

    @Override
    public List<CheckParagraphPair> getByTaskId(Long taskId) {
        LambdaQueryWrapper<CheckParagraphPair> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CheckParagraphPair::getTaskId, taskId);
        return this.getBaseMapper().selectList(queryWrapper);
    }
}




