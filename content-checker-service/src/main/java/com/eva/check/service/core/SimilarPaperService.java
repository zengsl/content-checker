package com.eva.check.service.core;

import com.eva.check.pojo.dto.PaperResult;
import com.eva.check.pojo.dto.ParagraphResult;

/**
 * 相似论文服务
 *
 * @author zzz
 * @date 2023/11/28 22:36
 */
public interface SimilarPaperService {


    PaperResult assemblePaperResult(Long taskId);

    ParagraphResult assembleParagraphResult(String originalText, Double similarity, Long checkParagraphId);

}
