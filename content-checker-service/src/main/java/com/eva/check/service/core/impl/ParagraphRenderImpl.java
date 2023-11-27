package com.eva.check.service.core.impl;

import cn.hutool.core.util.NumberUtil;
import com.eva.check.common.enums.TextColor;
import com.eva.check.pojo.CheckSentence;
import com.eva.check.service.core.SimilarTextRender;
import com.eva.check.service.core.SimilarTextRule;
import com.eva.check.service.support.CheckSentenceService;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 段落渲染
 *
 * @author zzz
 * @date 2023/11/27 21:27
 */
@RequiredArgsConstructor
public class ParagraphRenderImpl implements SimilarTextRender {

    private final SimilarTextRule similarTextRule = new DefaultSimilarTextRuleImpl();

    private final CheckSentenceService checkSentenceService;

    @Override
    public String render(String originalText, Double similarity, Long checkParagraphId) {
        if (!this.similarTextRule.isSimilar(similarity)) {
            return originalText;
        }

        List<CheckSentence> checkSentences = this.checkSentenceService.getByParagraphId(checkParagraphId);
        for (CheckSentence checkSentence : checkSentences) {
            if (!this.similarTextRule.isSimilar(checkSentence.getSimilarity())) {
                continue;
            }
            TextColor textColor = this.similarTextRule.computeTextColor(checkSentence.getSimilarity());
            originalText = originalText.replaceAll(checkSentence.getOriginContent(),
                    textColor.renderHtml(checkSentence.getOriginContent()
                            , checkSentence.getSentenceId().toString()
                            , NumberUtil.decimalFormat("#.##%", checkSentence.getSimilarity())
                    )
            );
        }

        return originalText;
    }
}
