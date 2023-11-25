package com.eva.check.service.support.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eva.check.mapper.PaperSentenceMapper;
import com.eva.check.pojo.PaperSentence;
import com.eva.check.service.support.PaperSentenceService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author zzz
* @description 针对表【paper_sentence(论文句子)】的数据库操作Service实现
* @createDate 2023-10-27 14:41:09
*/
@Service
public class PaperSentenceServiceImpl extends ServiceImpl<PaperSentenceMapper, PaperSentence>
    implements PaperSentenceService {

    @Transactional(readOnly = true)
    @Cacheable(value = "paper:paragraph:sentence", key = "#paragraphId")
    @Override
    public List<PaperSentence> getByParagraphId(Long paragraphId) {
        // 先使用Spring 内存缓存
        LambdaQueryWrapper<PaperSentence> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaperSentence::getParagraphId, paragraphId);
        return this.getBaseMapper().selectList(queryWrapper);
    }
}




