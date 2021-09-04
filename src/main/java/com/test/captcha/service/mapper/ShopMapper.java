package com.test.captcha.service.mapper;

import com.test.captcha.domain.*;
import com.test.captcha.service.dto.ShopDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Shop} and its DTO {@link ShopDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserExtraMapper.class })
public interface ShopMapper extends EntityMapper<ShopDTO, Shop> {
    @Mapping(target = "owner", source = "owner", qualifiedByName = "id")
    ShopDTO toDto(Shop s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ShopDTO toDtoId(Shop shop);
}
