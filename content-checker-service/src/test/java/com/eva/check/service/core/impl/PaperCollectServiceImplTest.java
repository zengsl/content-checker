package com.eva.check.service.core.impl;

import com.eva.check.common.constant.ContentCheckConstant;
import com.eva.check.common.enums.DataType;
import com.eva.check.common.enums.PaperErrorCode;
import com.eva.check.common.exception.SystemException;
import com.eva.check.pojo.PaperInfo;
import com.eva.check.pojo.dto.PaperAddReq;
import com.eva.check.service.config.ContentCheckAutoConfiguration;
import com.eva.check.service.core.PaperCollectService;
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
class PaperCollectServiceImplTest {

    private static final String PAPER_NO = "xxxx-1111-test";

    @Autowired
    private PaperCollectService paperCollectService;

    @Autowired
    private PaperInfoService paperInfoService;
    @Test
    void addNewPaperWithErrorParams() {
        SystemException systemException = Assertions.assertThrows(SystemException.class, () -> paperCollectService.addNewPaper(null));
        Assertions.assertEquals(PaperErrorCode.PARAM_INVALID,systemException.getErrorCode());
    }

    @Test
    void addNewPaper() {
        String document = "客户关系管理（Customer Relationship Managemen-CRM）是指企业运用营销、关怀等手段时刻保持商业银行与实际客户和潜在客户交互的动作，客户关系管理是在企业发展过程中的一个产物，也是企业保持竞争力的重要方面。";
        PaperAddReq paperAddReq = new PaperAddReq();
        paperAddReq.setPaperNo(PAPER_NO)
                .setDataType(DataType.FULL_TEXT.getValue())
                .setDataSource(ContentCheckConstant.DATA_SOURCE_DEFAULT)
                .setAuthor("zzz")
                .setTitle("Test Title")
                .setContent(document)
                .setPublishYear("2019")
                ;
        String paperNo = paperCollectService.addNewPaper(paperAddReq);
        Assertions.assertNotNull(paperNo);
        PaperInfo paperInfo = paperInfoService.getByPaperNo(paperNo);
        Assertions.assertNotNull(paperInfo);
    }

    @Test
    void removePaperByNo() {
        String document = "使用客户关系管理系统可以实现销售管理透明化，在系统中随时记录客户信息，并进行业务进程跟踪，销售人员可记录下与客户的每一次联系， 包括联系时间、联系结果、客户意向以及客户的基本情况等。 既可以方便管理者随时了解工作情况，又可以发掘潜在商机，防止因商机遗漏造成客户资源流失。商机代表销售机会，潜在客户通过跟进可以转化商机，CRM 能够以一致的格式管理这些潜在客户信息。 通过 CRM 客户关系管理系统，可以查看到客户的来源、基本信息以及跟进情况等，从而分析出客户的潜在需求， 采取不同的销售策略，而将潜在客户转变为商机。";
        PaperAddReq paperAddReq = new PaperAddReq();
        paperAddReq.setPaperNo(PAPER_NO)
                .setDataType(DataType.FULL_TEXT.getValue())
                .setDataSource(ContentCheckConstant.DATA_SOURCE_DEFAULT)
                .setAuthor("zzz")
                .setTitle("Test Title")
                .setContent(document)
                .setPublishYear("2019")

                ;
        String paperNo = paperCollectService.addNewPaper(paperAddReq);
        PaperInfo paperInfo = paperInfoService.getByPaperNo(paperNo);
        Assertions.assertNotNull(paperInfo);
        paperCollectService.removePaperByNo(PAPER_NO);
        PaperInfo paperInfo2 = paperInfoService.getByPaperNo(paperNo);
        Assertions.assertNull(paperInfo2);
    }

}