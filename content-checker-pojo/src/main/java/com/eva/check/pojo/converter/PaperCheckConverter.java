package com.eva.check.pojo.converter;

import com.eva.check.pojo.CheckPaper;
import com.eva.check.pojo.CheckRequest;
import com.eva.check.pojo.dto.PaperCheckReq;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 论文检测转化器
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Mapper
public interface PaperCheckConverter {

    PaperCheckConverter INSTANCE = Mappers.getMapper(PaperCheckConverter.class);


    /**
     * 论文请求DTO转为CheckRequest
     *
     * @param paperCheckReq 论文请求DTO
     * @return CheckRequest
     */
    @Mapping(target = "checkId", ignore = true)
    @Mapping(target = "taskNum", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "similarity", ignore = true)
    CheckRequest paperCheckReq2CheckReq(PaperCheckReq paperCheckReq);

    @Mapping(target = "paperId", ignore = true)
    @Mapping(target = "checkId", ignore = true)
    @Mapping(target = "similarity", ignore = true)
    CheckPaper paperCheckReq2CheckPaper(PaperCheckReq paperCheckReq);
}
