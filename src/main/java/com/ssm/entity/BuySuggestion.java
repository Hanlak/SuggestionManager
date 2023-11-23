package com.ssm.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("BUY")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BuySuggestion extends StockSuggestion {

    @Column(name = "buy_priority", nullable = false)
    private String buyPriority;

    @Column(name = "min_buy", nullable = false)
    private String minBuy;

    @Column(name = "max_buy", nullable = false)
    private String maxBuy;

    @Column(name = "stop_loss")
    private String stopLoss;

    @Column(name = "target")
    private String target;

    @Column(name = "qbos")  // Add QBOS to BuySuggestion
    private String qbos;
}

