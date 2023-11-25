package com.eva.check.service.support;


import com.baomidou.mybatisplus.extension.service.IService;
import com.eva.check.pojo.PaperExt;

/**
 * 针对表【paper_ext(论文扩展信息)】的数据库操作Service
 *
 * @author zzz
 * @date 2023-10-27 14:41:09
 */
public interface PaperExtService extends IService<PaperExt> {

    /**
     * 根据paperNo删除论文扩展信息
     *
     * @param paperNo 论文编号
     * @return 删除成功记录数量
     */
    int removeByPaperNo(String paperNo);
}
