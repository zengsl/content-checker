package com.eva.check.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eva.check.pojo.PaperExt;

/**
* @author zzz
* @description 针对表【paper_ext(论文扩展信息)】的数据库操作Mapper
* @createDate 2023-10-27 14:41:09
* @Entity com.eva.pojo.PaperExt
*/
public interface PaperExtMapper extends BaseMapper<PaperExt> {

    int removeByPageNo(String paperNo);
}




