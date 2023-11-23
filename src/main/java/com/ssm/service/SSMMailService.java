package com.ssm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SSMMailService {
    @Autowired
    JavaMailSender ssmMailSender;

    public String toSendEMail(String toAddress, String fromAddress, String subject, String msgBody) {

        SimpleMailMessage endgameMsg = new SimpleMailMessage();
        endgameMsg.setFrom(fromAddress);
        endgameMsg.setTo(toAddress);
        endgameMsg.setSubject(subject);
        endgameMsg.setText(msgBody);
        ssmMailSender.send(endgameMsg);
        return "OK";
    }

}
