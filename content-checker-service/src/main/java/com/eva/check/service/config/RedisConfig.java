package com.eva.check.service.config;

import cn.hutool.core.util.ReflectUtil;
import com.eva.check.common.constant.CacheConstant;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static com.eva.check.service.config.ContentCheckAutoConfiguration.*;

/**
 * @author zengsl
 * @date 2024/4/17 11:32
 */
@Configuration
@EnableCaching
@AutoConfigureBefore(RedisAutoConfiguration.class)
/*
@EnableConfigurationProperties(EnvironmentProperties.class)
*/
@Slf4j
public class RedisConfig implements CachingConfigurer {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory, @Qualifier("keySerializer") RedisSerializer<String> keySerializer, @Qualifier("valueSerializer") RedisSerializer<?> valueSerializer) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(keySerializer);
        template.setValueSerializer(valueSerializer);
        template.setStringSerializer(keySerializer);

        // Hash的key也采用StringRedisSerializer的序列化方式
        template.setHashKeySerializer(keySerializer);
        template.setHashValueSerializer(valueSerializer);
//        template.setScriptExecutor(new ScriptExecutorEnhance<>(template));
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisCacheManagerBuilderCustomizer myRedisCacheManagerBuilderCustomizer(ResourceLoader resourceLoader) {
        // 缓存配置
        ClassLoader classLoader = resourceLoader.getClassLoader();
        RedisCacheConfiguration defaultCacheConfig = createDefaultRedisCacheConfiguration(classLoader);
        log.info("设置redis缓存的默认失效时间，失效时间默认为：{}天", defaultCacheConfig.getTtl().toDays());
        // 针对不同cacheName，设置不同的失效时间，map的key是缓存名称（注解设定的value/cacheNames），value是缓存的失效配置
        Map<String, RedisCacheConfiguration> initialCacheConfiguration = buildInitialCacheConfigurationMap(classLoader);
        return (builder) -> builder
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(initialCacheConfiguration);

    }

    private Map<String, RedisCacheConfiguration> buildInitialCacheConfigurationMap(ClassLoader classLoader) {
        Map<String, RedisCacheConfiguration> initialCacheConfiguration = new HashMap<>(8);

        // 设定失效时间
        // 比对库内的数据缓存可以设置的时间长一些
        initialCacheConfiguration.put(CacheConstant.PARAGRAPH_SENTENCE_CACHE_KEY, getDefaultSimpleConfiguration(classLoader).entryTtl(Duration.ofDays(30)));
        initialCacheConfiguration.put(CacheConstant.SENTENCE_TOKEN_CACHE_KEY, getDefaultSimpleConfiguration(classLoader).entryTtl(Duration.ofDays(30)));
        initialCacheConfiguration.put(CacheConstant.SENTENCE_PAPER_TOKEN_CACHE_KEY, getDefaultSimpleConfiguration(classLoader).entryTtl(Duration.ofDays(30)));
        initialCacheConfiguration.put(CacheConstant.SENTENCE_TOKEN_WORD_FREQ_CACHE_KEY, getDefaultSimpleConfiguration(classLoader).entryTtl(Duration.ofDays(30)));
        initialCacheConfiguration.put(CacheConstant.PARAGRAPH_TOKEN_CACHE_KEY, getDefaultSimpleConfiguration(classLoader).entryTtl(Duration.ofDays(30)));
//        initialCacheConfiguration.put(CacheConstant.REPORT_CONTENT_CACHE_KEY, getDefaultSimpleConfiguration(classLoader).entryTtl(Duration.ofDays(5)));
        initialCacheConfiguration.put(CacheConstant.REPORT_CONTENT_DTO_CACHE_KEY, getDefaultSimpleConfiguration(classLoader).entryTtl(Duration.ofDays(5)));
        initialCacheConfiguration.put(CacheConstant.CHECK_PROCESS_LOG_CACHE_KEY, getDefaultSimpleConfiguration(classLoader).entryTtl(Duration.ofDays(5)));
        // 检测数据的缓存不需要保留很久
        initialCacheConfiguration.put(CacheConstant.CHECK_TASK_CONTENT_CACHE_KEY, getDefaultSimpleConfiguration(classLoader).entryTtl(Duration.ofDays(2)));
        initialCacheConfiguration.put(CacheConstant.CHECK_TASK_PARA_CACHE_KEY, getDefaultSimpleConfiguration(classLoader).entryTtl(Duration.ofDays(2)));
        return initialCacheConfiguration;
    }

    private static RedisCacheConfiguration createDefaultRedisCacheConfiguration(ClassLoader classLoader) {
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig(classLoader)
                // 默认没有特殊指定的缓存，设置失效时间为15天
                .entryTtl(Duration.ofDays(15))
                // 在缓存名称前加上前缀
                .computePrefixWith(cacheName -> "default:" + cacheName + ":");
        defaultCacheConfig = getConfigWithDefaultSerialize(classLoader, defaultCacheConfig);
        return defaultCacheConfig;
    }

    /**
     * 覆盖默认的构造key[默认拼接的时候是两个冒号（::）]，否则会多出一个冒号
     *
     * @return 返回缓存配置信息
     */
    private RedisCacheConfiguration getDefaultSimpleConfiguration(ClassLoader classLoader) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        config = getConfigWithDefaultSerialize(classLoader, config);
        config.computePrefixWith(cacheName -> cacheName + ":");
        return config;
    }

    private static RedisCacheConfiguration getConfigWithDefaultSerialize(ClassLoader classLoader, RedisCacheConfiguration defaultCacheConfig) {
        return defaultCacheConfig.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new JdkSerializationRedisSerializer(classLoader)));
    }

    /*
    @Bean("valueSerializer")
    public RedisSerializer<?> valueSerializer() {
        // TODO 待改成jackson，并移除FastJson依赖
        return new FastJson2JsonRedisSerializer<>(Object.class);
    }*/

    //    @ConditionalOnProperty(name = {"sfis.env.enabled-isolation", "sfis.env.enabledIsolation"}, havingValue = "false")
    @Bean("keySerializer")
    public RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    /*@ConditionalOnProperty(name = {"sfis.env.enabled-isolation", "sfis.env.enabledIsolation"}, havingValue = "true", matchIfMissing = true)
    @Bean("keySerializer")
    public RedisSerializer<String> envIsolationKeySerializer(EnvironmentProperties environmentProperties) {
        return new EnvironmentIsolationSerializer(environmentProperties);
    }*/

    @Bean("valueSerializer")
    public static RedisSerializer<?> buildRedisSerializer() {
        // 使用Spring默认的序列化器，可考虑自定义并使用JacksonUtil来实现
        RedisSerializer<Object> json = RedisSerializer.json();
        // 解决 LocalDateTime 的序列化
        ObjectMapper objectMapper = (ObjectMapper) ReflectUtil.getFieldValue(json, "mapper");

        // 以下配置参考JacksonUtil
        //java8日期 Local系列序列化和反序列化模块
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        //序列化
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(TIME_FORMAT)));
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        //反序列化
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));

//        objectMapper.registerModule(new ParameterNamesModule()).registerModule(new Jdk8Module()).registerModule(javaTimeModule);

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        // 忽略json字符串中不识别的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 忽略无法转换的对象
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // PrettyPrinter 格式化输出
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        // NULL不参与序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 指定时区
        objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        // Date日期类型字符串全局处理, 默认格式为：yyyy-MM-dd HH:mm:ss
        // 局部处理某个Date属性字段接收或返回日期格式yyyy-MM-dd, 可采用@JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")注解标注该属性
        objectMapper.setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT));
        return json;
    }
}
