package com.ssm.mapper;

import com.ssm.dto.LoginDTO;
import com.ssm.dto.RegisterDTO;
import com.ssm.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IUserMapper {

    User fromRegisterDto(RegisterDTO registerDTO);
    RegisterDTO toDto(User user);

}
