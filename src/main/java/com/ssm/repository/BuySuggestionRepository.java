package com.ssm.repository;

import com.ssm.entity.BuySuggestion;
import com.ssm.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface BuySuggestionRepository extends JpaRepository<BuySuggestion, Long> {
    List<BuySuggestion> findByUserGroup(UserGroup userGroup);
}
