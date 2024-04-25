package com.eva.check.service.support;

import com.eva.check.service.config.ContentCheckAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
@SpringBootTest
@ContextConfiguration(classes = ContentCheckAutoConfiguration.class)
@EnableAutoConfiguration
class ImportPaperServiceTest {

    @Autowired
    private ImportPaperService importPaperService;

    @Test
    void importPaper() {
        this.importPaperService.importPaper();
    }

    @Test
    void loadImportData() {
    }

    void reset() {

    }
}