package com.eva.check.service.support;


import com.baomidou.mybatisplus.extension.service.IService;
import com.eva.check.pojo.PaperSentence;

import java.util.List;

/**
* @author zzz
* @description 针对表【paper_sentence(论文句子)】的数据库操作Service
* @createDate 2023-10-27 14:41:09
*/
public interface PaperSentenceService extends IService<PaperSentence> {

    List<PaperSentence> getByParagraphIdFromCache(Long paragraphId);
    List<Long> getSentenceIdFromCache(Long paragraphId);
    PaperSentence getByIdFromCache(Long sentenceId);

}
