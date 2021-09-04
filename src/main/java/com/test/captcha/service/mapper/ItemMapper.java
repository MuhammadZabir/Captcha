package com.test.captcha.service.mapper;

import com.test.captcha.domain.*;
import com.test.captcha.service.dto.ItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Item} and its DTO {@link ItemDTO}.
 */
@Mapper(componentModel = "spring", uses = { ShopMapper.class })
public interface ItemMapper extends EntityMapper<ItemDTO, Item> {
    @Mapping(target = "shop", source = "shop", qualifiedByName = "id")
    ItemDTO toDto(Item s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ItemDTO toDtoId(Item item);
}
