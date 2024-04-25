package com.eva.check.service.support.impl;


import cn.hutool.core.date.StopWatch;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eva.check.common.enums.ImportPageStatus;
import com.eva.check.mapper.ImportPaperMapper;
import com.eva.check.pojo.ImportPaper;
import com.eva.check.pojo.converter.ImportPaperConverter;
import com.eva.check.pojo.dto.PaperAddReq;
import com.eva.check.service.core.PaperCollectService;
import com.eva.check.service.support.ImportPaperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 针对表【import_paper(导入论文表)】的数据库操作Service实现
 *
 * @author zengsl
 * @date 2024-04-25 11:33:41
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ImportPaperServiceImpl extends ServiceImpl<ImportPaperMapper, ImportPaper>
        implements ImportPaperService {

    public static final Integer BATCH_SIZE = 10000;

    private final PaperCollectService paperCollectService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void importPaper() {
        StopWatch stopWatch = new StopWatch("【ImportPaperService#importPaper】方法");
        stopWatch.start("执行录入操作");
        List<ImportPaper> importPapers = this.loadImportData();
        if (CollectionUtils.isEmpty(importPapers)) {
            log.info("【ImportPaperService#importPage】方法执行结束，数据为空");
            return;
        }
        for (ImportPaper importPaper : importPapers) {
            PaperAddReq addReq = ImportPaperConverter.INSTANCE.toAddReq(importPaper);
            try {
                this.paperCollectService.addNewPaper(addReq);
                importPaper.setStatus(ImportPageStatus.DONE.getValue());
            } catch (Exception e) {
                log.error("导入论文失败, importId:{}", importPaper.getImportId(), e);
                importPaper.setStatus(ImportPageStatus.FAIL.getValue());
                importPaper.setMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            }
        }
        stopWatch.stop();
        stopWatch.start("执行批量回写import_paper操作");
        // 执行批量插入操作
        this.updateBatchById(importPapers);
        stopWatch.stop();
        log.info("【ImportPaperService#importPage】方法执行结束  数据总量:{} ，耗时：{}s ，详情：{}", importPapers.size(), stopWatch.getTotalTimeSeconds(), stopWatch.prettyPrint());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ImportPaper> loadImportData() {
        // 设置查询的数量
        Page<ImportPaper> page = new Page<>(1, BATCH_SIZE);
        LambdaQueryWrapper<ImportPaper> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImportPaper::getStatus, ImportPageStatus.INIT.getValue());
        // 安装更新时间排序，每次导入之后无论失败与否，都更新更新时间。防止重新补偿推送时总是优先执行那些大概率失败的数据
        queryWrapper.orderByAsc(ImportPaper::getUpdateTime);
        return this.baseMapper.selectList(page, queryWrapper);
    }
}




