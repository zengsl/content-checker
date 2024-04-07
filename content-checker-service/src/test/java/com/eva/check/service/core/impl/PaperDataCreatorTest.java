package com.eva.check.service.core.impl;

import com.eva.check.pojo.dto.PaperAddReq;
import com.eva.check.service.config.ContentCheckAutoConfiguration;
import com.eva.check.service.core.PaperCollectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@SpringBootTest
@ContextConfiguration(classes = ContentCheckAutoConfiguration.class)
@EnableAutoConfiguration
public class PaperDataCreatorTest {

    @Autowired
    private PaperCollectService paperCollectService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static final String INIT_DATA_PATH = "initDataGz";

    @Test
    void testInitData() throws IOException, URISyntaxException {
        Path directory = Paths.get(Objects.requireNonNull(PaperDataCreatorTest.class.getClassLoader().getResource(INIT_DATA_PATH)).toURI());
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            int i =0 ;
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
    void testClearData() {
        this.testClearBaseData();
        this.testClearCheckData();
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
    }
}
