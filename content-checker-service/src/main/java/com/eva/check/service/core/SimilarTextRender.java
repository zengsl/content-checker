package com.eva.check.service.core;

/**
 * 文本渲染器
 *
 * @author zzz
 * @date 2023/11/27 21:24
 */
public interface SimilarTextRender {

    /**
     * 渲染方法
     *
     * @param originalText 原始文本
     * @param similarity   相似度
     * @return String
     */
    String render(String originalText, Double similarity, Long checkParagraphId);
}
