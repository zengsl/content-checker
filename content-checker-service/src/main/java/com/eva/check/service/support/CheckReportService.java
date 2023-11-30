package com.eva.check.service.support;


import com.baomidou.mybatisplus.extension.service.IService;
import com.eva.check.pojo.CheckReport;

/**
* @author zengsl
* @description 针对表【check_report(检测报告)】的数据库操作Service
* @createDate 2023-11-30 15:33:50
*/
public interface CheckReportService extends IService<CheckReport> {

    CheckReport getByCheckNo(String checkNo);

}
