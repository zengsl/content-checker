package com.eva.check.pojo.common;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 基础实体
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Data
public class BaseEntity {

    private LocalDateTime createTime;

    private  LocalDateTime updateTime;
}
