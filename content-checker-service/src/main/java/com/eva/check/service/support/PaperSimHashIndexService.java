package com.eva.check.service.support;

import com.eva.check.pojo.PaperInfo;
import com.eva.check.pojo.PaperParagraph;
import com.eva.check.pojo.dto.SimilarPaperParagraph;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.function.Predicate;

/**
 * 论文SimHash存储服务
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
public interface PaperSimHashIndexService {

    @Data
    @Builder
    class SimilarPaperInfo {
        private Long paperId;
        private String paperNo;
        private Long simHash;
    }


    void addHash(PaperInfo paperInfo);
    void addParagraphHash(PaperParagraph paperParagraph);

    List<SimilarPaperParagraph> findSimilarPaper(PaperParagraph paperParagraph);
    List<SimilarPaperParagraph> findSimilarPaper(PaperParagraph paperParagraph, Predicate<SimilarPaperParagraph> predicate);

    List<SimilarPaperInfo> findSimilarPaper(PaperInfo paperInfo);

    List<SimilarPaperInfo> findSimilarPaper(PaperInfo paperInfo, Predicate<SimilarPaperInfo> predicate);

    void rebuildAllIndex();
}
