package com.eva.check.pojo.dto;

import lombok.Builder;
import lombok.Data;

/**
 *
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Data
@Builder
public
class SimilarPaperParagraph {
    private Long paragraphId;
    private Long paperId;
    private String paperNo;
    private Long simHash;
}
