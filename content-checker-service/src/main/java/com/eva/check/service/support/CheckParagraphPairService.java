package com.eva.check.service.support;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eva.check.pojo.CheckParagraphPair;

import java.util.List;

/**
* @author zzz
* @description 针对表【check_paragraph_list(验证段落列表)】的数据库操作Service
* @createDate 2023-11-15 16:42:04
*/
public interface CheckParagraphPairService extends IService<CheckParagraphPair> {
    void initCompareList(List<CheckParagraphPair> checkParagraphListPairs);

    List<CheckParagraphPair> getByTaskId(Long taskId);
}
