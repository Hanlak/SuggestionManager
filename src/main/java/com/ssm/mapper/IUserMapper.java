package com.ssm.mapper;

import com.ssm.dto.RegisterDTO;
import com.ssm.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IUserMapper {

    User fromRegisterDto(RegisterDTO registerDTO);

    @Mapping(target = "name", source = "fullName")
    @Mapping(target = "username", source = "userName")
    RegisterDTO toDto(User user);

}
