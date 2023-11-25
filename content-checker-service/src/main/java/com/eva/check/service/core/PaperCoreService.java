package com.eva.check.service.core;

import com.eva.check.pojo.PaperParagraph;
import com.eva.check.pojo.dto.SimilarPaperParagraph;

import java.util.List;

public interface PaperCoreService {

    void collectParagraph(PaperParagraph paperParagraph);

    List<SimilarPaperParagraph> findSimilarPaperParagraph(PaperParagraph paperParagraph);
}
