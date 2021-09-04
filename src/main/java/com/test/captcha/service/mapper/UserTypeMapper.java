package com.test.captcha.service.mapper;

import com.test.captcha.domain.*;
import com.test.captcha.service.dto.UserTypeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserType} and its DTO {@link UserTypeDTO}.
 */
@Mapper(componentModel = "spring", uses = {})
public interface UserTypeMapper extends EntityMapper<UserTypeDTO, UserType> {
    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserTypeDTO toDtoId(UserType userType);
}
