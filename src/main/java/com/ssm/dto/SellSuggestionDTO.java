package com.ssm.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellSuggestionDTO {
    private String stockName;
    private String sellPriority;
    private String warnSellLevel;
    private String dangerSellLevel;
    private String pso;
}
