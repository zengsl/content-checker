package com.eva.check.pojo.converter;

import com.eva.check.pojo.dto.PaperResult;
import com.eva.check.pojo.dto.ParagraphResult;
import com.eva.check.pojo.dto.SentenceResult;
import com.eva.check.pojo.vo.ReportDetailParagraphVO;
import com.google.common.collect.Maps;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

/**
 *
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
        paperResult.getParagraphResultList().forEach(e ->{
            similarSentenceResultMap.putAll(e.getSimilarSentenceResultMap());
        });
        return similarSentenceResultMap;
    }

    default List<ReportDetailParagraphVO> paperResult2paragraphVO(PaperResult paperResult){
        if (paperResult == null || paperResult.getParagraphResultList() == null || paperResult.getParagraphResultList().isEmpty()) {
                        return null;
        }
        return paragraphResult2VO(paperResult.getParagraphResultList());
    }

    ReportDetailParagraphVO paragraphResult2VO(ParagraphResult paragraphResult);

    List<ReportDetailParagraphVO> paragraphResult2VO(List<ParagraphResult> paragraphResults);
}
