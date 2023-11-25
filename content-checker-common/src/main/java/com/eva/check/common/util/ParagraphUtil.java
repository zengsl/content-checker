package com.eva.check.common.util;

import lombok.experimental.UtilityClass;

import java.util.List;
import java.util.Map;

/**
 * 段落工具类
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@UtilityClass
public class ParagraphUtil {

    public static SimHashUtil.SimHash buildFingerprint(String content) {
        List<String> paperKeywordList = TextUtil.pretreatmentAndSegment(content);
        return SimHashUtil.buildSimHash(paperKeywordList);
    }
    public static SimHashUtil.SimHash buildFingerprint2(String content) {
        String newSentence = TextUtil.pretreatment(content);
        Map<String, Float> stringFloatMap = TextUtil.extractKeyword(newSentence, 6);
        long hash = SimHashUtil.hash(stringFloatMap);
        return SimHashUtil.buildSimHash(hash);
    }

    /*public static SimHashUtil.SimHash pretreatmentAndBuildSimHash2(String content) {
        List<String> paperKeywordList = TextUtil.pretreatmentAndSegment(content);
        return SimHashUtil.buildSimHash(paperKeywordList);
    }*/
}
