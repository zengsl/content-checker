package com.eva.check.service.support.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eva.check.common.constant.CacheConstant;
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
    @Cacheable(value = CacheConstant.SENTENCE_CACHE_KEY, key = "#sentenceId")
    @Override
    public PaperSentence getByIdFromCache(Long sentenceId) {
        return this.getById(sentenceId);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstant.PARAGRAPH_SENTENCE_ID_CACHE_KEY, key = "#paragraphId")
    @Override
    public List<Long> getSentenceIdFromCache(Long paragraphId) {
        // 先使用Spring 内存缓存
        LambdaQueryWrapper<PaperSentence> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaperSentence::getParagraphId, paragraphId);
        queryWrapper.select(PaperSentence::getSentenceId);
        return this.getBaseMapper().selectObjs(queryWrapper);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstant.PARAGRAPH_SENTENCE_CACHE_KEY, key = "#paragraphId")
    @Override
    public List<PaperSentence> getByParagraphIdFromCache(Long paragraphId) {
        // 先使用Spring 内存缓存
        LambdaQueryWrapper<PaperSentence> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaperSentence::getParagraphId, paragraphId);
        return this.getBaseMapper().selectList(queryWrapper);
    }
}




