package com.eva.check.service.core.impl;

import cn.hutool.core.util.NumberUtil;
import com.eva.check.common.enums.TextColor;
import com.eva.check.service.core.SimilarTextRender;
import com.eva.check.service.core.SimilarTextRule;
import lombok.RequiredArgsConstructor;

/**
 * 段落渲染
 *
 * @author zzz
 * @date 2023/11/27 21:27
 */
@RequiredArgsConstructor
public class ParagraphRenderImpl implements SimilarTextRender {
    private final SimilarTextRule similarTextRule = new DefaultSimilarTextRuleImpl();

    public String renderSentence(String originalText, Double similarity, String sentenceId) {
        TextColor textColor = this.similarTextRule.computeTextColor(similarity);
        return  textColor.renderSentenceHtml(originalText
                , sentenceId
                , NumberUtil.decimalFormat("#.##%", similarity));
    }
}
