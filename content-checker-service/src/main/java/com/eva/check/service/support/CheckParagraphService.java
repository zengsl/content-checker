package com.eva.check.service.support;

import com.baomidou.mybatisplus.extension.service.IService;
import com.eva.check.pojo.CheckParagraph;

import java.util.List;

/**
* @author zzz
* @description 针对表【check_paragraph(检测文本段落)】的数据库操作Service
* @createDate 2023-11-15 15:12:08
*/
public interface CheckParagraphService extends IService<CheckParagraph> {

    List<CheckParagraph> getByTaskId(Long taskId);

}
