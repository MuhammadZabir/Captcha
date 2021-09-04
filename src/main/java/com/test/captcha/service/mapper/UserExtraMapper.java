package com.test.captcha.service.mapper;

import com.test.captcha.domain.*;
import com.test.captcha.service.dto.UserExtraDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserExtra} and its DTO {@link UserExtraDTO}.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class, UserTypeMapper.class })
public interface UserExtraMapper extends EntityMapper<UserExtraDTO, UserExtra> {
    @Mapping(target = "user", source = "user", qualifiedByName = "id")
    @Mapping(target = "userType", source = "userType", qualifiedByName = "id")
    UserExtraDTO toDto(UserExtra s);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserExtraDTO toDtoId(UserExtra userExtra);
}
