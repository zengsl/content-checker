package com.eva.check.service.flow.enums;

/**
 * 任务检测状态
 *
 * @author zengsl
 * @date 2024/4/11 15:36
 */
public interface ICheckState {

    /**
     * 获取名称
     *
     * @return 状态名称
     */
    String getName();

    /**
     * 获取状态值
     *
     * @return 状态值
     */
    String getValue();

    /**
     * 获取下一个状态
     *
     * @return 下一个状态
     */
    ICheckState nextState();
}
