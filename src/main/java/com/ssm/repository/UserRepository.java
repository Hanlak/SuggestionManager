package com.ssm.repository;

import com.ssm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserName(String username);

    Optional<User> findByEmail(String email);

    @Modifying
    @Query("update User SET password = :password WHERE email = :email")
    int saveNewPasswordUsingEmail(@Param("password") String password, @Param("email") String email);
}