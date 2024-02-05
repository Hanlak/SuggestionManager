package com.ssm.repository;

import com.ssm.entity.GroupRequest;
import com.ssm.entity.User;
import com.ssm.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface GroupRequestRepository extends JpaRepository<GroupRequest, Long> {
    Optional<GroupRequest> findByUser(User user);
    Optional<GroupRequest> findByUserAndUserGroup(User user,UserGroup userGroup);

    void deleteByUserGroup(UserGroup userGroup);
    void deleteByUser(User user);

    List<GroupRequest> findByUserGroupAndStatus(UserGroup userGroup, GroupRequest.RequestStatus status);

    @Modifying
    @Query("update GroupRequest SET status = :status WHERE id = :id")
    int changeStatusToPending(@Param("status") GroupRequest.RequestStatus status, @Param("id") Long id);

}