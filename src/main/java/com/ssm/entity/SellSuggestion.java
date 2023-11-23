package com.ssm.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("SELL")
@Getter
@Setter
@NoArgsConstructor
public class SellSuggestion extends StockSuggestion {

    @Column(name = "sell_priority", nullable = false)
    private String sellPriority;

    @Column(name = "warn_sell_level", nullable = false)
    private String warnSellLevel;

    @Column(name = "danger_sell_level", nullable = false)
    private String dangerSellLevel;

    @Column(name = "pso")  // Add PSO to SellSuggestion
    private String pso;
}
