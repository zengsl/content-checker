package com.eva.check.service.support;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eva.check.pojo.CheckSentence;

import java.util.List;

/**
* @author zzz
* @description 针对表【check_sentence(检测文本句子)】的数据库操作Service
* @createDate 2023-11-15 15:12:08
*/
public interface CheckSentenceService extends IService<CheckSentence> {

    List<CheckSentence> getByParagraphId(Long paragraphId);

}
