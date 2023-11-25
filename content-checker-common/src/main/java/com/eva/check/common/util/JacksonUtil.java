package com.eva.check.common.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * jackson工具类 用于JSON序列化和反序列化
 *
 * @author zzz
 * @version V1.0
 * @date 2023/11/25 16:12
 */
@Slf4j
final public class JacksonUtil {

    private JacksonUtil() {

    }

    /**
     * ObjectMapper实例用于处理JSON
     * <p> ObjectMapper实例是线程安全的，保持单例用于提升性能。
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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


    // Json序列化和反序列化转换器
    static {
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

        OBJECT_MAPPER.registerModule(new ParameterNamesModule()).registerModule(new Jdk8Module()).registerModule(javaTimeModule);

        OBJECT_MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OBJECT_MAPPER.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        // 忽略json字符串中不识别的属性
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 忽略无法转换的对象
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        // PrettyPrinter 格式化输出
        OBJECT_MAPPER.configure(SerializationFeature.INDENT_OUTPUT, false);
        // NULL不参与序列化
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 指定时区
        OBJECT_MAPPER.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));

        // Date日期类型字符串全局处理, 默认格式为：yyyy-MM-dd HH:mm:ss
        // 局部处理某个Date属性字段接收或返回日期格式yyyy-MM-dd, 可采用@JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")注解标注该属性
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT));
    }

    /**
     * 将对象转换成字符串
     *
     * @param obj 待转换对象
     * @return json字符串
     */
    public static <T> String obj2String(T obj) {
        if (obj == null) {
            return null;
        }
        String s = null;
        try {
            s = obj instanceof String ? (String) obj : OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 将对象转换成【格式化后】的字符串
     *
     * @param obj 待转换对象
     * @return json字符串
     */
    public static <T> String obj2StringWithPretty(T obj) {
        if (obj == null) {
            return null;
        }
        try {
            return obj instanceof String ? (String) obj : OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * 字符串转对象
     *
     * @param jsonStr json字符串
     * @param clazz   对象类型
     * @return 转换后目标对象
     */
    public static <T> T string2Obj(String jsonStr, Class<T> clazz) {
        if (jsonStr == null || jsonStr.length() == 0 || clazz == null) {
            return null;
        }
        T t = null;
        try {
            t = clazz.equals(String.class) ? (T) jsonStr : OBJECT_MAPPER.readValue(jsonStr, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("string2Obj失败", e);
        }
        return t;
    }

    /**
     * 在字符串与集合对象转换时使用(处理复杂泛型数据)【推荐使用】
     * 用法：string2Obj(jsonStr, new TypeReference<List<xxx>>(){})
     *
     * @param jsonStr       json字符串
     * @param typeReference TypeReference实例
     * @return 转换后目标对象
     */
    public static <T> T string2Obj(String jsonStr, TypeReference<T> typeReference) {
        if (jsonStr == null || jsonStr.length() == 0 || typeReference == null) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ? jsonStr : OBJECT_MAPPER.readValue(jsonStr, typeReference));
        } catch (IOException e) {
            log.error("string2Obj失败", e);
            return null;
        }
    }

    /**
     * 在字符串与集合对象转换时使用(处理复杂泛型数据)
     * <p>
     * 可以直接传Class或者用JavaType构建之后传入
     * <p>
     * 如果想要转换为List<Set<Integer>>，那么这么使用：
     * <p> JavaType inner = TypeFactory.constructParametricType(Set.class, Integer.class)
     * string2Obj(jsonStr, List.class, inner.getRawClass())
     * TypeFactory快速获取方式TypeFactory.defaultInstance()
     *
     * @param jsonStr         json字符串
     * @param collectionClazz 集合类型
     * @param elementClazzes  集合元素类型
     * @return 转换后目标对象
     */
    public static <T> T string2Obj(String jsonStr, Class<?> collectionClazz, Class<?>... elementClazzes) {
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(collectionClazz, elementClazzes);
        try {
            return OBJECT_MAPPER.readValue(jsonStr, javaType);
        } catch (IOException e) {
            log.error("string2Obj失败", e);
            return null;
        }
    }

    /**
     * 在字符串与集合对象转换时使用(处理复杂泛型数据)
     * 如果想要转换为List<Set<Integer>>，那么这么使用：
     * <p> JavaType inner = TypeFactory.constructParametricType(Set.class, Integer.class)
     * string2Obj(jsonStr, List.class, inner)
     * TypeFactory快速获取方式TypeFactory.defaultInstance()
     *
     * @param jsonStr        json字符串
     * @param rawType        集合类型
     * @param parameterTypes 集合元素类型
     * @return 转换后目标对象
     */
    public static <T> T string2Obj(String jsonStr, Class<?> rawType, JavaType... parameterTypes) {
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(rawType, parameterTypes);
        try {
            return OBJECT_MAPPER.readValue(jsonStr, javaType);
        } catch (IOException e) {
            log.error("string2Obj失败", e);
            return null;
        }
    }

    /**
     * Json字符串构造器
     */
    public static JsonBuilder builder() {
        return new JsonBuilder();
    }

    public static class JsonBuilder {

        /**
         * 存放json属性-值
         */
        private final Map<String, Object> attrs = new HashMap<>();

        private JsonBuilder() {
        }

        /**
         * 设置json属性
         */
        public JsonBuilder put(String key, Object value) {
            this.attrs.put(key, value);
            return this;
        }

        /**
         * 批量设置json属性
         */
        public JsonBuilder putAll(Map<String, Object> attrs) {
            this.attrs.putAll(attrs);
            return this;
        }

        /**
         * 清空所有属性
         */
        public void clear() {
            attrs.clear();
        }

        /**
         * 构建Json字符串
         */
        public String build() {
            try {
                return OBJECT_MAPPER.writeValueAsString(this.attrs);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return "{}";
        }
    }

}
