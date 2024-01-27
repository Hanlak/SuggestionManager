package com.ssm.repository;

import com.ssm.entity.SellSuggestion;
import com.ssm.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface SellSuggestionRepository extends JpaRepository<SellSuggestion, Long> {
    List<SellSuggestion> findByUserGroup(UserGroup userGroup);

}
