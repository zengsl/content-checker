package com.eva.check.service.support;

import cn.hutool.core.lang.id.NanoId;
import cn.hutool.core.util.RandomUtil;
import com.eva.check.pojo.ImportPaper;
import com.eva.check.pojo.converter.ImportPaperConverter;
import com.eva.check.pojo.dto.PaperAddReq;
import com.eva.check.service.config.ContentCheckAutoConfiguration;
import com.eva.check.service.core.PaperCollectService;
import com.eva.check.service.core.impl.PaperDataCreatorTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StopWatch;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@SpringBootTest
@ContextConfiguration(classes = ContentCheckAutoConfiguration.class)
@EnableAutoConfiguration
@Slf4j
class ImportPaperServiceTest {

    @Autowired
    private ImportPaperService importPaperService;

    @Autowired
    private PaperCollectService paperCollectService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Test
    void importPaper() {
        this.importPaperService.importPaper();
    }

    @Test
    void loadImportData() {
    }

    @Test
    void testResetImportData() {
        this.jdbcTemplate.execute("update  import_paper t set t.status = '0'");

    }

    @Test
    void testClearImportData() {
        this.jdbcTemplate.execute("delete from import_paper");
    }

    @Test
    void initTestData() throws IOException, URISyntaxException {
        StopWatch stopWatch = new StopWatch("【initTestData】");
        stopWatch.start("【读取文件】");
        Path filePath = Paths.get(Objects.requireNonNull(PaperDataCreatorTest.class.getClassLoader().getResource("initDataSX" + File.separator + "data0.txt")).toURI());
        String content = Files.readString(filePath);
        int length = content.length();
        stopWatch.stop();
        List<ImportPaper> importPapers = new ArrayList<>();

        stopWatch.start("【批量处理数据】");
        int randomCount = 5;
        int size = length / 20;
        int saveBatchSize = 5000;
        IntStream.range(0, 80000).forEach(i -> {
            long startTime = System.currentTimeMillis();
            int count = 0;
            int times = (int) (Math.random() * randomCount);
            String finalContent = content;
            while (count < times) {
                int choice = (int) (Math.random() * 10);
                int end = (int) (Math.random() * length);
                int start = Math.max(end - size, 0);
                if (choice % 2 == 0) {
                    // 随机截取
                    finalContent = content.replace(content.substring(start, end), "");
                } else {
                    String randomString = RandomUtil.randomString(size);
                    finalContent = content.replace(content.substring(start, end), randomString);
                }
                count++;
            }
            PaperAddReq paperAddReq = new PaperAddReq();
            int no = 1 + i;
            paperAddReq.setContent(finalContent)
                    .setTitle("测试数据" + no + RandomUtil.randomStringUpper(2))
                    .setAuthor("测试作者" + RandomUtil.randomStringUpper(5))
                    .setPaperNo("test:" + NanoId.randomNanoId());

            ImportPaper importReq = ImportPaperConverter.INSTANCE.toImportReq(paperAddReq);
            importPapers.add(importReq);
            log.info("第{}次，耗时:{}", no, (System.currentTimeMillis() - startTime));

            if (importPapers.size() == saveBatchSize) {
                StopWatch stopWatch2 = new StopWatch("Inner【批量保存数据】");
                stopWatch2.start();
                this.transactionTemplate.execute((status)->{
                    try {
                        this.importPaperService.saveBatch(importPapers, saveBatchSize);
                    } catch (Exception e) {
                        // 如果操作失败，抛出异常，事务将回滚
                        status.setRollbackOnly();
                        log.error("批量保存失败", e);
                    }
                    return null;
                });
                stopWatch2.stop();
                log.info("【initTestData】提前分批保存  数据总量:{} ，耗时：{}s ，详情：{}", importPapers.size(), stopWatch2.getTotalTimeSeconds(), stopWatch2.prettyPrint());
                importPapers.clear();
            }
        });


        if (!importPapers.isEmpty()) {
            stopWatch.stop();
            stopWatch.start("【批量保存数据】");
            this.transactionTemplate.execute((status)->{
                try {
                    this.importPaperService.saveBatch(importPapers, saveBatchSize);
                } catch (Exception e) {
                    // 如果操作失败，抛出异常，事务将回滚
                    status.setRollbackOnly();
                    log.error("批量保存失败", e);
                }
                return null;
            });
            importPapers.clear();
            stopWatch.stop();
        }

        log.info("【initTestData】方法执行结束  数据总量:{} ，耗时：{}s ，详情：{}", importPapers.size(), stopWatch.getTotalTimeSeconds(), stopWatch.prettyPrint());
    }

}