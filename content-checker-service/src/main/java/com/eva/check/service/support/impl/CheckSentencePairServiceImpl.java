package com.eva.check.service.support.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eva.check.mapper.CheckSentencePairMapper;
import com.eva.check.pojo.CheckSentencePair;
import com.eva.check.service.support.CheckSentencePairService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author zzz
* @description 针对表【check_sentence_pair(验证句子对)】的数据库操作Service实现
* @createDate 2023-11-15 20:47:51
*/
@Service
public class CheckSentencePairServiceImpl extends ServiceImpl<CheckSentencePairMapper, CheckSentencePair>
    implements CheckSentencePairService {

    @Transactional(readOnly = true)
    @Override
    public List<CheckSentencePair> getAllByCheckSentenceId(Long checkSentenceId) {
        LambdaQueryWrapper<CheckSentencePair> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CheckSentencePair::getCheckSentenceId, checkSentenceId);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CheckSentencePair> getAllByCheckParagraphId(Long checkParagraphId) {
        LambdaQueryWrapper<CheckSentencePair> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CheckSentencePair::getCheckParaId, checkParagraphId);
        return this.baseMapper.selectList(queryWrapper);
    }
}




