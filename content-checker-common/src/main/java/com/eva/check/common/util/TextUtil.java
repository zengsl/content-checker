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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    final static String DEFAULT_SENTENCE_SEPARATOR = "[，,。:：？?！!；;～~]";
    final static int MIN_SENTENCE_LENGTH = 10;
//    final static String DEFAULT_SENTENCE_SEPARATOR = "[，,。:：“”？?！!；;～~]";

    final static String DEFAULT_PARAGRAPH_SEPARATOR = "[\r\n]";


    public static List<String> splitParagraph(String document) {
        String[] paragraphs = document.split(DEFAULT_PARAGRAPH_SEPARATOR);
        return Arrays.stream(paragraphs).filter(StringUtils::isNoneBlank).collect(Collectors.toList());
    }

    /**
     * 文章分段之后再拆分句子
     */
    @Deprecated
    public static List<String> splitSentenceFromDoc(String paragraph, String sentenceSeparator) {
        List<String> sentences = Lists.newArrayList();
        String newLineRegex = "[\r\n]";

        for (String line : paragraph.split(newLineRegex)) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            sentences.addAll(splitSentence(line, sentenceSeparator));
        }

        return sentences;
    }

    /**
     * 拆分句子
     */
    public static List<String> splitSentence(String paragraph, String sentenceSeparator) {
        List<String> sentences = Lists.newArrayList();
        for (String sent : paragraph.split(sentenceSeparator)) {
            sent = sent.trim();
            if (sent.isEmpty()) {
                continue;
            }
            sentences.add(sent);
        }

        return sentences;
    }

    public static List<String> splitSentence(String document) {
        return splitSentence(document, DEFAULT_SENTENCE_SEPARATOR);
    }

    /**
     * 智能拆分句子
     * <p>
     * 按照常见的句子分隔符{@link TextUtil#DEFAULT_SENTENCE_SEPARATOR}进行拆分，如果拆分后句子长度小于阈值{@link TextUtil#MIN_SENTENCE_LENGTH}，则与后面句子合并。
     *
     * @param document 文档内容
     */
    public static List<String> smartSplitSentence(String document) {

        String[] splitSentences = splitSentenceRemainSplitter(document);
        if (splitSentences.length == 0) {
            return Lists.newArrayList();
        }
        List<String> finalSentences = Lists.newArrayList();
        // 针对句子长度进行合并处理。
        StringBuilder waitSentence = new StringBuilder();
        for (int i = 0; i < splitSentences.length; i++) {
            String sentence = splitSentences[i];
            int length = sentence.trim().length();
            // 满足最小长度则直接添加
            if (length >= MIN_SENTENCE_LENGTH) {
                if (waitSentence.isEmpty()) {
                    finalSentences.add(sentence);
                } else {
//        可以针对长度进行判断，避免合并过长的句子        if(waitSentence.length() >= MIN_SENTENCE_LENGTH/2 )
                    waitSentence.append(sentence);
                    finalSentences.add(waitSentence.toString());
                    waitSentence.setLength(0);
                }
            } else {
                waitSentence.append(sentence);
                // 若当前是最后一句，则拼接到上一句后面。 ps:也可以增加更复杂的判断，如：上一句很长的话 就不拼接到上一句中。
                if (i == (splitSentences.length - 1)) {
                    if (finalSentences.isEmpty()) {
                        finalSentences.add(waitSentence.toString());
                    } else {
                        int lastIndex = finalSentences.size() - 1;
                        finalSentences.set(lastIndex, finalSentences.get(lastIndex) + waitSentence);
                    }
                } else {
                    if (waitSentence.length() >= MIN_SENTENCE_LENGTH) {
                        finalSentences.add(waitSentence.toString());
                        waitSentence.setLength(0);
                    }
                }
            }
        }

        return finalSentences;
    }

    public static String[] splitSentenceRemainSplitter(String sentence) {
        //1. 定义匹配模式
        Pattern p = Pattern.compile(DEFAULT_SENTENCE_SEPARATOR);
        Matcher m = p.matcher(sentence);

        //2. 拆分句子[拆分后的句子符号也没了]
        String[] words = p.split(sentence);

        //3. 保留原来的分隔符
        if (words.length > 0) {
            int count = 0;
            while (count < words.length) {
                if (m.find()) {
                    words[count] += m.group();
                }
                count++;
            }
        }
        return words;
    }

    public static void main(String[] args) {
        String sentence = "很抱歉打扰到您了，祝您生活愉快，再见。";

        List<String> strings = TextUtil.smartSplitSentence(sentence);
        System.out.println(strings);

        /*StringBuilder x = new StringBuilder();
        x.append("sfjlajslkdfjlkajskldjflajslkdjflajsldfAAAAA");
        System.out.println(x);
        x.setLength(0);
        x.append("112");
        System.out.println(x);*/
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
        // 去除尾部标点符号
        text = text.replaceFirst("[\\pP‘’“”]$", "");
        return text;
    }

    /**
     * 分词
     *
     * @param text 待处理的文本
     * @return 经过预处理后文本的分词结果
     */
    public static List<String> pretreatmentAndExtractKeyword(String text) {
        return pretreatmentAndExtractKeyword(text, text.length());
    }

    /**
     * 分词
     *
     * @param text 待处理的文本
     * @return 经过预处理后文本的分词结果
     */
    public static List<String> pretreatmentAndExtractKeyword(String text, int keywordCount) {
        String newSentence = pretreatment(text);
        return HanLP.extractKeyword(newSentence, keywordCount);
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
        return cleanAndExtractKeyword(text, text.length());
    }

    public static List<String> cleanAndExtractKeyword(String text, int keywordCount) {
        // 文本处理 + 分词
        List<String> keywordList = pretreatmentAndExtractKeyword(text, keywordCount);
//        System.out.println("extractKeyword:"+keywordList);
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

    public static String getDocument(String filePath) throws IOException, URISyntaxException {
        return Files.readString(Paths.get(Objects.requireNonNull(TextUtil.class.getClassLoader().getResource(filePath)).toURI()));
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

    public static Integer countWord(String content) {
        return content == null || content.isEmpty() ? 0 : content.length();
    }
}
