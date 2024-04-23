package com.eva.check.pojo.vo;

import com.eva.check.pojo.CheckRequest;
import com.eva.check.pojo.CheckTask;
import com.eva.check.pojo.dto.SentenceResult;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author zengsl
 * @date 2024/4/23 14:04
 */
@Data
@Builder
public class CheckReportContentDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1780729346791738276L;

    private String finalSimilarity;
    private CheckRequest checkRequest;
    private CheckTask contentCheckTask;
    private List<ReportDetailParagraphVO> reportParagraphs;
    private Map<Long, SentenceResult> similarSentenceResultMap;
    private List<SimilarPaperVO> allSimilarPaperList;
    private Boolean isDownload;
}
