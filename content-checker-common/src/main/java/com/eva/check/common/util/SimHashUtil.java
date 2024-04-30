package com.eva.check.common.util;

import cn.hutool.core.lang.hash.MurmurHash;
import lombok.Data;
import lombok.experimental.UtilityClass;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * <a href="https://www.cs.princeton.edu/courses/archive/spr04/cos598B/bib/CharikarEstim.pdf">SimHash算法介绍</a>
 * <a href="https://dl.acm.org/doi/10.1145/509907.509965">SimHash算法介绍</a>
 * <a href="https://engineering.dynatrace.com/blog/speeding-up-simhash-by-10x-using-a-bit-hack/">Speeding up Simhash by 10x using a bit hack</a>
 * <p>
 * SimHash工具类，一种由Google提出的局部敏感Hash算法（LSH），可用于生成文本指纹fingerprint
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@UtilityClass
public class SimHashUtil {
    private static final int bitNum = 64;
    /**
     * 存储段数，默认按照4段进行simhash存储
     */
    private static final int fracCount = 4;
    private static final int fracBitNum = bitNum / fracCount;

    public static long hash(Map<String, Float> charSequenceIntegerMap) {
        Collection<String> segList = charSequenceIntegerMap.keySet();
        // 计算词频
        // 按照词语的hash值，计算simHashWeight(低位对齐)
        return extracted(charSequenceIntegerMap, segList);
    }

    private static long extracted(Map<String, Float> charSequenceIntegerMap, Collection<String> segList) {
        final int[] weight = new int[bitNum];
        long wordHash;

        for (String seg : segList) {
            wordHash = MurmurHash.hash64(seg);
            for (int i = 0; i < bitNum; i++) {
                if (((wordHash >> i) & 1) == 1) {
                    weight[i] += charSequenceIntegerMap.get(seg);
                } else {
                    weight[i] -= charSequenceIntegerMap.get(seg);
                }
                /*if (((wordHash >> i) & 1) == 1) {
                    weight[i] += 1;
                } else {
                    weight[i] -= 1;
                }*/
            }
        }

        // 降维计算得到SimHash值
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bitNum; i++) {
            sb.append((weight[i] > 0) ? 1 : 0);
        }

        return new BigInteger(sb.toString(), 2).longValue();
    }

    /**
     * 指定文本计算simhash值
     *
     * @param segList 分词的词列表
     * @return Hash值
     */
    public static long hash(Collection<String> segList) {
        // 计算词频
        Map<String, Float> charSequenceIntegerMap = SimilarUtil.countWordFrequency(segList);
        return extracted(charSequenceIntegerMap, segList);
    }

    public static List<String> splitSimHash(Long simHash) {
        final List<String> ls = new ArrayList<>();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bitNum; i++) {
            sb.append(simHash >> i & 1);
            if ((i + 1) % fracBitNum == 0) {
                ls.add(sb.toString());
                sb.setLength(0);
            }
        }
        return ls;
    }


    public static SimHash buildSimHash(long hash) {

        List<String> splitSimHash = splitSimHash(hash);
        SimHash simHash = new SimHash();
        simHash.setSimHash(hash);
        simHash.setSimHash1(splitSimHash.get(0));
        simHash.setSimHash2(splitSimHash.get(1));
        simHash.setSimHash3(splitSimHash.get(2));
        simHash.setSimHash4(splitSimHash.get(3));
        return simHash;
    }

    public static SimHash buildSimHash(Collection<String> segList) {
        long hash = hash(segList);
        List<String> splitSimHash = splitSimHash(hash);
        SimHash simHash = new SimHash();
        simHash.setSimHash(hash);
        simHash.setSimHash1(splitSimHash.get(0));
        simHash.setSimHash2(splitSimHash.get(1));
        simHash.setSimHash3(splitSimHash.get(2));
        simHash.setSimHash4(splitSimHash.get(3));
        return simHash;
    }

    public static SimHash pretreatmentAndBuildSimHash(String content) {
        List<String> paperKeywordList = TextUtil.cleanAndSegment(content);
        return buildSimHash(paperKeywordList);
    }

    public static SimHash pretreatmentAndBuildSimHash2(String content) {
        List<String> paperKeywordList = TextUtil.cleanAndSegment(content);
        return buildSimHash(paperKeywordList);
    }

    @Data
    public static class SimHash {
        private long simHash;

        private String simHash1;
        private String simHash2;
        private String simHash3;
        private String simHash4;
    }
}
