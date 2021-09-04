package com.test.captcha.service.mapper;

import com.test.captcha.domain.*;
import com.test.captcha.service.dto.ShopReviewDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ShopReview} and its DTO {@link ShopReviewDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserExtraMapper.class, ShopMapper.class })
public interface ShopReviewMapper extends EntityMapper<ShopReviewDTO, ShopReview> {
    @Mapping(target = "reviewer", source = "reviewer", qualifiedByName = "id")
    @Mapping(target = "shop", source = "shop", qualifiedByName = "id")
    ShopReviewDTO toDto(ShopReview s);
}
