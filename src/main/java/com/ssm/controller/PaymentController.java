package com.ssm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/payToJoin")
public class PaymentController {

    @PostMapping("/payment")
    public String subscribeToJoinGroup(@RequestParam("groupName") String groupName) {
        //update payment status table and then send request
        return "redirect:/groups/sendRequest/" + groupName;
    }
}
