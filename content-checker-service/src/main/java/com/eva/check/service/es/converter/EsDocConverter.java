package com.eva.check.service.es.converter;


import com.eva.check.pojo.PaperParagraph;
import com.eva.check.service.es.entity.PaperParagraphDoc;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 *
 *
 * 
 * @param null
 * @return 
 * @throws 
 * @author zzz
 * @date 2023/11/25 16:12
 */
@Mapper
public interface EsDocConverter {

    EsDocConverter INSTANCE = Mappers.getMapper(EsDocConverter.class);

    PaperParagraphDoc paperParagraph2Doc(PaperParagraph paperParagraph);
}
