package com.ssm.mapper;

import com.ssm.dto.UserGroupDTO;
import com.ssm.entity.UserGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IUserGroupMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "admin", ignore = true)
    @Mapping(target = "users", ignore = true)
    UserGroup fromUserGroupDTO(UserGroupDTO userGroupDTO);

    UserGroupDTO fromUserGroup(UserGroup userGroup);
}
