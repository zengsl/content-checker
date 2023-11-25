package com.eva.check.mapper.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * mybatis-plus自动配置
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@AutoConfiguration
@MapperScan("com.eva.check.mapper")
public class MybatisPlusAutoConfiguration {


}
