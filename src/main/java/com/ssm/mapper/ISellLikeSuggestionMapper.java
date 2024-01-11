package com.ssm.mapper;

import com.ssm.dto.SellLikeSuggestionDTO;
import com.ssm.entity.SellSuggestion;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ISellLikeSuggestionMapper {

    SellLikeSuggestionDTO fromSellSuggestionAndChoice(SellSuggestion sellSuggestion, boolean likeState,long id);
}
