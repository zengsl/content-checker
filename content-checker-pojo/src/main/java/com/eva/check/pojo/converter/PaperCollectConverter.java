package com.eva.check.pojo.converter;

import com.eva.check.pojo.PaperExt;
import com.eva.check.pojo.PaperInfo;
import com.eva.check.pojo.dto.PaperAddReq;
import com.eva.check.pojo.dto.PaperCheckReq;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * 论文收集转换器
 *
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Mapper(builder = @Builder(disableBuilder = true))
public interface PaperCollectConverter {
    PaperCollectConverter INSTANCE = Mappers.getMapper(PaperCollectConverter.class);

    @Mapping(target = "paperId", ignore = true)
    @Mapping(target = "hash", ignore = true)
    @Mapping(target = "hash1", ignore = true)
    @Mapping(target = "hash2", ignore = true)
    @Mapping(target = "hash3", ignore = true)
    @Mapping(target = "hash4", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "wordCount", ignore = true)
    @Mapping(target = "paraCount", ignore = true)
    PaperInfo paperAddReq2Info(PaperAddReq paperAddReq);

    @Mapping(target = "paperId", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    PaperExt paperAddReq2Ext(PaperAddReq.PaperExtDto paperExtDto);

    List<PaperExt> paperAddReq2Ext(List<PaperAddReq.PaperExtDto> paperExtDtoList);


    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    PaperExt paperAddReq2Ext(PaperAddReq.PaperExtDto paperExtDto, Long paperId);

    default List<PaperExt> paperAddReq2Ext(List<PaperAddReq.PaperExtDto> paperExtDtoList, Long paperId) {
        if (paperExtDtoList == null) {
            return null;
        }
        List<PaperExt> list = new ArrayList<>(paperExtDtoList.size());
        for (PaperAddReq.PaperExtDto paperExtDto : paperExtDtoList) {
            list.add(paperAddReq2Ext(paperExtDto, paperId));
        }
        return list;
    }

    PaperAddReq check2AddReq(PaperCheckReq paperCheckReq);

}
