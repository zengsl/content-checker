package com.eva.check.pojo.converter;

import com.eva.check.pojo.ImportPaper;
import com.eva.check.pojo.dto.PaperAddReq;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 
 *
 * 
 * @param null
 * @return 
 * @throws 
 * @author zengsl
 * @date 2024/4/25 14:52
 */
@Mapper
public interface ImportPaperConverter {

    ImportPaperConverter INSTANCE = Mappers.getMapper(ImportPaperConverter.class);

    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "paperExtList", ignore = true)
    @Mapping(target = "dataType", ignore = true)
    @Mapping(target = "dataSource", ignore = true)
    PaperAddReq toAddReq(ImportPaper importPaper);

    @Mapping(target = "importId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "msg", ignore = true)
    ImportPaper toImportReq(PaperAddReq paperAddReq);

}
