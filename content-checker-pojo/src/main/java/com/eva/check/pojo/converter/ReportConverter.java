package com.eva.check.pojo.converter;

import com.eva.check.pojo.dto.PaperResult;
import com.eva.check.pojo.dto.ParagraphResult;
import com.eva.check.pojo.dto.SentenceResult;
import com.eva.check.pojo.vo.CheckReportContentDTO;
import com.eva.check.pojo.vo.ReportDetailParagraphVO;
import com.google.common.collect.Maps;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

/**
 * @author zzz
 * @date 2023/11/28 22:41
 */
@Mapper
public interface ReportConverter {
    ReportConverter INSTANCE = Mappers.getMapper(ReportConverter.class);


    default Map<Long, SentenceResult> paperResult2SentenceMap(PaperResult paperResult) {
        if (paperResult == null || paperResult.getParagraphResultList() == null || paperResult.getParagraphResultList().isEmpty()) {
            return null;
        }
        Map<Long, SentenceResult> similarSentenceResultMap = Maps.newHashMap();
        paperResult.getParagraphResultList().forEach(e -> {
            if (e.getSimilarSentenceResultMap() == null || e.getSimilarSentenceResultMap().isEmpty()) {
                return;
            }
            similarSentenceResultMap.putAll(e.getSimilarSentenceResultMap());
        });
        return similarSentenceResultMap;
    }

    default List<ReportDetailParagraphVO> paperResult2paragraphVO(PaperResult paperResult) {
        if (paperResult == null || paperResult.getParagraphResultList() == null || paperResult.getParagraphResultList().isEmpty()) {
            return null;
        }
        return paragraphResult2VO(paperResult.getParagraphResultList());
    }

    @Mapping(target = "paragraphNum", ignore = true)
    ReportDetailParagraphVO paragraphResult2VO(ParagraphResult paragraphResult);

    List<ReportDetailParagraphVO> paragraphResult2VO(List<ParagraphResult> paragraphResults);

    default Map<String, Object> reportContentDto2Map(CheckReportContentDTO checkReportContentDTO) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("finalSimilarity", checkReportContentDTO.getFinalSimilarity());
        params.put("checkRequest", checkReportContentDTO.getCheckRequest());
        params.put("contentCheckTask", checkReportContentDTO.getContentCheckTask());
        params.put("reportParagraphs", checkReportContentDTO.getReportParagraphs());
        params.put("similarSentenceResultMap", checkReportContentDTO.getSimilarSentenceResultMap());
        params.put("allSimilarPaperList", checkReportContentDTO.getAllSimilarPaperList());
        params.put("isDownload", checkReportContentDTO.getIsDownload());
        params.put("wordCount", checkReportContentDTO.getWordCount());
        params.put("sentenceCount", checkReportContentDTO.getSentenceCount());
        return params;
    }

    /*@Mapping(target = "finalSimilarity", source = "checkReportContentDTO.finalSimilarity")
    @Mapping(target = "checkRequest", source = "checkReportContentDTO.checkRequest")
    @Mapping(target = "contentCheckTask", source = "checkReportContentDTO.contentCheckTask")
    @Mapping(target = "reportParagraphs", source = "checkReportContentDTO.reportParagraphs")
    @Mapping(target = "similarSentenceResultMap", source = "checkReportContentDTO.similarSentenceResultMap")
    @Mapping(target = "allSimilarPaperList", source = "checkReportContentDTO.allSimilarPaperList")
    @Mapping(target = "isDownload", source = "checkReportContentDTO.isDownload")
    Map<String, Object> reportContentDto2Map(CheckReportContentDTO checkReportContentDTO) ;*/
}
