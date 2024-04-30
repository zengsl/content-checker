package com.eva.check.service.support.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eva.check.common.constant.CacheConstant;
import com.eva.check.common.util.SimilarUtil;
import com.eva.check.common.util.TextUtil;
import com.eva.check.mapper.PaperTokenMapper;
import com.eva.check.pojo.PaperSentence;
import com.eva.check.pojo.PaperToken;
import com.eva.check.service.support.PaperSentenceService;
import com.eva.check.service.support.PaperTokenService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
* @author zzz
* @description 针对表【paper_token(论文分词)】的数据库操作Service实现
* @createDate 2023-10-27 14:41:09
*/
@Service
@RequiredArgsConstructor
public class PaperTokenServiceImpl extends ServiceImpl<PaperTokenMapper, PaperToken>
    implements PaperTokenService {

    private final PaperSentenceService paperSentenceService;

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstant.PARAGRAPH_TOKEN_CACHE_KEY, key = "#paragraphId")
    @Override
    public List<PaperToken> getTokenByParagraphIdFromCache(Long paragraphId) {
        // 先使用Spring 内存缓存
        LambdaQueryWrapper<PaperToken> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaperToken::getParagraphId, paragraphId);
        return this.getBaseMapper().selectList(queryWrapper);
    }

    @Transactional(readOnly = true)
    @Override
    public List<PaperToken> getPaperTokenBySentenceId(Long sentenceId) {
        // 先使用Spring 内存缓存
        LambdaQueryWrapper<PaperToken> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaperToken::getSentenceId, sentenceId);
        return this.getBaseMapper().selectList(queryWrapper);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheConstant.SENTENCE_PAPER_TOKEN_CACHE_KEY, key = "#sentenceId")
    @Override
    public List<PaperToken> getPaperTokenBySentenceIdFromCache(Long sentenceId) {
        return this.getPaperTokenBySentenceId(sentenceId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> getTokenBySentenceId(Long sentenceId) {
        List<PaperToken> paperTokenList = this.getPaperTokenBySentenceId(sentenceId);
        if (CollectionUtils.isEmpty(paperTokenList)) {
            return null;
        }
        return paperTokenList.stream().map(PaperToken::getContent).toList();
    }

    @Override
    public List<String> computeTokenBySentenceId(Long sentenceId) {
        PaperSentence paperSentence = this.paperSentenceService.getByIdFromCache(sentenceId);
        // 分词 + 去除停顿词。与PaperCollectServiceImpl#handleSentence逻辑一致
        return TextUtil.segmentAndRemoveStopWord(paperSentence.getContent());
    }

    @Cacheable(value = CacheConstant.SENTENCE_TOKEN_CACHE_KEY, key = "#sentenceId")
    @Transactional(readOnly = true)
    @Override
    public List<String> getTokenBySentenceIdFromCache(Long sentenceId) {
        return this.getTokenBySentenceId(sentenceId);
    }

    @Cacheable(value = CacheConstant.SENTENCE_TOKEN_WORD_FREQ_CACHE_KEY, key = "#sentenceId")
    @Transactional(readOnly = true)
    @Override
    public Map<String, Float> getWordFrequencyFromCache(Long sentenceId) {
//        数据量总数以及每篇文章长度很大时，分词数量也会很大，如果直接从表里查询会出现性能问题。如：2w+文章数量，每篇文章2w+字符数，最终分词数量达到2亿+；目前改成实时计算
//        List<String> wordList = this.getTokenBySentenceId(sentenceId);
        List<String> wordList = this.computeTokenBySentenceId(sentenceId);
        if (CollectionUtils.isEmpty(wordList)) {
            return null;
        }
        return SimilarUtil.countWordFrequency(wordList);
    }
}




