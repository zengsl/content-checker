package com.eva.check.common.util;

import org.junit.jupiter.api.Test;

import java.util.List;

class SimHashUtilTest {

    @Test
    void testHash() {
        String document = "客户关系管理（Customer Relationship Managemen-CRM）是指企业运用营销、关怀等手段时刻保持商业银行与实际客户和潜在客户交互的动作，客户关系管理是在企业发展过程中的一个产物，也是企业保持竞争力的重要方面。";
        SimHashUtil.SimHash simHash = SimHashUtil.pretreatmentAndBuildSimHash2(document);
    }


    @Test
    void hash() {
        String document = "客户关系管理（Customer Relationship Managemen-CRM）是指企业运用营销、关怀等手段时刻保持商业银行与实际客户和潜在客户交互的动作，客户关系管理是在企业发展过程中的一个产物，也是企业保持竞争力的重要方面。";
        String document2 = "客户关系管理(Customer Relationship Management CRM)，包括企业识别、挑选、获取、发展和保持客户的整个商业过程。它是理念、技术、实施";
        List<String> stringList = TextUtil.cleanAndSegment(document);
        List<String> stringList2 = TextUtil.cleanAndSegment(document2);
        long hash = SimHashUtil.hash(stringList);
        List<String> strings = SimHashUtil.splitSimHash(hash);
        long hash2 = SimHashUtil.hash(stringList2);
        List<String> strings2 = SimHashUtil.splitSimHash(hash2);

        System.out.println("hash: " + hash);
        System.out.println("hash2: " + hash2);

        System.out.println("strings: "+ strings);
        System.out.println("strings2: "+ strings2);


        int hammingDistance = SimilarUtil.getHammingDistance(hash, hash2);
        System.out.println("hammingDistance: " + hammingDistance);
        Double similar = SimilarUtil.calSimHahSimilar(hash, hash2);
        System.out.println("similar: " + similar);

    }
    @Test
    void hash2() {
        String document = "客户关系管理（Customer Relationship Managemen-CRM）是指企业运用营销、关怀等手段时刻保持商业银行与实际客户和潜在客户交互的动作，客户关系管理是在企业发展过程中的一个产物，也是企业保持竞争力的重要方面。";
        String document2 = "客户关系管理(Customer Relationship Management CRM)，包括企业识别、挑选、获取、发展和保持客户的整个商业过程。它是理念、技术、实施";
        SimHashUtil.SimHash simHash = SimHashUtil.pretreatmentAndBuildSimHash(document);
        SimHashUtil.SimHash simHash2 = SimHashUtil.pretreatmentAndBuildSimHash(document2);
        int hammingDistance = SimilarUtil.getHammingDistance(simHash.getSimHash(), simHash2.getSimHash());
        System.out.println("hammingDistance: " + hammingDistance);

        SimHashUtil.SimHash simHash3 = SimHashUtil.pretreatmentAndBuildSimHash(document);
        SimHashUtil.SimHash simHash4 = SimHashUtil.pretreatmentAndBuildSimHash(document2);
        int hammingDistance2 = SimilarUtil.getHammingDistance(simHash3.getSimHash(), simHash4.getSimHash());
        System.out.println("hammingDistance2: " + hammingDistance2);

    }
}