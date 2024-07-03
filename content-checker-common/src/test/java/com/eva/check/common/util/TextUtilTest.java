package com.eva.check.common.util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import com.hankcs.hanlp.model.perceptron.PerceptronLexicalAnalyzer;
import com.hankcs.hanlp.seg.common.Term;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;

import static org.junit.jupiter.api.Assertions.*;


class TextUtilTest {

    @Test
    public void testSplitSentence() {
        String document = "使用客户关系管理系统可以实现销售管理透明化，在系统中随时记录客户信息，并进行业务进程跟踪，销售人员可记录下与客户的每一次联系， 包括联系时间、联系结果、客户意向以及客户的基本情况等。 既可以方便管理者随时了解工作情况，又可以发掘潜在商机，防止因商机遗漏造成客户资源流失。商机代表销售机会，潜在客户通过跟进可以转化商机，CRM 能够以一致的格式管理这些潜在客户信息。 通过 CRM 客户关系管理系统，可以查看到客户的来源、基本信息以及跟进情况等，从而分析出客户的潜在需求， 采取不同的销售策略，而将潜在客户转变为商机。";
        List<String> stringList = TextUtil.splitSentence(document);
        assertNotNull(stringList);
        assertNotEquals(0, stringList.size());
        for (String s : stringList) {
            System.out.println("Sentence: " + s);
        }
    }

    @Test
    public void testCleanAndExtractKeyword() {
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
    }

    @Deprecated
    @Test
    void testResult2() throws IOException, URISyntaxException {
        String document = TextUtil.getCheckDocument("data10.txt");
//        String document = "客户关系管理(Customer Relationship Management CRM)，包括企业识别、挑选、获取、发展和保持客户的整个商业过程。它是理念、技术、实施";

//        System.out.println("document:" + document);
        String newSentence = TextUtil.pretreatment(document);
        System.out.println(newSentence.length());
//        System.out.println("newSentence:" + newSentence);
        List<String> strings0 = HanLP.extractKeyword(newSentence, 25);
        List<String> strings = HanLP.extractKeyword(newSentence, 20);
        List<String> strings2 = HanLP.extractKeyword(newSentence, 15);
        List<String> strings3 = HanLP.extractKeyword(newSentence, 10);
        /*System.out.println("strings0:" + strings0);
        System.out.println("string00:" + strings);
        System.out.println("strings2:" + strings2);
        System.out.println("strings3:" + strings3);*/
        List<Term> segment = HanLP.segment(newSentence);
        System.out.println(segment);

        List<String> strings1 = HanLP.extractPhrase(newSentence, 10);
        System.out.println("strings1:" + strings1);

        Map<String, Float> stringFloatLinkedHashMap = TextUtil.extractKeyword(newSentence, 15);
        System.out.println(stringFloatLinkedHashMap);

        Map<String, Float> stringFloatLinkedHashMap2 = TextUtil.extractKeyword(newSentence, 20);
        System.out.println(stringFloatLinkedHashMap2);
    }

    @Deprecated
    @Test
    void testResult3() throws IOException, URISyntaxException {
        String document = TextUtil.getCheckDocument("data9.txt");
        String document2 = TextUtil.getCheckDocument("data15.txt");
        /* String document = "客户关系管理（Customer Relationship Managemen-CRM）是指企业运用营销、关怀等手段时刻保持商业银行与实际客户和潜在客户交互的动作，客户关系管理是在企业发展过程中的一个产物，也是企业保持竞争力的重要方面。";
        String document2 = "客户关系管理(Customer Relationship Management CRM)，包括企业识别、挑选、获取、发展和保持客户的整个商业过程。它是理念、技术、实施";*/
        String newSentence = TextUtil.pretreatment(document);
        String newSentence2 = TextUtil.pretreatment(document2);
        Map<String, Float> stringFloatLinkedHashMap = TextUtil.extractKeyword(newSentence, 6);
        System.out.println(stringFloatLinkedHashMap);

        Map<String, Float> stringFloatLinkedHashMap2 = TextUtil.extractKeyword(newSentence2, 6);
        System.out.println(stringFloatLinkedHashMap2);

        long hash = SimHashUtil.hash(stringFloatLinkedHashMap);
        long hash2 = SimHashUtil.hash(stringFloatLinkedHashMap2);
        System.out.println("hash: " + hash);
        System.out.println("hash2: " + hash2);

        int hammingDistance = SimilarUtil.getHammingDistance(hash, hash2);
        System.out.println("hammingDistance: " + hammingDistance);

        Double similar = SimilarUtil.calSimHahSimilarWithHamming(hammingDistance);
        System.out.println("similar: " + similar);


    }


    @Test
    void testSeg() throws IOException, URISyntaxException {
        String document = TextUtil.getCheckDocument("data9.txt");
//        String document = "客户关系管理（Customer Relationship Managemen-CRM）是指企业运用营销、关怀等手段时刻保持商业银行与实际客户和潜在客户交互的动作，客户关系管理是在企业发展过程中的一个产物，也是企业保持竞争力的重要方面。";
        List<Term> termList = HanLP.segment(document);
        System.out.println("termList:" + termList);

        PerceptronLexicalAnalyzer analyzer = new PerceptronLexicalAnalyzer("data/model/perceptron/pku199801/cws.bin",
                HanLP.Config.PerceptronPOSModelPath,
                HanLP.Config.PerceptronNERModelPath);

        List<Term> seg = analyzer.seg(document);
        System.out.println("seg:" + seg);


        CRFLexicalAnalyzer analyzer2 = new CRFLexicalAnalyzer();
        List<Term> scrfSeg = analyzer2.seg(document);
        System.out.println("scrfSeg:" + scrfSeg);

        Map<String, Float> defaultMap = TextUtil.topKeyword(termList, 10);
        Map<String, Float> lexicalMap = TextUtil.topKeyword(seg, 10);
        Map<String, Float> crfMap = TextUtil.topKeyword(scrfSeg, 10);
        System.out.println("defaultMap: " + defaultMap);
        System.out.println("lexicalMap: " + lexicalMap);
        System.out.println("crfMap: " + crfMap);

    }

    @Test
    void testSeg2() throws IOException, URISyntaxException {
        String document = TextUtil.getDocument("data" + File.separator + "sxBigData.txt");
        String document2 = TextUtil.getCheckDocument("data0.txt");
        List<String> extractKeyword = HanLP.extractKeyword(document, 10);
        List<String> extractKeyword2 = HanLP.extractKeyword(document2, 10);
        System.out.println("extractKeyword:" + extractKeyword);
        System.out.println("extractKeyword2:" + extractKeyword2);

    }

    @Test
    void testSeg3() throws IOException, URISyntaxException {
        String document = TextUtil.getDocument("data" + File.separator + "sxBigData.txt");

        List<String> extractKeyword = HanLP.extractKeyword(document, 30);
        List<String> extractKeyword2 = HanLP.extractKeyword(document, 20);
        List<String> extractKeyword3 = HanLP.extractKeyword(document, 15);
        List<String> extractKeyword4 = HanLP.extractKeyword(document, 10);
        List<String> extractKeyword5 = HanLP.extractKeyword(document, 5);
        System.out.println("extractKeyword:" + extractKeyword);
        System.out.println("extractKeyword:" + TextUtil.cleanAndExtractKeyword(document, 30));
        System.out.println("extractKeyword2:" + extractKeyword2);
        System.out.println("extractKeyword2:" + TextUtil.cleanAndExtractKeyword(document, 20));
        System.out.println("extractKeyword3:" + extractKeyword3);
        System.out.println("extractKeyword3:" + TextUtil.cleanAndExtractKeyword(document, 15));
        System.out.println("extractKeyword4:" + extractKeyword4);
        System.out.println("extractKeyword4:" + TextUtil.cleanAndExtractKeyword(document, 10));
        System.out.println("extractKeyword5:" + extractKeyword5);
        System.out.println("extractKeyword5:" + TextUtil.cleanAndExtractKeyword(document, 5));
    }

    @Test
    void testSeg4() throws IOException, URISyntaxException {
        String document = TextUtil.getCheckDocument("data0.txt");

        List<String> extractKeyword = HanLP.extractKeyword(document, 30);
        List<String> extractKeyword2 = HanLP.extractKeyword(document, 20);
        List<String> extractKeyword3 = HanLP.extractKeyword(document, 15);
        List<String> extractKeyword4 = HanLP.extractKeyword(document, 10);
        List<String> extractKeyword5 = HanLP.extractKeyword(document, 5);
        System.out.println("extractKeyword:" + extractKeyword);
        System.out.println("extractKeyword:" + TextUtil.cleanAndExtractKeyword(document, 30));
        System.out.println("extractKeyword2:" + extractKeyword2);
        System.out.println("extractKeyword2:" + TextUtil.cleanAndExtractKeyword(document, 20));
        System.out.println("extractKeyword3:" + extractKeyword3);
        System.out.println("extractKeyword3:" + TextUtil.cleanAndExtractKeyword(document, 15));
        System.out.println("extractKeyword4:" + extractKeyword4);
        System.out.println("extractKeyword4:" + TextUtil.cleanAndExtractKeyword(document, 10));
        System.out.println("extractKeyword4:" + extractKeyword4);
        System.out.println("extractKeyword4:" + TextUtil.cleanAndExtractKeyword(document, 5));
    }

    @Test
    void testSmartSplitSentence() {
        List<String> strings = TextUtil.smartSplitSentence("你好，我是李四。我是李四。我是李四。我是李四。我是李四。我是李四。我是李四。我是李四。我是李四。我是李四。我是李四。我是李四。我是");
        System.out.println("strings：" + strings);
        List<String> strings2 = TextUtil.smartSplitSentence("你好。我是。李四。我是。");
        List<String> except2 = List.of("你好。我是。李四。我是。");
        System.out.println("strings2：" + strings2);
        assertEquals(strings2, except2);
    }
}