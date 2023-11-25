package com.eva.check.mapper;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.eva.check.mapper.config.MybatisPlusAutoConfiguration;
import com.eva.check.pojo.PaperInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertNull;


@MybatisPlusTest
@ActiveProfiles("h2")
@ContextConfiguration(classes = MybatisPlusAutoConfiguration.class)
class PaperInfoMapperTest {

    @Autowired
    private PaperInfoMapper paperInfoMapper;

//    @Test
    public void testSelect() {
        PaperInfo paperInfo = paperInfoMapper.selectById(-1L);
        assertNull(paperInfo);
    }
}