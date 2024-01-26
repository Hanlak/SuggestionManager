package com.ssm.repository;

import com.ssm.entity.Comment;
import com.ssm.entity.StockSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByStockSuggestion(StockSuggestion suggestion);
}
