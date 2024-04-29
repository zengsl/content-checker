package com.eva.check.service.support;


import com.baomidou.mybatisplus.extension.service.IService;
import com.eva.check.pojo.ImportPaper;

import java.util.List;

/**
 * 针对表【import_paper(导入论文表)】的数据库操作Service
 *
 * @author zengsl
 * @date 2024-04-25 11:33:41
 */
public interface ImportPaperService extends IService<ImportPaper> {

    /**
     * 批量导入论文
     *
     * @param batchSize 批量插入数量
     */
    void batchImportPaper(int batchSize);

    /**
     * 导入所有论文
     */
    void importAllPaper();

    /**
     * 载入待导入论文数据
     *
     * @param batchSize 查询数量
     * @return List<ImportPaper>
     */
    List<ImportPaper> loadImportData(int batchSize);

    /**
     * 载入待导入论文数据Id集合
     *
     * @param batchSize 查询数量
     * @return List<Long>
     */
    List<Long> loadImportDataId(int batchSize);
}
