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

    @Test
    void hash3() {
        String document = "我们的研究项目主要探索高科技材料在太阳能电池中的应用。首先，通过对肿瘤癌细胞进行研究、分析，发现了一种具有潜在抗肿瘤活性化合物质的存在，这种化合物质十分特殊，且具有显著特点。科技创新实现新的突破。国家实验室体系建设有力推进。关键核心技术攻关成果丰硕，航空发动机、燃气轮机、第四代核电机组等高端装备研制取得长足进展，人工智能、量子技术等前沿领域创新成果不断涌现。技术合同成交额增长28.6%。创新驱动发展能力持续提升。 最终，实验结果揭示了：该化合物能够选择性地抑制肿瘤细胞的增殖并诱导其凋亡，但对正常细胞的影响不大，对普通系统的影响也非常有限。所以，该发现为抗肿瘤药物的研发提供了新的候选药物方向，增加了攻克肿瘤问题的可能性。今后，我们正进行进一步的研究探索，评估该化合物在人体内的安全性和有效性，加速研究进度，早日产出研究成果，为推动高质量发展注入新动能。";
        String document2 = "本研究关注于气候变化对生态系统的影响及其适应机制。我们通过收集和分析大量的生态数据，研究了气候变化对物种分布、种群动态和生态系统功能的影响。结果表明，气候变化导致了许多物种的分布范围发生变化，种群数量也呈现出波动趋势。同时，我们还发现一些物种通过改变生活习性、迁移或进化等方式来适应气候变化。基于这些发现，我们提出了一系列保护生物多样性和维护生态系统稳定的措施，为应对气候变化提供了科学依据。";
        SimHashUtil.SimHash simHash = SimHashUtil.pretreatmentAndBuildSimHash(document);
        SimHashUtil.SimHash simHash2 = SimHashUtil.pretreatmentAndBuildSimHash(document2);
        int hammingDistance = SimilarUtil.getHammingDistance(simHash.getSimHash(), simHash2.getSimHash());
        System.out.println("hammingDistance: " + hammingDistance);
        System.out.println("similarity: " + SimilarUtil.calSimHahSimilarWithHamming(hammingDistance));

        SimHashUtil.SimHash simHash3 = SimHashUtil.pretreatmentAndBuildSimHash(document);
        SimHashUtil.SimHash simHash4 = SimHashUtil.pretreatmentAndBuildSimHash(document2);
        int hammingDistance2 = SimilarUtil.getHammingDistance(simHash3.getSimHash(), simHash4.getSimHash());
        System.out.println("hammingDistance2: " + hammingDistance2);
        System.out.println("similarity: " + SimilarUtil.calSimHahSimilarWithHamming(hammingDistance2));


    }
}