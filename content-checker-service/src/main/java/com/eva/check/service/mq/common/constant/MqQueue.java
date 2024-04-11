package com.eva.check.service.mq.common.constant;

/**
 *
 * @author zengsl
 * @date 2024/4/15 10:09
 */
public interface MqQueue {
    String START_TASK_CONSUMER_GROUP = "start_check_task_consumer";
    String FINISH_TASK_CONSUMER_GROUP = "finish_check_task_consumer";
    String CANCEL_TASK_CONSUMER_GROUP = "cancel_check_task_consumer";
    String CONTENT_PRE_CHECK_CONSUMER_GROUP = "content_pre_check_consumer";
    String PARAGRAPH_CHECK_CONSUMER_GROUP = "paragraph_check_consumer";
    String COLLECT_RESULT_CONSUMER_GROUP = "collect_check_result_consumer";
    String GENERATE_REPORT_CONSUMER_GROUP = "generate_check_report_consumer";

    String CONTENT_CHECK_TOPIC = "Content_Check_Topic";
    String START_TASK_TAG = "start_check_task";
    String FINISH_TASK_TAG = "finish_check_task";
    String CANCEL_TASK_TAG = "cancel_check_task";
    String CONTENT_PRE_CHECK_TAG = "content_pre_check";
    String PARAGRAPH_CHECK_TAG = "paragraph_check";
    String COLLECT_RESULT_TAG = "collect_check_result";
    String GENERATE_REPORT_TAG = "generate_check_report";


}
