package com.test.captcha.service.mapper;

import com.test.captcha.domain.*;
import com.test.captcha.service.dto.ItemReviewDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ItemReview} and its DTO {@link ItemReviewDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserExtraMapper.class, ItemMapper.class })
public interface ItemReviewMapper extends EntityMapper<ItemReviewDTO, ItemReview> {
    @Mapping(target = "reviewer", source = "reviewer", qualifiedByName = "id")
    @Mapping(target = "item", source = "item", qualifiedByName = "id")
    ItemReviewDTO toDto(ItemReview s);
}
