package com.eva.check.service.es.repository;

import com.eva.check.service.es.entity.PaperParagraphDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PaperParagraphRepository extends ElasticsearchRepository<PaperParagraphDoc, Long> {
}
