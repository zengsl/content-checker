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
     * 导入论文
     */
    void importPaper();

    /**
     * 载入待导入论文数据
     *
     * @return List<ImportPaper>
     */
    List<ImportPaper> loadImportData();
}
