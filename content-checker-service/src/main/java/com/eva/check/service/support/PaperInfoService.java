package com.eva.check.service.support;


import com.baomidou.mybatisplus.extension.service.IService;
import com.eva.check.pojo.PaperInfo;

/**
* @author zzz
* @description 针对表【paper_info(论文信息)】的数据库操作Service
* @createDate 2023-10-27 14:41:09
*/
public interface PaperInfoService extends IService<PaperInfo> {
    PaperInfo getByPaperNo(String paperNo);

    int removeByPaperNo(String paperNo);

    PaperInfo getByParagraphId(Long paragraphId);


}
