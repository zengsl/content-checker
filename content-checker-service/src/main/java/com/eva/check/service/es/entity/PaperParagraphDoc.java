package com.eva.check.service.es.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.time.LocalDateTime;

/**
 * 论文段落的Es对象
 * <p>
 * 索引通过手动在Es中设置，而不通过该实体进行自动创建
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Setting(useServerConfiguration = true)
@Document(indexName = "paper-paragraph", createIndex = false)
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public class PaperParagraphDoc {
    /**
     * 段落主键
     */
    @Id
    private Long paragraphId;

    private Long paragraphNum;

    private Long paperId;

    private String paperNo;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String content;

    @Field(type = FieldType.Date, format = {}, pattern = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "epoch_millis"})
    private LocalDateTime createTime;

    @Field(type = FieldType.Date, format = {}, pattern = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "epoch_millis"})
    private LocalDateTime updateTime;
}
