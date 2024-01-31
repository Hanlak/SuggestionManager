package com.ssm.repository;

import com.ssm.entity.Payment;
import com.ssm.entity.User;
import com.ssm.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByUserAndUserGroup(User user, UserGroup userGroup);

    @Modifying
    @Query("UPDATE Payment p SET p.subscriptionStatus = :subscriptionStatus WHERE p.userGroup = :userGroup")
    void updateSubscriptionStatusByUserGroup(
            @Param("userGroup") UserGroup userGroup,
            @Param("subscriptionStatus") Payment.SubscriptionStatus subscriptionStatus
    );

    @Modifying
    @Query("UPDATE Payment p SET p.subscriptionStatus = :subscriptionStatus WHERE p.userGroup = :userGroup AND p.user = :user")
    void updateSubscriptionStatusByUserGroupAndUser(
            @Param("userGroup") UserGroup userGroup,
            @Param("user") User user,
            @Param("subscriptionStatus") Payment.SubscriptionStatus subscriptionStatus
    );

}
