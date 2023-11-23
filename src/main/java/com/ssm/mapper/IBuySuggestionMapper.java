package com.ssm.mapper;

import com.ssm.dto.BuySuggestionDTO;
import com.ssm.entity.BuySuggestion;
import com.ssm.entity.UserGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IBuySuggestionMapper {
    @Mapping(target = "userGroup", source = "userGroup")
    @Mapping(target = "id", ignore = true) // Ignore mapping for the ID field
    BuySuggestion toBuySuggestionEntity(UserGroup userGroup, BuySuggestionDTO buySuggestionDTO);
}