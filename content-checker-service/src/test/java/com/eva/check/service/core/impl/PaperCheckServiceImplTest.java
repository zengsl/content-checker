package com.eva.check.service.core.impl;

import com.eva.check.common.enums.CheckReqSource;
import com.eva.check.common.util.TextUtil;
import com.eva.check.pojo.dto.PaperCheckReq;
import com.eva.check.service.config.ContentCheckAutoConfiguration;
import com.eva.check.service.core.PaperCheckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.net.URISyntaxException;


@SpringBootTest
@ContextConfiguration(classes = ContentCheckAutoConfiguration.class)
@EnableAutoConfiguration
class PaperCheckServiceImplTest {
    public static final String CHECK_DATA_PATH = "checkData";

    @Autowired
    private PaperCheckService paperCheckService;
/*    private PaperCheckServiceImpl paperCheckServiceImpl;
    private PaperCollectService paperCollectService;
    private CheckRequestService checkRequestService;
    private CheckTaskService checkTaskService;*/

    @BeforeEach
    public void setUp() {
       /* checkRequestService = mock(CheckRequestService.class);
        checkTaskService = mock(CheckTaskService.class);
        paperCheckServiceImpl = new PaperCheckServiceImpl(paperCollectService, checkRequestService, checkTaskService);*/
    }

    @Test
    public void testCreatePaperCheck() {
        // Arrange
        /*PaperCheckReq paperCheckReq = new PaperCheckReq();
        paperCheckReq.setContent("test content");
        paperCheckReq.setTitle("test title");

        CheckRequest checkRequest = new CheckRequest();
        checkRequest.setCheckId(1L);

        List<CheckTask> checkTaskList = new ArrayList<>();

        CheckTask contentCheckTask = new CheckTask();
        contentCheckTask.setCheckId(checkRequest.getCheckId())
                .setCheckNo("")
                .setCheckType(DataType.FULL_TEXT.getValue())
                .setContent(paperCheckReq.getContent())
                .setStatus(CheckTaskStatus.INIT.getValue());
        checkTaskList.add(contentCheckTask);

        CheckTask titleCheckTask = new CheckTask();
        titleCheckTask.setCheckId(checkRequest.getCheckId())
                .setCheckNo("")
                .setCheckType(DataType.TITLE.getValue())
                .setContent(paperCheckReq.getTitle())
                .setStatus(CheckTaskStatus.CANCEL.getValue());
        checkTaskList.add(titleCheckTask);

        String checkNo = "testCheckNo";

//        when(PaperCheckServiceImpl.checkParams(paperCheckReq)).thenReturn(Void);
        when(StringUtils.hasText(paperCheckReq.getCheckNo())).thenReturn(false);
        when(NanoId.randomNanoId()).thenReturn(checkNo);
        when(checkRequestService.save(checkRequest)).thenReturn(true);
        when(checkTaskService.saveBatch(checkTaskList)).thenReturn(true);

        // Act
        String result = paperCheckServiceImpl.createPaperCheck(paperCheckReq);

        // Assert
        assertEquals(checkNo, result);*/
    }


    @Test
    void createPaperCheck() {
        String document = "客户关系管理（Customer Relationship Managemen-CRM）是指企业运用营销、关怀等手段时刻保持商业银行与实际客户和潜在客户交互的动作，客户关系管理是在企业发展过程中的一个产物，也是企业保持竞争力的重要方面。";
        PaperCheckReq paperCheckReq = new PaperCheckReq();
        paperCheckReq.setReqSource(CheckReqSource.API.getValue())
                .setContent(document)
                .setAuthor("zzz")
                .setTitle("Test Title")
                .setPublishYear("2019")
        ;
        this.paperCheckService.createPaperCheck(paperCheckReq);
    }

    @Test
    void createPaperCheck2() {
        String document = "客户关系管理(Customer Relationship Management CRM)，包括企业识别、挑选、获取、发展和保持客户的整个商业过程。它是理念、技术、实施";
        PaperCheckReq paperCheckReq = new PaperCheckReq();
        paperCheckReq.setReqSource(CheckReqSource.API.getValue())
                .setContent(document)
                .setAuthor("zzz")
                .setTitle("Test Title")
                .setPublishYear("2019")
        ;
        this.paperCheckService.createPaperCheck(paperCheckReq);
    }

    @Test
    void createPaperCheck3() throws URISyntaxException, IOException {
        String document = TextUtil.getCheckDocument("data9.txt");
        PaperCheckReq paperCheckReq = new PaperCheckReq();
        paperCheckReq.setReqSource(CheckReqSource.API.getValue())
                .setContent(document)
                .setPaperNo("test:9")
                .setAuthor("zzz")
                .setTitle("Test Title")
                .setPublishYear("2019")
        ;
        //
        this.paperCheckService.createPaperCheck(paperCheckReq);
    }

    @Test
    void createPaperCheckAndCollect() {
        String document = "客户关系管理（Customer Relationship Managemen-CRM）是指企业运用营销、关怀等手段时刻保持商业银行与实际客户和潜在客户交互的动作，客户关系管理是在企业发展过程中的一个产物，也是企业保持竞争力的重要方面。";
        PaperCheckReq paperCheckReq = new PaperCheckReq();
        paperCheckReq.setReqSource(CheckReqSource.API.getValue())
                .setContent(document)
                .setAuthor("zzz")
                .setTitle("Test Title")
                .setPublishYear("2019")
        ;

        this.paperCheckService.createPaperCheckAndCollect(paperCheckReq);
    }

    @Test
    void getPaperCheckReport() {
    }

    @Test
    void testDoubleCounter() {
        double a = 9.87D;
        double b = 0.6D;
        System.out.println(a + b);
    }
}