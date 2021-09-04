package com.test.captcha.service.mapper;

import com.test.captcha.domain.*;
import com.test.captcha.service.dto.PurchaseHistoryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PurchaseHistory} and its DTO {@link PurchaseHistoryDTO}.
 */
@Mapper(componentModel = "spring", uses = { CartMapper.class, UserExtraMapper.class })
public interface PurchaseHistoryMapper extends EntityMapper<PurchaseHistoryDTO, PurchaseHistory> {
    @Mapping(target = "cart", source = "cart", qualifiedByName = "id")
    @Mapping(target = "buyer", source = "buyer", qualifiedByName = "id")
    PurchaseHistoryDTO toDto(PurchaseHistory s);
}
