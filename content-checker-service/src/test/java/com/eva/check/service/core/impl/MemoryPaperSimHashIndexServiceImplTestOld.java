package com.eva.check.service.core.impl;

import com.eva.check.common.util.SimHashUtil;
import com.eva.check.pojo.PaperInfo;
import com.eva.check.service.support.PaperInfoService;
import com.eva.check.service.support.PaperParagraphService;
import com.eva.check.service.support.PaperSimHashIndexService;
import com.eva.check.service.support.impl.MemoryPaperSimHashIndexServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;


class MemoryPaperSimHashIndexServiceImplTestOld {
    private static final String PAPER_NO = "xxxx-1111-test";
    private static final String DEFAULT_TEST_DOC = "客户关系管理（Customer Relationship Managemen-CRM）是指企业运用营销、关怀等手段时刻保持商业银行与实际客户和潜在客户交互的动作，客户关系管理是在企业发展过程中的一个产物，也是企业保持竞争力的重要方面。";

    @Mock
    private PaperInfoService paperInfoService;
    @Mock
    private PaperParagraphService paperParagraphService;
    private MemoryPaperSimHashIndexServiceImpl memoryPaperSimHashIndexService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        memoryPaperSimHashIndexService = new MemoryPaperSimHashIndexServiceImpl(paperInfoService, paperParagraphService);
    }

    @Test
    public void testAddHash() {
        long simHash = -4712657485262536311L;
        long paperId = 1111L;
        PaperInfo paperAddReq = new PaperInfo();
        List<String> strings = SimHashUtil.splitSimHash(simHash);
        paperAddReq.setPaperId(paperId);
        paperAddReq.setHash(simHash);
        paperAddReq.setHash1(strings.get(0));
        paperAddReq.setHash2(strings.get(1));
        paperAddReq.setHash3(strings.get(2));
        paperAddReq.setHash4(strings.get(3));

        this.memoryPaperSimHashIndexService.addHash(paperAddReq);
        List<Map<String, List<PaperSimHashIndexService.SimilarPaperInfo>>> storage = memoryPaperSimHashIndexService.getStorage();
        Assertions.assertNotNull(storage);
        Assertions.assertEquals(4, storage.size());
        Assertions.assertEquals(simHash, storage.get(0).get(strings.get(0)).get(0).getSimHash());
        Assertions.assertEquals(paperId, storage.get(0).get(strings.get(0)).get(0).getPaperId());

        Assertions.assertEquals(simHash, storage.get(1).get(strings.get(1)).get(0).getSimHash());
        Assertions.assertEquals(paperId, storage.get(1).get(strings.get(1)).get(0).getPaperId());

        Assertions.assertEquals(simHash, storage.get(2).get(strings.get(2)).get(0).getSimHash());
        Assertions.assertEquals(paperId, storage.get(2).get(strings.get(2)).get(0).getPaperId());

        Assertions.assertEquals(simHash, storage.get(3).get(strings.get(3)).get(0).getSimHash());
        Assertions.assertEquals(paperId, storage.get(3).get(strings.get(3)).get(0).getPaperId());
    }

    @Test
    public void testRebuildAllIndexWithError() {
        // Arrange
        List<PaperInfo> list = new ArrayList<>();
        list.add(new PaperInfo());
        list.add(new PaperInfo());
        when(paperInfoService.list()).thenReturn(list);

        // Act
        Assertions.assertThrowsExactly(IllegalArgumentException.class, () -> memoryPaperSimHashIndexService.rebuildAllIndex());

    }

    @Test
    public void testRebuildAllIndex() {
        long simHash = -4712657485262536311L;
        List<String> hashList = SimHashUtil.splitSimHash(simHash);

        long paperId = 1111L;
        // Arrange
        List<PaperInfo> list = new ArrayList<>();
        PaperInfo paperInfo = PaperInfo.builder()
                .paperId(paperId)
                .hash(simHash).hash1(hashList.get(0)).hash2(hashList.get(1)).hash3(hashList.get(2)).hash4(hashList.get(3))
                .build();
        list.add(paperInfo);
        when(paperInfoService.list()).thenReturn(list);
        memoryPaperSimHashIndexService.rebuildAllIndex();
        // Act
        // Assert
        verify(paperInfoService, times(1)).list();

    }


    @Test
    void test() {
        /*String document = "客户关系管理（Customer Relationship Managemen-CRM）是指企业运用营销、关怀等手段时刻保持商业银行与实际客户和潜在客户交互的动作，客户关系管理是在企业发展过程中的一个产物，也是企业保持竞争力的重要方面。";
        PaperInfo paperAddReq = PaperInfo.builder()
                .paperNo(PAPER_NO)
                .author("zzz")
                .title("Test Title")
                .content(document)
                .publishYear("2019")
                .dataType(DataType.FULL_TEXT.getValue())
                .dataSource(DataSource.INTERNET.getValue())
                .build();
        paperSimHashIndexService.addHash(paperAddReq);
        List<PaperSimHashIndexService.SimplePaperInfo> similarPaperList = paperSimHashIndexService.findSimilarPaper(paperAddReq);

        similarPaperList.forEach(System.out::println);*/
    }
}