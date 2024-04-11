package com.eva.check.pojo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 消息队列检测任务
 *
 * @author zengsl
 * @date 2024/4/15 16:49
 */
@Data
public class MqCheckTask {

    /**
     * 验证任务主键
     */
    @TableId(type = IdType.AUTO)
    private Long taskId;

    /**
     * 验证请求主键
     */
    private Long checkId;

    /**
     * 验证标号
     */
    private String checkNo;

    /**
     * 论文编号
     */
    private String paperNo;

    /**
     * 论文主键
     */
    private Long paperId;

    /**
     * 验证类型: 1 内容 2 title
     */
    private String checkType;



    /**
     * 状态: 0 待处理,  1 处理中， 2 已完成 3 失败
     */
//    private String status;

    /**
     * 执行结果： 成功或者失败的详细信息
     */
//    private String result;

    /**
     * 相似度
     */
    private Double similarity;
}
