package com.eva.check.service.event;

import com.eva.check.pojo.CheckTask;
import lombok.Builder;
import lombok.Data;

/**
 * 检测任务取消事件
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Data
@Builder
public class CheckTaskCancelEvent {

    private CheckTask checkTask;

}
