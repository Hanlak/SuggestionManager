package com.ssm.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "stock_suggestions")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "suggestion_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockSuggestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private UserGroup userGroup;

    @Column(name = "stock_name", nullable = false)
    private String stockName;

    @UpdateTimestamp
    @Temporal(TemporalType.DATE)
    @Column(name = "lastUpdated", nullable = false)
    private Date lastUpdated;

}
