package com.eva.check.service.flow;

/**
 * 执行日志接口
 *
 * @author zengsl
 * @date 2024/4/23 15:42
 */
public interface IProcessLogService {

    /**
     * @param checkId 检测Id
     * @param msg     消息
     */
    void log(String processType, Long checkId, String msg);
}
