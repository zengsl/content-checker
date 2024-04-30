package com.eva.check.util;

import com.eva.check.common.util.SimilarUtil;
import com.eva.check.common.util.TextUtil;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SimilarUtilTest {

    @Test
    void testNewInstance() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            Class<?> clazz = Class.forName("com.eva.check.common.util.SimilarUtil");
            Constructor<?> declaredConstructor = clazz.getDeclaredConstructor();
            declaredConstructor.setAccessible(true);
            try {
                declaredConstructor.newInstance();
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        });

    }

    @Test
    void testGetDifferentHammingDistance() {
        long sourceHash = Long.valueOf("100001", 2);
        long targetHash = Long.valueOf("100000", 2);
        int hammingDistance = SimilarUtil.getHammingDistance(sourceHash, targetHash);
        Assertions.assertEquals(1, hammingDistance);
    }

    @Test
    void testGetDifferentHammingDistance2() {
        long sourceHash = Long.valueOf("100001", 2);
        long targetHash = Long.valueOf("101101", 2);
        int hammingDistance = SimilarUtil.getHammingDistance(sourceHash, targetHash);
        Assertions.assertEquals(2, hammingDistance);
    }


    @Test
    void testGetDifferentHammingDistance3() {
        long sourceHash = Long.valueOf("100001100001", 2);
        long targetHash = Long.valueOf("100001100000", 2);
        int hammingDistance = SimilarUtil.getHammingDistance(sourceHash, targetHash);
        Assertions.assertEquals(1, hammingDistance);
    }

    @Test
    void testGetSameHammingDistance() {
        long sourceHash = Long.valueOf("100001", 2);
        long targetHash = Long.valueOf("100001", 2);
        int hammingDistance = SimilarUtil.getHammingDistance(sourceHash, targetHash);
        Assertions.assertEquals(0, hammingDistance);
    }

    @Test
    void testCalSimHahSimilar() {
        long sourceHash = Long.valueOf("100001100001", 2);
        long targetHash = Long.valueOf("100001100000", 2);
        Assertions.assertEquals(100D, SimilarUtil.calSimHahSimilar(sourceHash, sourceHash));
        Assertions.assertEquals(SimilarUtil.calSimHahSimilarWithHamming(1), SimilarUtil.calSimHahSimilar(sourceHash, targetHash));
    }

    @Test
    void testCalSimHahSimilarWithHamming() {
        Assertions.assertEquals(98.44, SimilarUtil.calSimHahSimilarWithHamming(1));
    }


    @Test
    void countWordFrequency() {
        Map<String, Integer> freqMap = Map.of("项目", 3, "重复", 1);
        List<String> wordList = Lists.newArrayList();
        wordList.add("项目");
        wordList.add("项目");
        wordList.add("项目");
        wordList.add("重复");
        Map<String, Float> charSequenceIntegerMap = SimilarUtil.countWordFrequency(wordList);
        freqMap.forEach((k, v) -> {
            Assertions.assertEquals(v, charSequenceIntegerMap.get(k));
        });
    }

    @Test
    void getCosineSimilarity() {
        /*Map<String, Float> frequency1 = Map.of("项目", 3L, "重复", 2L);
        Map<String, Float> frequency2 = Map.of("项目", 3L, "重复", 1L);
        double cosineSimilarity = SimilarUtil.getCosineSimilarity(frequency1, frequency2);
        double cosineSimilarity2 = SimilarUtil.getCosineSimilarity(frequency1, frequency1);
        System.out.println(cosineSimilarity);
        System.out.println(cosineSimilarity2);
        Assertions.assertNotEquals(1, cosineSimilarity);
        Assertions.assertEquals(1, cosineSimilarity2);*/

    }

    @Test
    public void testAllWithExtractKeyword() {
        /*String document = "客户关系管理（Customer Relationship Managemen-CRM）是指企业运用营销、关怀等手段时刻保持商业银行与实际客户和潜在客户交互的动作，客户关系管理是在企业发展过程中的一个产物，也是企业保持竞争力的重要方面。";
        String document2 = "客户关系管理(Customer Relationship Management CRM)，包括企业识别、挑选、获取、发展和保持客户的整个商业过程。它是理念、技术、实施";*/
        String document = "客户关系管理（Customer Relationship Managemen-CRM）是指企业运用营销、关怀等手段时刻保持商业银行与实际客户和潜在客户交互的动作，客户关系管理是在企业发展过程中的一个产物，也是企业保持竞争力的重要方面。";
        String document2 = "客户关系管理(Customer Relationship Management CRM)，包括企业识别、挑选、获取、发展和保持客户的整个商业过程。它是理念、技术、实施";
        List<String> stringList = TextUtil.cleanAndExtractKeyword(document);
        assertNotNull(stringList);
        assertNotEquals(0, stringList.size());
        List<String> stringList2 = TextUtil.cleanAndExtractKeyword(document2);
        assertNotNull(stringList2);
        assertNotEquals(0, stringList2.size());
        System.out.println("stringList:" + stringList);
        System.out.println("stringList2:" + stringList2);

        Map<String, Float> charSequenceIntegerMap1 = SimilarUtil.countWordFrequency(stringList);
        Map<String, Float> charSequenceIntegerMap2 = SimilarUtil.countWordFrequency(stringList2);
        double cosineSimilarity = SimilarUtil.getCosineSimilarity(charSequenceIntegerMap1, charSequenceIntegerMap2);
        System.out.println("cosineSimilarity: " + cosineSimilarity);
    }

    @Test
    public void testAllWithSegment() {
        /*String document = "客户关系管理（Customer Relationship Managemen-CRM）是指企业运用营销、关怀等手段时刻保持商业银行与实际客户和潜在客户交互的动作，客户关系管理是在企业发展过程中的一个产物，也是企业保持竞争力的重要方面。";
        String document2 = "客户关系管理(Customer Relationship Management CRM)，包括企业识别、挑选、获取、发展和保持客户的整个商业过程。它是理念、技术、实施";*/
        String document = "客户关系管理（Customer Relationship Managemen-CRM）是指企业运用营销、关怀等手段时刻保持商业银行与实际客户和潜在客户交互的动作，客户关系管理是在企业发展过程中的一个产物，也是企业保持竞争力的重要方面。";
        String document2 = "客户关系管理(Customer Relationship Management CRM)，包括企业识别、挑选、获取、发展和保持客户的整个商业过程。它是理念、技术、实施";
        List<String> stringList = TextUtil.cleanAndSegment(document);
        assertNotNull(stringList);
        assertNotEquals(0, stringList.size());
        List<String> stringList2 = TextUtil.cleanAndSegment(document2);
        assertNotNull(stringList2);
        assertNotEquals(0, stringList2.size());
        System.out.println("stringList:" + stringList);
        System.out.println("stringList2:" + stringList2);

        Map<String, Float> charSequenceIntegerMap1 = SimilarUtil.countWordFrequency(stringList);
        Map<String, Float> charSequenceIntegerMap2 = SimilarUtil.countWordFrequency(stringList2);
        System.out.println("charSequenceIntegerMap1: " + charSequenceIntegerMap1);
        System.out.println("charSequenceIntegerMap2: " + charSequenceIntegerMap2);


        double cosineSimilarity = SimilarUtil.getCosineSimilarity(charSequenceIntegerMap1, charSequenceIntegerMap2);
        System.out.println("cosineSimilarity: " + cosineSimilarity);
    }

    @Test
    public void testAllWithSegment2() throws IOException, URISyntaxException {

        String document = TextUtil.getCheckDocument("data1.txt");
        String document2 = TextUtil.getCheckDocument("data2.txt");
        ;
        List<String> stringList = TextUtil.cleanAndSegment(document);
        assertNotNull(stringList);
        assertNotEquals(0, stringList.size());
        List<String> stringList2 = TextUtil.cleanAndSegment(document2);
        assertNotNull(stringList2);
        assertNotEquals(0, stringList2.size());
        System.out.println("stringList:" + stringList);
        System.out.println("stringList2:" + stringList2);

        Map<String, Float> charSequenceIntegerMap1 = SimilarUtil.countWordFrequency(stringList);
        Map<String, Float> charSequenceIntegerMap2 = SimilarUtil.countWordFrequency(stringList2);
        System.out.println("charSequenceIntegerMap1: " + charSequenceIntegerMap1);
        System.out.println("charSequenceIntegerMap2: " + charSequenceIntegerMap2);


        double cosineSimilarity = SimilarUtil.getCosineSimilarity(charSequenceIntegerMap1, charSequenceIntegerMap2);
        System.out.println("cosineSimilarity: " + cosineSimilarity);
    }

    @Deprecated
    @Test
    void testResult() {
        int hammingDistance = SimilarUtil.getHammingDistance(1925649831562732737L, 4303550434814354625L);
        System.out.println("hammingDistance:" + hammingDistance);
        Double similar = SimilarUtil.calSimHahSimilar(1925649831562732737L, 4303550434814354625L);
        System.out.println("similar:" + similar);

        Double similar1 = SimilarUtil.calSimHahSimilarWithHamming(hammingDistance);
        System.out.println("similar1:" + similar1);

    }

    /**
     * 当对某句子对相似度结果计算有疑问时，可以测试某对句子相似度的计算结果：
     * 1、根据checkNo查找数据的SQL
     * <p>
     * select *
     * from check_task t where t.check_no = 'fY65qSJTM7tI2Qbuz2yLo';
     * <p>
     * select *
     * from check_paragraph t where t.paper_id = 285;
     * <p>
     * select t.sentence_id, t.*
     * from check_sentence t where t.paragraph_id = 511;
     * <p>
     * select *
     * from check_sentence_pair t where t.check_sentence_id = 16543;
     * <p>
     * select t.content
     * from paper_sentence t where t.sentence_id = 5565024;
     * select t.content
     * from check_sentence t where t.sentence_id = 16543;
     * <p>
     * 2、 将找到的句子作为doc1和doc2
     *
     * @author zengsl
     * @date 2024/4/30 16:59
     */
    @Test
    void testSimilarity() {
        // 根据doc注释查找对应的句子内容
        String doc1 = "加速研究进度,早日产出研究成果,为推动高质量发展注入新动能";
        String doc2 = "基于相关研究成果";
        List<String> keywordList1 = TextUtil.segmentAndRemoveStopWord(doc1);
        List<String> keywordList2 = TextUtil.segmentAndRemoveStopWord(doc2);
        Map<String, Float> frequency1 = SimilarUtil.countWordFrequency(keywordList1);
        Map<String, Float> frequency2 = SimilarUtil.countWordFrequency(keywordList2);
        double cosineSimilarity = SimilarUtil.getCosineSimilarity(frequency1, frequency2);
        System.out.println("cosineSimilarity:" + cosineSimilarity);
    }

}