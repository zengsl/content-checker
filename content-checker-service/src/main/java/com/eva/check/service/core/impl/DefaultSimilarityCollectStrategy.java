package com.eva.check.service.core.impl;


import com.eva.check.common.util.SimilarUtil;
import com.eva.check.pojo.CheckTask;
import com.eva.check.service.core.SimilarityCollectStrategy;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * 默认相似度计算策略
 *
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@RequiredArgsConstructor
public class DefaultSimilarityCollectStrategy implements SimilarityCollectStrategy {

    @Override
    public Double computeFinalSimilarity(List<CheckTask> checkTaskList) {
        // 目前只有一个内容检查任务，所以就不算平均数。日后可根据实际情况进行加权平均数计算最终相似度
        return SimilarUtil.formatSimilarity(checkTaskList.stream().mapToDouble(CheckTask::getSimilarity).sum());
    }
}
