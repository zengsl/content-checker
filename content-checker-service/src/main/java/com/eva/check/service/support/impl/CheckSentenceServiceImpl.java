package com.eva.check.service.support.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eva.check.mapper.CheckSentenceMapper;
import com.eva.check.pojo.CheckSentence;
import com.eva.check.service.support.CheckSentenceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author zzz
* @description 针对表【check_sentence(检测文本句子)】的数据库操作Service实现
* @createDate 2023-11-15 15:12:08
*/
@Service
public class CheckSentenceServiceImpl extends ServiceImpl<CheckSentenceMapper, CheckSentence>
    implements CheckSentenceService {

    @Transactional(readOnly = true)

    @Override
    public List<CheckSentence> getByParagraphId(Long paragraphId) {
        LambdaQueryWrapper<CheckSentence> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CheckSentence::getParagraphId, paragraphId);
        return this.getBaseMapper().selectList(queryWrapper);
    }
}




