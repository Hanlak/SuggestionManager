package com.ssm.repository;

import com.ssm.entity.Likes;
import com.ssm.entity.StockSuggestion;
import com.ssm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface LikesRepository extends JpaRepository<Likes, Long> {
    Optional<List<Likes>> findByUser(User user);

    Optional<Likes> findByUserAndSuggestion(User user, StockSuggestion suggestion);

    @Transactional
    @Modifying
    @Query("DELETE FROM Likes l WHERE l.suggestion.id = :suggestionId")
    void deleteBySuggestionId(Long suggestionId);
}
