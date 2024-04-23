package com.eva.check.service.flow.impl;

import com.eva.check.common.constant.CacheConstant;
import com.eva.check.pojo.CheckTask;
import com.eva.check.service.flow.IContentCheckTaskBaseFlow;
import com.eva.check.service.flow.IProcessLogService;
import com.eva.check.service.flow.enums.ContentCheckState;
import com.eva.check.service.mq.producer.SendMqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 内容检测任务流程
 *
 * @author zengsl
 * @date 2024/4/11 11:39
 */
@Service
@Slf4j
public class ContentCheckTaskFlow extends BaseCheckTaskFlow implements IContentCheckTaskBaseFlow {

    public ContentCheckTaskFlow(SendMqService sendMqService, IProcessLogService processLogService) {
        super(sendMqService, processLogService);
    }

    @Override
    public void processStateNext(CheckTask checkTask, ContentCheckState currentState) {
        // 获取下一个需要执行的状态
        ContentCheckState nextState = currentState.nextState();
        switch (nextState) {
            case PRE_CHECK:
                this.getProcessLogService().log(CacheConstant.CHECK_PROCESS_LOG_CACHE_KEY, checkTask.getCheckId(), "检测任务：" + checkTask.getTaskId() + " 开始预检测");
                // 内容预检测
                this.getSendMqService().doContentPreCheck(checkTask);
                break;
            case PARAGRAPH_CHECK:
                this.getProcessLogService().log(CacheConstant.CHECK_PROCESS_LOG_CACHE_KEY, checkTask.getCheckId(), "检测任务：" + checkTask.getTaskId() + " 段落比对检测");
                // 触发比对事件事件
                this.getSendMqService().doParagraphCheck(checkTask);
                break;
            case COLLECT_RESULT:
                this.getProcessLogService().log(CacheConstant.CHECK_PROCESS_LOG_CACHE_KEY, checkTask.getCheckId(), "检测任务：" + checkTask.getTaskId() + " 汇总段落检测结果");
                // 汇总段落检测对的结果
                this.getSendMqService().doCollectResult(checkTask);
                break;
            case FINISH:
                // 任务结束
                super.processFinish(checkTask);
                break;
            default:
                log.warn("无法匹配导对应的执行状态，nextState={}", nextState);
                break;
        }
    }

}
