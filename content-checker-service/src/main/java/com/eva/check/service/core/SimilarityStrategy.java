package com.eva.check.service.core;

import com.eva.check.pojo.CheckTask;

import java.util.List;

/**
 * 相似度服务
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
public interface SimilarityStrategy {

    /**
     * 计算最终相似度
     *
     * @param checkTaskList 所有检测任务列表
     * @return Double
     */
    Double computeFinalSimilarity(List<CheckTask> checkTaskList);

}
