package com.eva.check.service.core.impl;

import com.eva.check.common.enums.TextColor;
import com.eva.check.service.core.SimilarTextRule;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 默认相似文本颜色规则
 *
 * @author zzz
 * @date 2023/11/27 21:40
 */
@Service
public class DefaultSimilarTextRuleImpl implements SimilarTextRule {

    @Override
    public TextColor computeTextColor(Double similarity) {
        BigDecimal bigDecimal = BigDecimal.valueOf(similarity);
        BigDecimal bigDecimal70 = BigDecimal.valueOf(0.7D);
        BigDecimal bigDecimal40 = BigDecimal.valueOf(0.4D);
        if (bigDecimal.compareTo(bigDecimal70) >= 0) {
            return TextColor.RED;
        } else if (bigDecimal.compareTo(bigDecimal40) >= 0) {
            return TextColor.ORANGE;
        }
        return TextColor.BLACK;
    }

    @Override
    public boolean isSimilar(Double similarity) {
        BigDecimal bigDecimal = BigDecimal.valueOf(similarity);
        BigDecimal bigDecimal2 = BigDecimal.valueOf(0.7D);
        BigDecimal bigDecimal3 = BigDecimal.valueOf(0.4D);
        if (bigDecimal.compareTo(bigDecimal2) > 0) {
            return true;
        } else if (bigDecimal.compareTo(bigDecimal3) > 0) {
            return true;
        }
        return false;
    }
}
