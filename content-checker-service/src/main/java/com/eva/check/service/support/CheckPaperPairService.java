package com.eva.check.service.support;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eva.check.pojo.CheckPaperPair;
import com.eva.check.pojo.vo.SimilarPaperVO;

import java.util.List;

/**
 * @author zengsl
 * @description 针对表【check_paper_pair(验证论文对)】的数据库操作Service
 * @createDate 2024-04-09 09:22:10
 */
public interface CheckPaperPairService extends IService<CheckPaperPair> {
    void initCompareList(List<CheckPaperPair> checkPaperListPairs);

    List<CheckPaperPair> getByCheckPaperIdWithOrder(Long checkPaperId);

    List<SimilarPaperVO> getSimilarityPapers(Long checkPaperId);

}
