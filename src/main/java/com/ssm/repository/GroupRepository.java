package com.ssm.repository;

import com.ssm.entity.User;
import com.ssm.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface GroupRepository extends JpaRepository<UserGroup, Long> {
    Optional<UserGroup> findByGroupName(String groupName);

    @Query("SELECT g FROM UserGroup g WHERE g.admin = :user OR :user MEMBER OF g.users")
    List<UserGroup> findByAdminOrUsers(@Param("user") User user);

    List<UserGroup> findByAdmin(User user);
}