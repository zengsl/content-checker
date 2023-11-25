package com.eva.check.mapper;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.eva.check.mapper.config.MybatisPlusAutoConfiguration;
import com.eva.check.mapper.user.User;
import com.eva.check.mapper.user.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.Assert;

import java.util.List;

@MybatisPlusTest
@ActiveProfiles("h2")
@ContextConfiguration(classes = MybatisPlusAutoConfiguration.class)
public class SampleTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        Assert.isTrue(5 == userList.size(), "");
        userList.forEach(System.out::println);
    }

}