package com.eva.check.service.core;

import com.eva.check.pojo.dto.PaperAddReq;

/**
 * 论文收集服务
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
public interface PaperCollectService {

    /**
     * 新增论文
     */
    String addNewPaper(PaperAddReq paperAddReq);

    void removePaperByNo(String paperNo);

    void removePaperById(String paperId);


}
