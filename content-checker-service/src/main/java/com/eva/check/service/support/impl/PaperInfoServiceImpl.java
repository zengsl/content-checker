package com.eva.check.service.support.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eva.check.mapper.PaperInfoMapper;
import com.eva.check.pojo.PaperInfo;
import com.eva.check.service.support.PaperInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author zzz
* @description 针对表【paper_info(论文信息)】的数据库操作Service实现
* @createDate 2023-10-27 14:41:09
*/
@Service
public class PaperInfoServiceImpl extends ServiceImpl<PaperInfoMapper, PaperInfo>
    implements PaperInfoService {

    @Transactional(readOnly = true)
    @Override
    public PaperInfo getByPaperNo(String paperNo) {
        LambdaQueryWrapper<PaperInfo> query = new LambdaQueryWrapper<>();
        query.eq(PaperInfo::getPaperNo, paperNo);
        return this.getBaseMapper().selectOne(query);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int removeByPaperNo(String paperNo) {
        LambdaUpdateWrapper<PaperInfo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(PaperInfo::getPaperNo, paperNo);
        return this.baseMapper.delete(updateWrapper) ;
    }

    @Transactional(readOnly = true)
    @Override
    public PaperInfo getByParagraphId(Long paragraphId) {
        return this.baseMapper.getByParagraphId(paragraphId);
    }
}




