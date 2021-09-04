package com.test.captcha.service.mapper;

import com.test.captcha.domain.*;
import com.test.captcha.service.dto.CartBasketDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CartBasket} and its DTO {@link CartBasketDTO}.
 */
@Mapper(componentModel = "spring", uses = { CartMapper.class })
public interface CartBasketMapper extends EntityMapper<CartBasketDTO, CartBasket> {
    @Mapping(target = "cart", source = "cart", qualifiedByName = "id")
    CartBasketDTO toDto(CartBasket s);
}
