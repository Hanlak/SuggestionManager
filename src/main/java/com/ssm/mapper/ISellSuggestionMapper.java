package com.ssm.mapper;

import com.ssm.dto.SellSuggestionDTO;
import com.ssm.entity.SellSuggestion;
import com.ssm.entity.UserGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ISellSuggestionMapper {
    @Mapping(target = "userGroup", source = "userGroup")
    @Mapping(target = "id", ignore = true) // Ignore mapping for the ID field
    SellSuggestion toSellSuggestionEntity(UserGroup userGroup, SellSuggestionDTO sellSuggestionDTO);
}