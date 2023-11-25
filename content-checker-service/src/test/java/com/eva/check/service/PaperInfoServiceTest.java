package com.eva.check.service;

import com.eva.check.pojo.PaperInfo;
import com.eva.check.service.config.ContentCheckAutoConfiguration;
import com.eva.check.service.support.PaperInfoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = ContentCheckAutoConfiguration.class)
@EnableAutoConfiguration
class PaperInfoServiceTest {

    @Autowired
    PaperInfoService paperInfoService;

    @Test
    public void test() {
        PaperInfo paperInfo = paperInfoService.getById(-1L);
        Assertions.assertNull(paperInfo);
    }
}