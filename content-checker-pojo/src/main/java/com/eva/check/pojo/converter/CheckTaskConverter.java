package com.eva.check.pojo.converter;

import com.eva.check.pojo.CheckTask;
import com.eva.check.pojo.dto.MqCheckTask;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 检测任务转换器
 *
 * @author zengsl
 * @date 2024/4/15 16:51
 */
@Mapper
public interface CheckTaskConverter {
    CheckTaskConverter INSTANCE = Mappers.getMapper(CheckTaskConverter.class);

    MqCheckTask checkTask2MqCheckTask(CheckTask checkTask);
    List<MqCheckTask> checkTask2MqCheckTask(List<CheckTask> checkTask);
}
