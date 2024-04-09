package com.eva.check.service.support.impl;


import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eva.check.common.enums.TextColor;
import com.eva.check.mapper.CheckPaperMapper;
import com.eva.check.pojo.CheckPaper;
import com.eva.check.pojo.CheckPaperPair;
import com.eva.check.pojo.vo.SimilarPaperVO;
import com.eva.check.service.core.SimilarTextRule;
import com.eva.check.service.support.CheckPaperPairService;
import com.eva.check.service.support.CheckPaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zengsl
 * @description 针对表【check_paper(检测论文)】的数据库操作Service实现
 * @createDate 2024-04-09 09:22:10
 */
@Service
@RequiredArgsConstructor
public class CheckPaperServiceImpl extends ServiceImpl<CheckPaperMapper, CheckPaper>
        implements CheckPaperService {

    private final CheckPaperPairService checkPaperPairService;
    private final SimilarTextRule similarTextRule;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateSimilarity(Long paperId, Double similarity) {
        CheckPaper checkPaper = CheckPaper.builder()
                .paperId(paperId).similarity(similarity)
                .build();
        this.getBaseMapper().updateById(checkPaper);
    }

    @Transactional(readOnly = true)
    @Override
    public List<SimilarPaperVO> getAllSimilarPaper(Long checkPaperId) {
//        List<CheckPaperPair> checkPaperPairList = this.checkPaperPairService.getByCheckPaperIdWithOrder(checkPaperId);
        List<SimilarPaperVO> similarityPapers = this.checkPaperPairService.getSimilarityPapers(checkPaperId);
        similarityPapers.forEach(e->{
            TextColor textColor = this.similarTextRule.computeTextColor(e.getSimilarity());
            e.setCssClassName(textColor.getCssClass());
            e.setFormatSimilarity(NumberUtil.decimalFormat("#.##%", e.getSimilarity()));
        });
        return similarityPapers;
    }
}




