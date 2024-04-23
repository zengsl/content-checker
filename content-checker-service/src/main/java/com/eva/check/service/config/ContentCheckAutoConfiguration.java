package com.eva.check.service.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportOptions;
import co.elastic.clients.transport.Version;
import co.elastic.clients.transport.rest_client.RestClientOptions;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.eva.check.common.constant.MessageQueueConstants;
import com.eva.check.service.core.*;
import com.eva.check.service.core.impl.*;
import com.eva.check.service.mq.consumer.eventbus.CheckTaskEventBusListenerImpl;
import com.eva.check.service.mq.consumer.eventbus.ContentCheckEventBusListenerImpl;
import com.eva.check.service.mq.consumer.rocket.CheckTaskRocketListenerImpl;
import com.eva.check.service.mq.consumer.rocket.ContentCheckRocketListenerImpl;
import com.eva.check.service.mq.producer.SendMqService;
import com.eva.check.service.mq.producer.eventbus.EventBusSendMqServiceImpl;
import com.eva.check.service.mq.producer.eventbus.listener.CheckTaskEventBusListener;
import com.eva.check.service.mq.producer.eventbus.listener.ContentCheckEventBusListener;
import com.eva.check.service.mq.producer.rocket.RocketSendMqServiceImpl;
import com.eva.check.service.support.*;
import com.eva.check.service.support.impl.MemoryPaperSimHashIndexServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.AutoCloseableElasticsearchClient;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

/**
 * Paper服务配置
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@AutoConfiguration
@EnableConfigurationProperties(value = {CheckProperties.class})
@ComponentScan("com.eva.check.service")
@RequiredArgsConstructor
@EnableCaching
@EnableElasticsearchRepositories("com.eva.check.service.es.repository")
public class ContentCheckAutoConfiguration extends ElasticsearchConfiguration {

    private final ElasticsearchProperties elasticsearchProperties;

    @Bean
    SimilarTextRender paragraphRender() {
        // 文本渲染
        return new ParagraphRenderImpl();
    }

    @Bean
    SimilarityCollectStrategy similarityStrategy() {
        // 相似度策略
        return new DefaultSimilarityCollectStrategy();
    }

    @Bean
    SimilarTextRule similarTextRule() {
        // 文本渲染
        return new DefaultSimilarTextRuleImpl();
    }

    /*@Bean
    CheckTaskExecuteService checkTaskDispatcher(com.eva.check.service.support.CheckTaskService checkTaskService, CheckRequestService checkRequestService, DuplicateCheckPrepareService duplicateCheckPrepareService, CheckReportService checkReportService) {
        return new DefaultCheckTaskExecuteServiceImpl(checkRequestService, checkTaskService, duplicateCheckPrepareService, checkReportService);
    }*/


    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "content-check", name = "mq", havingValue = MessageQueueConstants.EVENT_BUS, matchIfMissing = true)
    protected static class GuavaStrategy {
        @Bean
        CheckTaskExecuteService checkTaskExecuteService(CheckRequestService checkRequestService, CheckTaskService checkTaskService, DuplicateCheckPrepareService duplicateCheckPrepareService, CheckReportService checkReportService) {
            return new DefaultCheckTaskExecuteServiceImpl(checkRequestService, checkTaskService, duplicateCheckPrepareService, checkReportService);
        }

        @Bean
        SendMqService sendMqService(CheckTaskEventBusListener checkTaskEventBusListener, ContentCheckEventBusListener contentCheckEventBusListener) {
            return new EventBusSendMqServiceImpl(checkTaskEventBusListener, contentCheckEventBusListener);
        }

        @Bean
        CheckTaskEventBusListener checkTaskEventBusListener(CheckTaskExecuteService checkTaskExecuteService) {
            return new CheckTaskEventBusListenerImpl(checkTaskExecuteService);
        }

        @Bean
        ContentCheckEventBusListener contentCheckEventBusListener(DuplicateCheckService duplicateCheckService) {
            return new ContentCheckEventBusListenerImpl(duplicateCheckService);
        }

    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "content-check", name = "mq", havingValue = MessageQueueConstants.ROCKET_MQ)
    protected static class RocketStrategy {

        @Bean
        CheckTaskExecuteService checkTaskExecuteService(CheckRequestService checkRequestService, CheckTaskService checkTaskService, DuplicateCheckPrepareService duplicateCheckPrepareService, RedisTemplate<String, Integer> redisTemplate, CheckReportService checkReportService) {
            return new RedisCheckTaskExecuteServiceImpl(checkRequestService, checkTaskService, duplicateCheckPrepareService, redisTemplate, checkReportService);
        }

        @Bean
        SendMqService sendMqService(RocketMQTemplate rocketMqTemplate) {
            return new RocketSendMqServiceImpl(rocketMqTemplate);
        }

        @Bean
        ContentCheckRocketListenerImpl contentCheckRocketListener() {
            return new ContentCheckRocketListenerImpl();
        }

        @Bean
        CheckTaskRocketListenerImpl checkTaskRocketListener() {
            return new CheckTaskRocketListenerImpl();
        }
    }

    @Bean
    public PaperSimHashIndexService paperSimHashIndexService(PaperInfoService paperInfoService, PaperParagraphService paperParagraphService) {
        return new MemoryPaperSimHashIndexServiceImpl(paperInfoService, paperParagraphService);
    }

    @Nonnull
    @Override
    public ClientConfiguration clientConfiguration() {
        List<String> uris = elasticsearchProperties.getUris();
        String url = "localhost:9200";
        if (uris == null) {
            throw new RuntimeException("需要配置spring.elasticsearch相关信息");
        }
        if (uris.get(0).startsWith("http://")) {
            url = uris.get(0).replace("http://", "");
        } else if (uris.get(0).startsWith("https://")) {
            url = uris.get(0).replace("https://", "");
        }
        return ClientConfiguration.builder()
                .connectedTo(url)
                .usingSsl()
                .withBasicAuth(elasticsearchProperties.getUsername(), elasticsearchProperties.getPassword())
                .build();
    }


    // -------------------------------- 以下代码只为给默认的JacksonJsonpMapper中的ObjectMapper设置Java时间模块，其他代码均从ElasticsearchConfiguration和ElasticsearchClients中复制过来，不做修改不做修改。如果新版本支持更优雅的配置方式或者支持Java时间模块则可以删除以下代码------------------------------------------------

    private static final String X_SPRING_DATA_ELASTICSEARCH_CLIENT = "X-SpringDataElasticsearch-Client";
    private static final String IMPERATIVE_CLIENT = "imperative";

    @Override
    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient restClient) {

        Assert.notNull(restClient, "restClient must not be null");

        ElasticsearchTransport transport = getElasticsearchTransport(restClient, IMPERATIVE_CLIENT, transportOptions());

        return new AutoCloseableElasticsearchClient(transport);
    }

    private static ElasticsearchTransport getElasticsearchTransport(RestClient restClient, String clientType,
                                                                    @Nullable TransportOptions transportOptions) {

        TransportOptions.Builder transportOptionsBuilder = transportOptions != null ? transportOptions.toBuilder()
                : new RestClientOptions(RequestOptions.DEFAULT).toBuilder();

        ContentType jsonContentType = Version.VERSION == null ? ContentType.APPLICATION_JSON
                : ContentType.create("application/vnd.elasticsearch+json",
                new BasicNameValuePair("compatible-with", String.valueOf(Version.VERSION.major())));

        Consumer<String> setHeaderIfNotPresent = header -> {
            if (transportOptionsBuilder.build().headers().stream() //
                    .noneMatch((h) -> h.getKey().equalsIgnoreCase(header))) {
                // need to add the compatibility header, this is only done automatically when not passing in custom options.
                // code copied from RestClientTransport as it is not available outside the package
                transportOptionsBuilder.addHeader(header, jsonContentType.toString());
            }
        };

        setHeaderIfNotPresent.accept("Content-Type");
        setHeaderIfNotPresent.accept("Accept");

        TransportOptions transportOptionsWithHeader = transportOptionsBuilder
                .addHeader(X_SPRING_DATA_ELASTICSEARCH_CLIENT, clientType).build();

        ObjectMapper objectMapper = new ObjectMapper();
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
        objectMapper.registerModule(javaTimeModule);

        return new RestClientTransport(restClient, new JacksonJsonpMapper(objectMapper), transportOptionsWithHeader);
    }


    /**
     * 默认日期时间格式
     */
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认日期格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 默认时间格式
     */
    public static final String TIME_FORMAT = "HH:mm:ss";


}
