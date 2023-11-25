package com.eva.check.service.support;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eva.check.pojo.CheckRequest;

/**
 * 针对表【check_request(论文信息)】的数据库操作Service
 *
 * @author zzz
 * @date 2023-11-13 12:11:00
 */
public interface CheckRequestService extends IService<CheckRequest> {

    /**
     * 结束任务
     *
     * @param checkId 任务ID
     */
    void finish(Long checkId);

    /**
     * 结束并汇总任务结果
     *
     * @param checkId 任务ID
     */
    void finishAndCollectResult(Long checkId);

    CheckRequest getByCheckNo(String checkNo);
}
