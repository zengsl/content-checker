package com.eva.check.service.flow.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 内容检测任务状态
 *
 * @author zengsl
 * @date 2024/4/11 11:20
 */
@Getter
@RequiredArgsConstructor
public enum ContentCheckState implements ICheckState {

    /**
     * 准备
     */
    PREPARE("准备", "1") {
        @Override
        public ContentCheckState nextState() {
            return PRE_CHECK;
        }
    },

    PRE_CHECK("预检测", "2") {
        @Override
        public ContentCheckState nextState() {
            return PARAGRAPH_CHECK;
        }
    },
    PARAGRAPH_CHECK("段落检测", "3") {
        @Override
        public ContentCheckState nextState() {
            return COLLECT_RESULT;
        }
    },
    COLLECT_RESULT("收集结果", "4") {
        @Override
        public ContentCheckState nextState() {
            return FINISH;
        }
    },
    FINISH("完成", "5") {
        @Override
        public ContentCheckState nextState() {
            return null;
        }
    },
    CANCEL("取消", "6") {
        @Override
        public ContentCheckState nextState() {
            return null;
        }
    };

    private final String name;

    private final String value;

    public abstract ContentCheckState nextState();
}
