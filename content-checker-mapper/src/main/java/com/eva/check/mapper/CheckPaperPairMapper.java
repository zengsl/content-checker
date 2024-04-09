package com.eva.check.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eva.check.pojo.CheckPaperPair;
import com.eva.check.pojo.vo.SimilarPaperVO;

import java.util.List;

/**
* @author zengsl
* @description 针对表【check_paper_pair(验证论文对)】的数据库操作Mapper
* @createDate 2024-04-09 09:22:10
* @Entity com.eva.pojo.CheckPaperPair
*/
public interface CheckPaperPairMapper extends BaseMapper<CheckPaperPair> {
    List<SimilarPaperVO> getSimilarityPapers(Long checkPaperId);
}




