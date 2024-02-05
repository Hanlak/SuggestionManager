package com.ssm.service;

import com.ssm.entity.Payment;
import com.ssm.entity.User;
import com.ssm.entity.UserGroup;
import com.ssm.exception.*;
import com.ssm.repository.PaymentRepository;
import com.ssm.util.SSMUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PaymentService {
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    UserService userService;

    @Autowired
    GroupService groupService;

    public PaymentService(PaymentRepository paymentRepository, UserService userService, GroupService groupService) {
        this.paymentRepository = paymentRepository;
        this.userService = userService;
        this.groupService = groupService;
    }

    public void updatePayment(String groupName, String username) throws UserNotFoundException, GroupNotFoundException, DataAccessException {
        User user = userService.getUserByUsername(username);
        UserGroup userGroup = groupService.getGroupByGroupName(groupName);
        Payment payment = paymentRepository.findByUserAndUserGroup(user, userGroup).orElse(null);
        if (ObjectUtils.isEmpty(payment))
            paymentRepository.save(mapPayment(user, userGroup));
        else {
            payment.setSubscriptionStatus(Payment.SubscriptionStatus.ACTIVE);
            payment.setPaymentDate(LocalDate.now());
            paymentRepository.save(payment);
        }
    }

    public void makePayment(String groupName, String username) throws UserNotFoundException, GroupNotFoundException, PaymentNotFoundException, PaymentException {
        User user = userService.getUserByUsername(username);
        UserGroup userGroup = groupService.getGroupByGroupName(groupName);
        if (UserGroup.Subscription.PAID.equals(userGroup.getSubscription())) {
            Payment payment = paymentRepository.findByUserAndUserGroup(user, userGroup).orElseThrow(() -> new PaymentNotFoundException("Error While Activating Subscription.Payment Not Found:Contact Support"));
            if (SSMUtil.isWithin7DaysOfOneYearAfterPaymentDate(payment.getPaymentDate()) || Payment.SubscriptionStatus.EXPIRED.equals(payment.getSubscriptionStatus())) {
                payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
                payment.setSubscriptionStatus(Payment.SubscriptionStatus.ACTIVE);
                if (SSMUtil.isWithin7DaysOfOneYearAfterPaymentDate(payment.getPaymentDate())) {
                    payment.setPaymentDate(payment.getPaymentDate().plus(1, ChronoUnit.YEARS));
                } else {
                    payment.setPaymentDate(LocalDate.now());
                }
                paymentRepository.save(payment);
            } else {
                throw new PaymentException("The Subscription is still Active And if you unable to Access the group.Please Contact Support");
            }
        } else {
            throw new PaymentException("This is Not a Paid Group; You Can join by Sending Request");
        }
    }

    public List<String> getAllPaidNonAdminGroupsBasedOnUser(String username) throws UserNotFoundException {
        User user = userService.getUserByUsername(username);
        return groupService.getAllPaidNonAdminGroupsBasedOnUser(user);
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

    public String validatePaymentSubscription(UserGroup userGroup, String username) throws UserNotFoundException, PaymentNotFoundException, SubscriptionExpiredException, GroupNotFoundException {
        User user = userService.getUserByUsername(username);
        Payment payment = paymentRepository.findByUserAndUserGroup(user, userGroup)
                .orElseThrow(() -> new PaymentNotFoundException("Issue with Subscription: contact payment support"));
        LocalDate paymentDate = payment.getPaymentDate();
        if (SSMUtil.isExactly1Year3DaysAfterPaymentDate(paymentDate)) {
            //group removal initiate;
            try {
                groupService.removeMember(userGroup.getGroupName(), userGroup.getAdmin().getUserName(), username);
            } catch (Exception exception) {
                throw new PaymentNotFoundException("You Subscription Expired But Error occurred while removing from Group. Contact Support");
            }
            return "you have been removed from the group. please re join the group by making a payment";
        }
        if (Payment.SubscriptionStatus.EXPIRED.equals(payment.getSubscriptionStatus())) {
            throw new SubscriptionExpiredException("You Subscription has been expired.Please pay to gain access");
        }
        if (paymentDate == null) {
            throw new PaymentNotFoundException("Issue with Subscription: contact payment support");
        }
        if (SSMUtil.isPastOneYearAfterPaymentDate(paymentDate)) {
            throw new SubscriptionExpiredException("You Subscription has been expired.Please pay to gain access");
        }
        if (SSMUtil.isWithin7DaysOfOneYearAfterPaymentDate(paymentDate)) {
            long remainingDays = SSMUtil.daysBefore7DaysOfOneYearAnniversary(paymentDate);
            if (remainingDays == -1)
                throw new PaymentNotFoundException("Issue with Subscription: contact payment support{payment date:null");
            return "You Subscription gonna expire in " + remainingDays + " days. Please pay before its expired";
        }
        return Payment.SubscriptionStatus.ACTIVE.toString();
    }

}