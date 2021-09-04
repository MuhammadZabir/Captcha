package com.test.captcha.service.mapper;

import com.test.captcha.domain.*;
import com.test.captcha.service.dto.ItemStatusDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ItemStatus} and its DTO {@link ItemStatusDTO}.
 */
@Mapper(componentModel = "spring", uses = { ItemMapper.class })
public interface ItemStatusMapper extends EntityMapper<ItemStatusDTO, ItemStatus> {
    @Mapping(target = "item", source = "item", qualifiedByName = "id")
    ItemStatusDTO toDto(ItemStatus s);
}
