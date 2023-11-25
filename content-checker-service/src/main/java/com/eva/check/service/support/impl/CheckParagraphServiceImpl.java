package com.eva.check.service.support.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eva.check.mapper.CheckParagraphMapper;
import com.eva.check.pojo.CheckParagraph;
import com.eva.check.service.support.CheckParagraphService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 针对表【check_paragraph(检测文本段落)】的数据库操作Service实现
 *
 * @author zzz
 * @date 2023-11-15 15:12:08
 */
@Service
public class CheckParagraphServiceImpl extends ServiceImpl<CheckParagraphMapper, CheckParagraph>
        implements CheckParagraphService {

    @Transactional(readOnly = true)

    @Override
    public List<CheckParagraph> getByTaskId(Long taskId) {
        LambdaQueryWrapper<CheckParagraph> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CheckParagraph::getTaskId, taskId);
        return this.getBaseMapper().selectList(queryWrapper);
    }
}




