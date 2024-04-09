package com.eva.check.service.support;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eva.check.pojo.CheckPaper;
import com.eva.check.pojo.vo.SimilarPaperVO;

import java.util.List;

/**
* @author zengsl
* @description 针对表【check_paper(检测论文)】的数据库操作Service
* @createDate 2024-04-09 09:22:10
*/
public interface CheckPaperService extends IService<CheckPaper> {

    void updateSimilarity(Long paperId, Double similarity);
     List<SimilarPaperVO> getAllSimilarPaper(Long checkPaperId);
}
