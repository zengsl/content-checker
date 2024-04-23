package com.eva.check.service.support;


import com.baomidou.mybatisplus.extension.service.IService;
import com.eva.check.pojo.CheckReport;
import com.eva.check.pojo.CheckRequest;
import com.eva.check.pojo.vo.CheckReportContentDTO;

/**
 * @author zengsl
 * @description 针对表【check_report(检测报告)】的数据库操作Service
 * @createDate 2023-11-30 15:33:50
 */
public interface CheckReportService extends IService<CheckReport> {

    /**
     * 查找检测报告
     *
     * @param checkNo 检测编号
     * @return CheckReport
     */
    CheckReport getByCheckNo(String checkNo);

    String getReportContent(String checkNo);

    /**
     * 初始化检测报告
     *
     * @param checkNo 检测编号
     */
    void initCheckReport(String checkNo);

    /**
     * 初始化检测报告
     *
     * @param checkId 检测记录ID
     */
    void initCheckReport(Long checkId);

    /**
     * 构建检测报告内容
     *
     * @param checkRequest 检测请求对象
     * @return Map<String, Object>
     */
    CheckReportContentDTO buildCheckReportContent(CheckRequest checkRequest);

    /**
     * 获取检测报告内容
     *
     * @param checkNo 检测编号
     * @return Map<String, Object>
     */
    CheckReportContentDTO getCheckReportContent(String checkNo);
}
