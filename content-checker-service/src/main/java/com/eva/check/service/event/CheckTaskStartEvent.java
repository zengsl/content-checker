package com.eva.check.service.event;

import com.eva.check.pojo.CheckTask;
import lombok.Builder;
import lombok.Data;

import java.util.List;


/**
 * 检测任务开始事件
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Data
@Builder
public class CheckTaskStartEvent {
    /**
     * 验证请求主键
     */
    private Long checkId;

    /**
     * 任务数: check_task
     */
    private Integer taskNum;

    private List<CheckTask> checkTasks;
}
