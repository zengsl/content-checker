package com.eva.check.mapper.handler;


import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.eva.check.pojo.common.BaseEntity;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 通用参数填充实现类
 * <p>
 * 如果没有显式的对通用参数进行赋值，这里会对通用参数进行填充、赋值
 *
 * @author zengsl
 * @date 2023/3/8 16:43
 */
public class DefaultDataFieldHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        if (Objects.nonNull(metaObject) && (metaObject.getOriginalObject() instanceof BaseEntity baseEntity)) {

            LocalDateTime current = LocalDateTime.now();
            // 创建时间为空，则以当前时间为插入时间
            if (Objects.isNull(baseEntity.getCreateTime())) {
                baseEntity.setCreateTime(current);
            }
            // 更新时间为空，则以当前时间为更新时间
            if (Objects.isNull(baseEntity.getUpdateTime())) {
                baseEntity.setUpdateTime(current);
            }

        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时间为空，则以当前时间为更新时间
        setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
    }
}
