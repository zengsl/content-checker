package com.eva.check.service.core;

import com.eva.check.common.exception.SystemException;
import com.eva.check.pojo.CheckRequest;
import com.eva.check.pojo.dto.PaperCheckReq;

/**
 * 论文检测服务
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
public interface PaperCheckService {

    /**
     * 发起校验申请
     *
     * @param paperCheckReq 论文添加请求
     * @return 校验号
     * @throws SystemException 系统异常
     */
    String createPaperCheck(PaperCheckReq paperCheckReq) throws SystemException;

    /**
     * 发起校验申请并且收集
     *
     * @param paperCheckReq 论文添加请求
     * @return 校验号
     * @throws SystemException 系统异常
     */
    String createPaperCheckAndCollect(PaperCheckReq paperCheckReq) throws SystemException;

    /**
     *
     * 根据校验号获取校验结果
     *
     * @param checkNo 检测编号
     * @return CheckRequest
     */
    CheckRequest getPaperCheckResult(String checkNo) throws SystemException;

    /**
     * 获取检测报告
     *
     * @param checkNo  检测编号
     */
    String getPaperCheckReport(String checkNo) throws SystemException;

}
