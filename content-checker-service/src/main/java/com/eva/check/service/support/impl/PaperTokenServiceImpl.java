package com.eva.check.service.support.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eva.check.mapper.PaperTokenMapper;
import com.eva.check.pojo.PaperToken;
import com.eva.check.service.support.PaperTokenService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author zzz
* @description 针对表【paper_token(论文分词)】的数据库操作Service实现
* @createDate 2023-10-27 14:41:09
*/
@Service
public class PaperTokenServiceImpl extends ServiceImpl<PaperTokenMapper, PaperToken>
    implements PaperTokenService {


    @Transactional(readOnly = true)
    @Cacheable(value = "paper:paragraph:token", key = "#paragraphId")
    @Override
    public List<PaperToken> getTokenByParagraphIdFromCache(Long paragraphId) {
        // 先使用Spring 内存缓存
        LambdaQueryWrapper<PaperToken> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaperToken::getParagraphId, paragraphId);
        return this.getBaseMapper().selectList(queryWrapper);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "paper:sentence:token", key = "#sentenceId")
    @Override
    public List<PaperToken> getTokenBySentenceIdFromCache(Long sentenceId) {
        // 先使用Spring 内存缓存
        LambdaQueryWrapper<PaperToken> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaperToken::getSentenceId, sentenceId);
        return this.getBaseMapper().selectList(queryWrapper);
    }
}




