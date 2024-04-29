package com.eva.check.service.core.impl;

import com.eva.check.pojo.ImportPaper;
import com.eva.check.pojo.converter.ImportPaperConverter;
import com.eva.check.pojo.dto.PaperAddReq;
import com.eva.check.service.config.ContentCheckAutoConfiguration;
import com.eva.check.service.core.PaperCollectService;
import com.eva.check.service.es.repository.PaperParagraphRepository;
import com.eva.check.service.support.ImportPaperService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SpringBootTest
@ContextConfiguration(classes = ContentCheckAutoConfiguration.class)
@EnableAutoConfiguration
public class PaperDataCreatorTest {

    @Autowired
    private PaperCollectService paperCollectService;

    @Autowired
    private ImportPaperService importPaperService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private PaperParagraphRepository paperParagraphRepository;

    public static final String INIT_DATA_PATH = "initDataWx";


    @Test
    void reset() throws IOException, URISyntaxException {
        testClearData();
        testInitData();
    }

    @Test
    void testInitData() throws IOException, URISyntaxException {
        Path directory = Paths.get(Objects.requireNonNull(PaperDataCreatorTest.class.getClassLoader().getResource(INIT_DATA_PATH)).toURI());
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            int i = 0;
            // 测试文件不多，这里就不用多线程了
            for (Path file : stream) {
                System.out.println(file);
                if (Files.isDirectory(file)) {
                    continue;
                }
                // 这里直接读取文件里面的内容
                String content = Files.readString(file);
                PaperAddReq paperAddReq = new PaperAddReq();
                paperAddReq.setContent(content)
                        .setTitle("测试数据" + (++i))
                        .setAuthor(file.getFileName().toString())
                        .setPaperNo("test:" + i)
                ;
                paperCollectService.addNewPaper(paperAddReq);

            }
        }
    }

    @Test
    void testInitBigData() throws IOException, URISyntaxException {
        Path directory = Paths.get(Objects.requireNonNull(PaperDataCreatorTest.class.getClassLoader().getResource("initDataSX")).toURI());
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            int i = 0;
            // 测试文件不多，这里就不用多线程了
            for (Path file : stream) {
                System.out.println(file);
                if (Files.isDirectory(file)) {
                    continue;
                }
                // 这里直接读取文件里面的内容
                String content = Files.readString(file);
                PaperAddReq paperAddReq = new PaperAddReq();
                paperAddReq.setContent(content)
                        .setTitle("测试数据" + (++i))
                        .setAuthor(file.getFileName().toString())
                        .setPaperNo("test:" + i)
                ;
                paperCollectService.addNewPaper(paperAddReq);

            }
        }
    }


    @Test
    void testInitMuchData() {

    }

    @Test
    void testClearData() {
        this.testClearBaseData();
        this.testClearCheckData();
        this.testClearEsData();
//        清空Redis较为危险，可以采用在Redis Client中进行手动处理的方式
        this.testClearRedis();
    }

    @Test
    void testClearRedis() {
        this.redisTemplate.execute((RedisCallback<String>) connection -> {
            connection.serverCommands().flushAll();
            return null;
        });

    }

    @Test
    void testClearBaseData() {
        this.jdbcTemplate.execute("delete from paper_ext");
        this.jdbcTemplate.execute("delete from paper_info");
        this.jdbcTemplate.execute("delete from paper_paragraph");
        this.jdbcTemplate.execute("delete from paper_sentence");
        this.jdbcTemplate.execute("delete from paper_token");
    }

    @Test
    void testClearCheckData() {
        this.jdbcTemplate.execute("delete from check_request");
        this.jdbcTemplate.execute("delete from check_task");
        this.jdbcTemplate.execute("delete from check_report");
        this.jdbcTemplate.execute("delete from check_paragraph");
        this.jdbcTemplate.execute("delete from check_paragraph_pair");
        this.jdbcTemplate.execute("delete from check_sentence");
        this.jdbcTemplate.execute("delete from check_sentence_pair");
        this.jdbcTemplate.execute("delete from check_paper");
        this.jdbcTemplate.execute("delete from check_paper_pair");
    }

    @Test
    void testClearEsData() {
        this.paperParagraphRepository.deleteAll();
    }

    @Test
    void testClearRocketMq() {
        /*this.rocketMQTemplate.getProducer().*/
    }

    @Test
    void testCreateImportData() throws IOException, URISyntaxException {
        Path directory = Paths.get(Objects.requireNonNull(PaperDataCreatorTest.class.getClassLoader().getResource(INIT_DATA_PATH)).toURI());
        List<ImportPaper> importPapers = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            int i = 0;
            // 测试文件不多，这里就不用多线程了
            for (Path file : stream) {
                System.out.println(file);
                if (Files.isDirectory(file)) {
                    continue;
                }
                // 这里直接读取文件里面的内容
                String content = Files.readString(file);
                PaperAddReq paperAddReq = new PaperAddReq();
                paperAddReq.setContent(content)
                        .setTitle("测试数据" + (++i))
                        .setAuthor(file.getFileName().toString())
                        .setPaperNo("test:" + i);
                ImportPaper importReq = ImportPaperConverter.INSTANCE.toImportReq(paperAddReq);
                importPapers.add(importReq);
            }
        }
        importPaperService.saveBatch(importPapers);
    }


}
