package com.ssm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuyLikeSuggestionDTO extends BuySuggestionDTO {
    private long id;
    private boolean likeState;
}
