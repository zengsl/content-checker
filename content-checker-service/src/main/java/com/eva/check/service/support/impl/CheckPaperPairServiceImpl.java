package com.eva.check.service.support.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eva.check.mapper.CheckPaperPairMapper;
import com.eva.check.pojo.CheckPaperPair;
import com.eva.check.pojo.vo.SimilarPaperVO;
import com.eva.check.service.support.CheckPaperPairService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author zengsl
* @description 针对表【check_paper_pair(验证论文对)】的数据库操作Service实现
* @createDate 2024-04-09 09:22:10
*/
@Service
public class CheckPaperPairServiceImpl extends ServiceImpl<CheckPaperPairMapper, CheckPaperPair>
    implements CheckPaperPairService {

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void initCompareList(List<CheckPaperPair> checkPaperListPairs) {
        this.saveBatch(checkPaperListPairs);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CheckPaperPair> getByCheckPaperIdWithOrder(Long checkPaperId) {
        if (checkPaperId != null) {
            return this.lambdaQuery().eq(CheckPaperPair::getCheckPaperId, checkPaperId).orderByDesc(CheckPaperPair::getSimilarity).list();
        }
        return List.of();
    }

    @Transactional(readOnly = true)
    @Override
    public List<SimilarPaperVO> getSimilarityPapers(Long checkPaperId) {
        return this.getBaseMapper().getSimilarityPapers(checkPaperId);
    }
}




