package com.eva.check.mapper.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.eva.check.mapper.handler.DefaultDataFieldHandler;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * mybatis-plus自动配置
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@AutoConfiguration
@MapperScan("com.eva.check.mapper")
public class MybatisPlusAutoConfiguration {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 数据权限处理
//        interceptor.addInnerInterceptor(dataPermissionInterceptor());
        // 分页插件
//        interceptor.addInnerInterceptor(paginationInnerInterceptor());
        // 乐观锁插件
//        interceptor.addInnerInterceptor(optimisticLockerInnerInterceptor());
        // 防全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());




        return interceptor;
    }

    @Bean
    public MetaObjectHandler defaultMetaObjectHandler() {
        // 自动填充参数类
        return new DefaultDataFieldHandler();
    }
}
