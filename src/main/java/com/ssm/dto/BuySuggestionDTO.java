package com.ssm.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuySuggestionDTO {
    private String stockName;
    private String buyPriority;
    private String minBuy;
    private String maxBuy;
    private String target;
    private String stopLoss;
    private String qbos;
}
