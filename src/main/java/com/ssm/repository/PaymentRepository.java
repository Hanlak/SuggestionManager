package com.ssm.repository;

import com.ssm.entity.Payment;
import com.ssm.entity.User;
import com.ssm.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByUserAndUserGroup(User user, UserGroup userGroup);
}
