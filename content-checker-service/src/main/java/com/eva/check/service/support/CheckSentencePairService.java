package com.eva.check.service.support;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eva.check.pojo.CheckSentencePair;

import java.util.List;

/**
* @author zzz
* @description 针对表【check_sentence_pair(验证句子对)】的数据库操作Service
* @createDate 2023-11-15 20:47:51
*/
public interface CheckSentencePairService extends IService<CheckSentencePair> {

    List<CheckSentencePair> getAllByCheckSentenceId(Long checkSentenceId);
    List<CheckSentencePair> getAllByCheckSentenceId(Long checkSentenceId, Double similarity);
    List<CheckSentencePair> getAllByCheckParagraphId(Long checkParagraphId);
}
