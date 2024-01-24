package com.ssm.service;

import com.ssm.entity.Payment;
import com.ssm.entity.User;
import com.ssm.entity.UserGroup;
import com.ssm.exception.GroupNotFoundException;
import com.ssm.exception.UserNotFoundException;
import com.ssm.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PaymentService {
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    UserService userService;

    @Autowired
    GroupService groupService;

    public PaymentService(PaymentRepository paymentRepository, UserService userService) {
        this.paymentRepository = paymentRepository;
        this.userService = userService;
    }

    public void updatePayment(String groupName, String username) throws UserNotFoundException, GroupNotFoundException, DataAccessException {
        User user = userService.getUserByUsername(username);
        UserGroup userGroup = groupService.getGroupByGroupName(groupName);
        paymentRepository.save(mapPayment(user, userGroup));
    }

    private Payment mapPayment(User user, UserGroup userGroup) {
        Payment payment = new Payment();
        payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
        payment.setUser(user);
        payment.setUserGroup(userGroup);
        payment.setSubscriptionStatus(Payment.SubscriptionStatus.ACTIVE);
        payment.setPaymentDate(LocalDate.now());
        return payment;
    }
}