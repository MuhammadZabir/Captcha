package com.test.captcha.service.mapper;

import com.test.captcha.domain.*;
import com.test.captcha.service.dto.ImageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Image} and its DTO {@link ImageDTO}.
 */
@Mapper(componentModel = "spring", uses = { ItemMapper.class })
public interface ImageMapper extends EntityMapper<ImageDTO, Image> {
    @Mapping(target = "item", source = "item", qualifiedByName = "id")
    ImageDTO toDto(Image s);
}
