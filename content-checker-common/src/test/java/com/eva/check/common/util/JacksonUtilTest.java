package com.eva.check.common.util;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class JacksonUtilTest {

    @Test
    void testLocalDateTime() {
//        String content = "{\"createTime\": \"2024-04-24 11:47:52\"}";
        Result obj = new Result(LocalDateTime.now());
        String content = JacksonUtil.obj2String(obj);
        Result result = JacksonUtil.string2Obj(content, new TypeReference<>() {});
        System.out.println(result);
    }

    @Data
    static
    class Result {

        public Result() {
        }
        public Result(LocalDateTime createTIme) {
            this.createTIme = createTIme;
        }

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        private LocalDateTime createTIme;
    }
}
