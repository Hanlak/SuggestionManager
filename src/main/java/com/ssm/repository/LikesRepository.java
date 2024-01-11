package com.ssm.repository;

import com.ssm.entity.Likes;
import com.ssm.entity.StockSuggestion;
import com.ssm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface LikesRepository extends JpaRepository<Likes, Long> {
    List<Likes> findByUser(User user);

    Optional<Likes> findByUserAndSuggestion(User user, StockSuggestion suggestion);
}
