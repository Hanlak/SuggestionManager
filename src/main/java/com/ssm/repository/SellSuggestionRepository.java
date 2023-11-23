package com.ssm.repository;

import com.ssm.entity.SellSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface SellSuggestionRepository extends JpaRepository<SellSuggestion, Long> {
}
