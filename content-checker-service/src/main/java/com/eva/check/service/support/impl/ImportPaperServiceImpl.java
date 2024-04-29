package com.eva.check.service.support.impl;


import cn.hutool.core.date.StopWatch;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eva.check.common.constant.ContentCheckConstant;
import com.eva.check.common.enums.ImportPageStatus;
import com.eva.check.mapper.ImportPaperMapper;
import com.eva.check.pojo.ImportPaper;
import com.eva.check.pojo.converter.ImportPaperConverter;
import com.eva.check.pojo.dto.PaperAddReq;
import com.eva.check.service.core.PaperCollectService;
import com.eva.check.service.support.ImportPaperService;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

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

    private final PaperCollectService paperCollectService;


    private final TransactionTemplate transactionTemplate;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchImportPaper(int batchSize) {
        StopWatch stopWatch = new StopWatch("【ImportPaperService#importPaper】方法");
        stopWatch.start("执行录入操作");
        List<Long> importPaperIds = this.loadImportDataId(batchSize);
        if (CollectionUtils.isEmpty(importPaperIds)) {
            log.info("【ImportPaperService#importPage】方法执行结束，数据为空");
            return;
        }
        List<ImportPaper> importPapers = executeImportPaper(importPaperIds);
        stopWatch.stop();
        stopWatch.start("执行批量回写import_paper操作");
        // 执行批量插入操作
        this.updateBatchById(importPapers);
        stopWatch.stop();
        log.info("【ImportPaperService#importPage】方法执行结束  数据总量:{} ，耗时：{}s ，详情：{}", importPaperIds.size(), stopWatch.getTotalTimeSeconds(), stopWatch.prettyPrint());
    }

    private List<ImportPaper> executeImportPaper(List<Long> importPapers) {
        List<ImportPaper> importPaperResultList = Lists.newArrayList();
        for (Long importPaperId : importPapers) {
            ImportPaper importPaper = this.getById(importPaperId);
            PaperAddReq addReq = ImportPaperConverter.INSTANCE.toAddReq(importPaper);
            ImportPaper result = new ImportPaper();
            result.setImportId(importPaperId);
            try {
                this.paperCollectService.addNewPaper(addReq);
                result.setStatus(ImportPageStatus.DONE.getValue());
            } catch (Exception e) {
                log.error("导入论文失败, importId:{}", importPaper.getImportId(), e);
                result.setStatus(ImportPageStatus.FAIL.getValue());
                result.setMsg(StringUtils.substring(e.getMessage(), 0, 2000));
            }

            importPaperResultList.add(result);
        }
        return importPaperResultList;
    }

    @Deprecated
    /*@Async*/
    @Override
    public void importAllPaper() {
        StopWatch stopWatch = new StopWatch("【ImportPaperService#importAllPaper】方法");
        stopWatch.start("循环执行录入操作");
        int batchSize = ContentCheckConstant.IMPORT_BATCH_SIZE;
        int count = 0;
        // 查找数据
        List<Long> importPaperIds = this.loadImportDataId(batchSize);
        while (importPaperIds != null && !importPaperIds.isEmpty()) {
            StopWatch stopWatch2 = new StopWatch("每批次执行导入");
            stopWatch2.start("执行入库， 回写导入状态");
            log.info("importPaperIds.size() = {}", importPaperIds.size());
            count += importPaperIds.size();
            List<Long> finalImportPapers = importPaperIds;
            transactionTemplate.executeWithoutResult((status) -> {
                try {
                    // 执行导入操作
                    List<ImportPaper> importPapers = executeImportPaper(finalImportPapers);
                    // 执行批量插入操作
                    this.updateBatchById(importPapers);
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.error("导入异常", e);
                }
            });
            stopWatch2.stop();
            stopWatch2.start("再次获取一批新数据");
            // 再次查找数据，看是否有剩余需要导入的数据。
            importPaperIds = this.loadImportDataId(batchSize);
            stopWatch2.stop();
            log.info("【每批次执行导入】执行结束  该批次数据总量:{} ，耗时：{}s ，详情：{}", importPaperIds.size(), stopWatch2.getTotalTimeSeconds(), stopWatch2.prettyPrint());
        }
        stopWatch.stop();
        log.info("【ImportPaperService#importAllPaper】方法执行结束  数据总量:{} ，耗时：{}s ，详情：{}", count, stopWatch.getTotalTimeSeconds(), stopWatch.prettyPrint());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ImportPaper> loadImportData(int batchSize) {
        // 设置查询的数量
        LambdaQueryWrapper<ImportPaper> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImportPaper::getStatus, ImportPageStatus.INIT.getValue());
        // 安装更新时间排序，每次导入之后无论失败与否，都更新更新时间。防止重新补偿推送时总是优先执行那些大概率失败的数据
        queryWrapper.orderByAsc(ImportPaper::getUpdateTime);
        queryWrapper.last("limit " + batchSize);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<Long> loadImportDataId(int batchSize) {
        // 设置查询的数量
        LambdaQueryWrapper<ImportPaper> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ImportPaper::getStatus, ImportPageStatus.INIT.getValue());
        queryWrapper.select(ImportPaper::getImportId);
        // 安装更新时间排序，每次导入之后无论失败与否，都更新更新时间。防止重新补偿推送时总是优先执行那些大概率失败的数据
        queryWrapper.orderByAsc(ImportPaper::getUpdateTime);
        queryWrapper.last("limit " + batchSize);
        return this.baseMapper.selectObjs(queryWrapper);
    }
}




