package com.ssm.mapper;

import com.ssm.dto.BuyLikeSuggestionDTO;
import com.ssm.entity.BuySuggestion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IBuyLikeSuggestionMapper {

    BuyLikeSuggestionDTO fromBuySuggestionAndChoice(BuySuggestion buySuggestion, boolean likeState,long id);
}
