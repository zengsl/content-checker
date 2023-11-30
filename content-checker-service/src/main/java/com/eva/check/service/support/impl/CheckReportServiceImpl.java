package com.eva.check.service.support.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eva.check.mapper.CheckReportMapper;
import com.eva.check.pojo.CheckReport;
import com.eva.check.service.support.CheckReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author zengsl
* @description 针对表【check_report(检测报告)】的数据库操作Service实现
* @createDate 2023-11-30 15:33:50
*/
@Service
public class CheckReportServiceImpl extends ServiceImpl<CheckReportMapper, CheckReport>
    implements CheckReportService {

    @Transactional(readOnly = true)
    @Override
    public CheckReport getByCheckNo(String checkNo) {
        LambdaQueryWrapper<CheckReport> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CheckReport::getCheckNo, checkNo);
        queryWrapper.orderByDesc(CheckReport::getUpdateTime);
        List<CheckReport> checkReports = this.baseMapper.selectList(queryWrapper);
        return CollUtil.isEmpty(checkReports) ? null : checkReports.get(0);
    }

}




