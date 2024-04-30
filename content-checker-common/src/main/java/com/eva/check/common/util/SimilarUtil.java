package com.eva.check.common.util;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 相似度计算工具
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@UtilityClass
public class SimilarUtil {
    public static final int DEFAULT_BIT_NUM = 64;

    public static boolean isSimilarWithHamming(Long s1, Long s2) {
        int hammingDistance = getHammingDistance(s1, s2);
        Double similar = calSimHahSimilarWithHamming(hammingDistance);
        System.out.println("【isSimilarWithHamming】similar = " + similar);
        return hammingDistance <= 3;
    }

    public static int getHammingDistance(Long s1, Long s2) {
        return Long.bitCount(s1 ^ s2);
    }

    public static Double calSimHahSimilar(long simHash, long simHash2) {

        // 获取海明距离
        int hammingDistance = getHammingDistance(simHash, simHash2);
        return calSimHahSimilarWithHamming(hammingDistance);
    }

    public static Double calSimHahSimilarWithHamming(int hammingDistance) {
        // 求得海明距离百分比
        Double scale = (1 - hammingDistance * 1D / DEFAULT_BIT_NUM) * 100D;
        return Double.parseDouble(String.format("%.2f", scale));
    }

    /**
     * 统计词频
     *
     * @param sentence 句子
     * @return 词频统计信息
     */
    public static Map<String, Float> countWordFrequency(String sentence) {
       return countWordFrequency(sentence, false);
    }

    /**
     * 统计词频
     *
     * @param sentence 句子
     * @return 词频统计信息
     */
    public static Map<String, Float> countWordFrequency(String sentence, boolean pretreatment) {
        if (pretreatment) {
            sentence = TextUtil.pretreatment(sentence);
        }
        List<String> tokenList = TextUtil.segmentAndRemoveStopWord(sentence);
        if (CollectionUtil.isEmpty(tokenList)) {
            return null;
        }
        return countWordFrequency(tokenList);
    }

    /**
     * 统计词频
     *
     * @param wordSegList 词列表
     * @return 词频统计信息
     */
    public static Map<String, Float> countWordFrequency(Collection<String> wordSegList) {
        Map<String, Float> wordFrequencyMap = new HashMap<>(16);
        wordSegList.forEach(seg -> {
            Float integer = wordFrequencyMap.computeIfAbsent(seg, k -> 0F);
            // 词频 + 1
            wordFrequencyMap.put(seg, integer + 1);
        });
        return wordFrequencyMap;
    }


    /**
     * 获取cosine相似度
     *
     * <a href="https://www.cnblogs.com/qdhxhz/p/9484274.html">参考文档</a>
     *
     *
     * <a href="https://github.com/tdebatty/java-string-similarity">其他一些相似度算法的Java实现</a>
     *
     * @param weightMap1 第一个词频集合
     * @param weightMap2 第二个词频集合
     */
    public static double getCosineSimilarity(Map<String, Float> weightMap1, Map<String, Float> weightMap2) {

        // 向每一个Word对象的属性都注入weight（权重）属性值
        // taggingWeightByFrequency(words1, words2);

        //第二步：计算词频
        //通过上一步让每个Word对象都有权重值，那么在封装到map中（key是词，value是该词出现的次数（即权重））

        //将所有词都装入set容器中
        Set<String> words = new HashSet<>();
        words.addAll(weightMap1.keySet());
        words.addAll(weightMap2.keySet());
        // a.b
        AtomicDouble ab = new AtomicDouble();
        // |a|的平方
        AtomicDouble aa = new AtomicDouble();
        // |b|的平方
        AtomicDouble bb = new AtomicDouble();

        // 第三步：写出词频向量，后进行计算
        words.parallelStream().forEach(word -> {
            //看同一词在a、b两个集合出现的此次
            Float x1 = weightMap1.get(word);
            Float x2 = weightMap2.get(word);
            if (x1 != null && x2 != null) {
                //x1x2
                float oneOfTheDimension = x1 * x2;
                //+
                ab.addAndGet(oneOfTheDimension);
            }
            if (x1 != null) {
                //(x1)^2
                float oneOfTheDimension = x1 * x1;
                //+
                aa.addAndGet(oneOfTheDimension);
            }
            if (x2 != null) {
                //(x2)^2
                float oneOfTheDimension = x2 * x2;
                //+
                bb.addAndGet(oneOfTheDimension);
            }
        });
        //|a| 对aa开方
        double aaSqrt = Math.sqrt(aa.doubleValue());
        //|b| 对bb开方
        double bbSqrt = Math.sqrt(bb.doubleValue());

        //使用BigDecimal保证精确计算浮点数
        //double aabb = aaSqrt * bbSqrt;
        BigDecimal aabb = BigDecimal.valueOf(aaSqrt).multiply(BigDecimal.valueOf(bbSqrt));

        //similarity=a.b/|a|*|b|
        //divide参数说明：aabb被除数,9表示小数点后保留9位，最后一个表示用标准的四舍五入法
        return BigDecimal.valueOf(ab.get()).divide(aabb, 2, RoundingMode.HALF_UP).doubleValue();
    }

    public static Double formatSimilarity(Double d) {
        BigDecimal similarity = BigDecimal.valueOf(d);
        return similarity.setScale(2,  RoundingMode.HALF_UP).doubleValue();
    }
}
