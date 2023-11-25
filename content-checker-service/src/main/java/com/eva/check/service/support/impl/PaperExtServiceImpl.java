package com.eva.check.service.support.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.eva.check.mapper.PaperExtMapper;
import com.eva.check.pojo.PaperExt;
import com.eva.check.service.support.PaperExtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
* @author zzz
* @description 针对表【paper_ext(论文扩展信息)】的数据库操作Service实现
* @createDate 2023-10-27 14:41:09
*/
@Service
public class PaperExtServiceImpl extends ServiceImpl<PaperExtMapper, PaperExt>
    implements PaperExtService {

    @Transactional(rollbackFor = Exception.class)

    @Override
    public int removeByPaperNo(String paperNo) {

        return this.baseMapper.removeByPageNo(paperNo);
    }

}




