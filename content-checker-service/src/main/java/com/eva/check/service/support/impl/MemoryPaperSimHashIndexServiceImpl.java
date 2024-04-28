package com.eva.check.service.support.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.eva.check.common.constant.ContentCheckConstant;
import com.eva.check.common.util.SimilarUtil;
import com.eva.check.pojo.PaperInfo;
import com.eva.check.pojo.PaperParagraph;
import com.eva.check.pojo.dto.SimilarPaperParagraph;
import com.eva.check.service.support.PaperInfoService;
import com.eva.check.service.support.PaperParagraphService;
import com.eva.check.service.support.PaperSimHashIndexService;
import com.google.common.collect.Lists;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author zzz
 * @date 2023/11/25 16:12
 */
@RequiredArgsConstructor
public class MemoryPaperSimHashIndexServiceImpl implements PaperSimHashIndexService {
    private final int fracCount = 4;

    private final PaperInfoService paperInfoService;
    private final PaperParagraphService paperParagraphService;

    /**
     * 按照分段存储simhash，查找更快速
     */
    @Getter
    private final List<Map<String, List<SimilarPaperInfo>>> storage = List.of(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());

    /**
     * 按照分段存储simhash，查找更快速
     */
    @Getter
    private final List<Map<String, List<SimilarPaperParagraph>>> paragraphStorage = List.of(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());

    /*@PostConstruct*/
    public void init() {
        this.rebuildAllIndex();
    }

    @Override
    public void addHash(PaperInfo paperInfo) {
//        Assert.notNull(paperInfo, SystemException.withExSupplier(PaperErrorCode.PARAM_INVALID));
        Assert.notNull(paperInfo, "Invalid parameter, paperInfo cannot be null");
        setHashIndex(storage.get(0), paperInfo::getHash1, paperInfo);
        setHashIndex(storage.get(1), paperInfo::getHash2, paperInfo);
        setHashIndex(storage.get(2), paperInfo::getHash3, paperInfo);
        setHashIndex(storage.get(3), paperInfo::getHash4, paperInfo);
    }

    @Override
    public void addParagraphHash(PaperParagraph paperParagraph) {
        Assert.notNull(paperParagraph, "Invalid parameter, paperInfo cannot be null");
        setHashIndex(paragraphStorage.get(0), paperParagraph::getHash1, paperParagraph);
        setHashIndex(paragraphStorage.get(1), paperParagraph::getHash2, paperParagraph);
        setHashIndex(paragraphStorage.get(2), paperParagraph::getHash3, paperParagraph);
        setHashIndex(paragraphStorage.get(3), paperParagraph::getHash4, paperParagraph);
    }


    private void setHashIndex(Map<String, List<SimilarPaperInfo>> fracMap, Supplier<String> supplier, PaperInfo paperInfo) {
        String key = supplier.get();
        Assert.notBlank(key, "Invalid key,hash segment cannot be empty");
        List<SimilarPaperInfo> indexDataList = fracMap.computeIfAbsent(key, s -> Lists.newArrayList());
        SimilarPaperInfo similarPaperInfo = SimilarPaperInfo.builder().paperId(paperInfo.getPaperId()).simHash(paperInfo.getHash()).paperNo(paperInfo.getPaperNo()).build();
        indexDataList.add(similarPaperInfo);
    }


    private void setHashIndex(Map<String, List<SimilarPaperParagraph>> fracMap, Supplier<String> supplier, PaperParagraph paperParagraph) {
        String key = supplier.get();
        Assert.notBlank(key, "Invalid key,hash segment cannot be empty");
        List<SimilarPaperParagraph> indexDataList = fracMap.computeIfAbsent(key, s -> Lists.newArrayList());
        SimilarPaperParagraph similarPaperInfo = SimilarPaperParagraph.builder().paperId(paperParagraph.getPaperId()).paragraphId(paperParagraph.getParagraphId()).simHash(paperParagraph.getHash()).paperNo(paperParagraph.getPaperNo()).build();
        indexDataList.add(similarPaperInfo);
    }

    @Override
    public List<SimilarPaperParagraph> findSimilarPaper(PaperParagraph paperParagraph) {
        return findSimilarPaper(paperParagraph, similarPaperParagraph -> SimilarUtil.calSimHahSimilar(paperParagraph.getHash(), similarPaperParagraph.getSimHash()) > ContentCheckConstant.PRE_CHECK_THRESHOLD);
    }

    @Override
    public List<SimilarPaperParagraph> findSimilarPaper(PaperParagraph paperParagraph, Predicate<SimilarPaperParagraph> predicate) {
        // 不要和自己比较
        Predicate<SimilarPaperParagraph> notSamePredicate = similarPaperInfo -> StrUtil.isBlank(paperParagraph.getPaperNo()) || !similarPaperInfo.getPaperNo().equals(paperParagraph.getPaperNo());
        Predicate<SimilarPaperParagraph> similarPaperInfoPredicate = notSamePredicate.and(predicate);
        List<SimilarPaperParagraph> result = Lists.newArrayList();
        if (paragraphStorage.get(0).containsKey(paperParagraph.getHash1())) {
            for (SimilarPaperParagraph similarPaperInfo : paragraphStorage.get(0).get(paperParagraph.getHash1())) {
                // 当汉明距离小于标准时相似
                if (similarPaperInfoPredicate.test(similarPaperInfo)) {
                    result.add(similarPaperInfo);
                }
            }
        }
        if (paragraphStorage.get(1).containsKey(paperParagraph.getHash2())) {
            for (SimilarPaperParagraph similarPaperInfo : paragraphStorage.get(1).get(paperParagraph.getHash2())) {
                // 当汉明距离小于标准时相似
                if (similarPaperInfoPredicate.test(similarPaperInfo)) {
                    result.add(similarPaperInfo);
                }
            }
        }
        if (paragraphStorage.get(2).containsKey(paperParagraph.getHash3())) {
            for (SimilarPaperParagraph similarPaperInfo : paragraphStorage.get(2).get(paperParagraph.getHash3())) {
                // 当汉明距离小于标准时相似
                if (similarPaperInfoPredicate.test(similarPaperInfo)) {
                    result.add(similarPaperInfo);
                }
            }
        }
        if (paragraphStorage.get(3).containsKey(paperParagraph.getHash4())) {
            for (SimilarPaperParagraph similarPaperInfo : paragraphStorage.get(3).get(paperParagraph.getHash4())) {
                // 当汉明距离小于标准时相似
                if (similarPaperInfoPredicate.test(similarPaperInfo)) {
                    result.add(similarPaperInfo);
                }
            }
        }
        return result;
    }

    @Override
    public List<SimilarPaperInfo> findSimilarPaper(PaperInfo paperInfo) {
        return findSimilarPaper(paperInfo, similarPaperInfo -> SimilarUtil.calSimHahSimilar(paperInfo.getHash(), similarPaperInfo.getSimHash()) > ContentCheckConstant.PRE_CHECK_THRESHOLD);
    }

    @Override
    public List<SimilarPaperInfo> findSimilarPaper(PaperInfo paperInfo, Predicate<SimilarPaperInfo> predicate) {
        // 不要和自己比较
        Predicate<SimilarPaperInfo> notSamePredicate = similarPaperInfo -> StrUtil.isBlank(paperInfo.getPaperNo()) || !similarPaperInfo.getPaperNo().equals(paperInfo.getPaperNo());
        Predicate<SimilarPaperInfo> similarPaperInfoPredicate = notSamePredicate.and(predicate);
        List<SimilarPaperInfo> result = Lists.newArrayList();
        if (storage.get(0).containsKey(paperInfo.getHash1())) {
            for (SimilarPaperInfo similarPaperInfo : storage.get(0).get(paperInfo.getHash1())) {
                // 当汉明距离小于标准时相似
                if (similarPaperInfoPredicate.test(similarPaperInfo)) {
                    result.add(similarPaperInfo);
                }
            }
        } else if (storage.get(1).containsKey(paperInfo.getHash2())) {
            for (SimilarPaperInfo similarPaperInfo : storage.get(1).get(paperInfo.getHash2())) {
                // 当汉明距离小于标准时相似
                if (similarPaperInfoPredicate.test(similarPaperInfo)) {
                    result.add(similarPaperInfo);
                }
            }
        } else if (storage.get(2).containsKey(paperInfo.getHash3())) {
            for (SimilarPaperInfo similarPaperInfo : storage.get(2).get(paperInfo.getHash3())) {
                // 当汉明距离小于标准时相似
                if (similarPaperInfoPredicate.test(similarPaperInfo)) {
                    result.add(similarPaperInfo);
                }
            }
        } else if (storage.get(3).containsKey(paperInfo.getHash4())) {
            for (SimilarPaperInfo similarPaperInfo : storage.get(3).get(paperInfo.getHash4())) {
                // 当汉明距离小于标准时相似
                if (similarPaperInfoPredicate.test(similarPaperInfo)) {
                    result.add(similarPaperInfo);
                }
            }
        }
        return result;
    }

    @Override
    public void rebuildAllIndex() {
        List<PaperInfo> paperInfoList = paperInfoService.list();
        List<PaperParagraph> paperParagraphList = this.paperParagraphService.list();

        paperInfoList.forEach(this::addHash);
        paperParagraphList.forEach(this::addParagraphHash);
    }
}
