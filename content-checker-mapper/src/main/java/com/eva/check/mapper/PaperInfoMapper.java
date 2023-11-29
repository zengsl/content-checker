package com.eva.check.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eva.check.pojo.PaperInfo;

/**
* @author zzz
* @description 针对表【paper_info(论文信息)】的数据库操作Mapper
* @createDate 2023-10-27 14:41:09
* @Entity com.eva.pojo.PaperInfo
*/
public interface PaperInfoMapper extends BaseMapper<PaperInfo> {


    PaperInfo getByParagraphId(Long paragraphId);
}




