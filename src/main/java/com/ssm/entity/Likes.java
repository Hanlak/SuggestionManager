package com.ssm.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "Likes")
@Getter
@Setter
@NoArgsConstructor
public class Likes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "suggestion_id", nullable = false)
    private StockSuggestion suggestion;

    @Column(name = "liked", nullable = false)
    private boolean liked;

    public Likes(User user, StockSuggestion suggestion, boolean liked) {
        this.user = user;
        this.suggestion = suggestion;
        this.liked = liked;
    }
}
