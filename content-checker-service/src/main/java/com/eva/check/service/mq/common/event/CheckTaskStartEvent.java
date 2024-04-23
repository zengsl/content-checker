package com.eva.check.service.mq.common.event;

import com.eva.check.pojo.CheckTask;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 检测任务开始事件
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckTaskStartEvent {

    /**
     * 验证请求Id
     */
    private Long checkId;

    /**
     * 任务数: check_task
     */
    private Integer taskNum;

    private List<CheckTask> checkTasks;
}
