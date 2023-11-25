package com.eva.check.service.support;


import com.baomidou.mybatisplus.extension.service.IService;
import com.eva.check.pojo.PaperToken;

import java.util.List;

/**
 * 针对表【paper_token(论文分词)】的数据库操作Service
 *
 * @author zzz
 * @date 2023-10-27 14:41:09
 */
public interface PaperTokenService extends IService<PaperToken> {

    List<PaperToken> getTokenByParagraphIdFromCache(Long paragraphId);
    List<PaperToken> getTokenBySentenceIdFromCache(Long sentenceId);
}
