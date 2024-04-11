package com.eva.check.service.flow;

/*执行流程
 *
 * 1. CheckTaskFlow执行流程
 * 2. ContentBaseContentCheckTaskFlow内容检测任务具体的执行流程
 *
 * CheckTaskFlow会针对不同的checkTask类型，执行不同的流程。CheckTask主要状态：运行中、结束、取消，具体查重计算流程中的过程不做规定，可以由具体的流程中自行根据需要处理。
 *
 * 目前支持内容检测流程为ContentBaseContentCheckTaskFlow
 *
 * 由内部绑定的MQ监听者决定具体的处理对象,MQ分为2种：Guava EventBus、RocketMQ。详情可见mq包
 * */