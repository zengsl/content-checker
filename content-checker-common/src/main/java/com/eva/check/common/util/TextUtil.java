package com.eva.check.common.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.algorithm.MaxHeap;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.summary.TextRankKeyword;
import com.vdurmont.emoji.EmojiParser;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 文本工具类
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@UtilityClass
public class TextUtil {

    /**
     * 默认句子分隔符
     */
    final static String DEFAULT_SENTENCE_SEPARATOR = "[，,。:：“”？?！!；;]";
    /*final static String DEFAULT_PARAGRAPH_SEPARATOR = "[\r\n]";


    public static List<String> splitParagraph(String document) {
        String[] paragraphs = document.split(DEFAULT_PARAGRAPH_SEPARATOR);
        return Arrays.stream(paragraphs).filter(StringUtils::isNoneBlank).collect(Collectors.toList());
    }*/

    public static List<String> splitSentence(String document, String sentenceSeparator) {
        List<String> sentences = Lists.newArrayList();
        String newLineRegex = "[\r\n]";
        for (String line : document.split(newLineRegex)) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            for (String sent : line.split(sentenceSeparator)) {
                sent = sent.trim();
                if (sent.isEmpty()) {
                    continue;
                }
                sentences.add(sent);
            }
        }

        return sentences;
    }

    public static List<String> splitSentence(String document) {
        // TOOD 限制下句子的最小长度？例如：4个字符
        return splitSentence(document, DEFAULT_SENTENCE_SEPARATOR);
    }

    public static String clearSpecialCharacters(String text) {

        // 过滤HTML标签
        text = Jsoup.clean(text, Safelist.none());

        // 过滤特殊字符
        String[] strings = {" ", "\n", "\r", "\t", "\\r", "\\n", "\\t", "&nbsp;", "&amp;", "&lt;", "&gt;", "&quot;", "&qpos;"};
        for (String string : strings) {
            text = text.replaceAll(string, "");
        }
        // 将内容转换为小写
        text = StringUtils.lowerCase(text);

        // 去除表情符号
        text = EmojiParser.removeAllEmojis(text);
        return text;
    }

    /**
     * 文本预处理方法
     *
     * @param text 待处理的文本
     * @return 经过预处理后的文本
     */
    public static String pretreatment(String text) {
        text = clearSpecialCharacters(text);
        //  1、中文繁体转简体
        text = HanLP.convertToSimplifiedChinese(text);
        //  2、全角字符转半角
        text = Convert.toDBC(text);
        // 归一化处理：编码
        text = StrUtil.normalize(text);
        return text;
    }

    /**
     * 分词
     *
     * @param text 待处理的文本
     * @return 经过预处理后文本的分词结果
     */
    public static List<String> pretreatmentAndExtractKeyword(String text) {
        String newSentence = pretreatment(text);
        return HanLP.extractKeyword(newSentence, newSentence.length());
    }

    public static List<String> pretreatmentAndSegment(String text) {
        String newSentence = pretreatment(text);
        return segment(newSentence);
    }

    public static List<String> segment(String text) {
        HanLP.Config.ShowTermNature = false;
        List<Term> sourceSegment = HanLP.segment(text);
        return sourceSegment.stream().map(t -> t.word).toList();
    }

    public static List<String> segmentAndRemoveStopWord(String text) {
        HanLP.Config.ShowTermNature = false;
        List<Term> sourceSegment = HanLP.segment(text);
        return sourceSegment.stream().map(t -> t.word).filter(k -> k.length() > 1).filter(StopWordRemover::isNotStopWord).toList();
    }

    public static List<String> cleanAndExtractKeyword(String text) {
        // 文本处理 + 分词
        List<String> keywordList = pretreatmentAndExtractKeyword(text);
        // 去除停顿词
        return keywordList.stream().filter(k -> k.length() > 1).filter(StopWordRemover::isNotStopWord).toList();
    }

    public static List<String> cleanAndSegment(String text) {
        // 文本处理 + 分词
        List<String> keywordList = pretreatmentAndSegment(text);
        // 去除停顿词
        return keywordList.stream().filter(k -> k.length() > 1).filter(StopWordRemover::isNotStopWord).toList();
    }

    public static final String CHECK_DATA_PATH = "checkData";

    public static String getCheckDocument(String fileName) throws IOException, URISyntaxException {
        Path filePath = Paths.get(Objects.requireNonNull(TextUtil.class.getClassLoader().getResource(CHECK_DATA_PATH + File.separator + fileName)).toURI());
        return Files.readString(filePath);
    }

    public static Map<String, Float> extractKeyword(String text, int size) {
        List<Term> termList = HanLP.segment(text);
        return topKeyword(termList, size);
    }

    public static Map<String, Float> topKeyword(List<Term> termList, int size) {
        TextRankKeyword textRankKeyword = new TextRankKeyword();
        return top(size, textRankKeyword.getTermAndRank(termList));
    }

    private Map<String, Float> top(int size, Map<String, Float> map) {
        Map<String, Float> result = new LinkedHashMap<>();
        for (Map.Entry<String, Float> entry : new MaxHeap<>(size, new Comparator<Map.Entry<String, Float>>() {
            @Override
            public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        }).addAll(map.entrySet()).toList()) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
