package com.ssm.controller;

import com.ssm.exception.GroupNotFoundException;
import com.ssm.exception.UserNotFoundException;
import com.ssm.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/groups")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payment")
    public String subscribeToJoinGroup(@RequestParam("groupName") String groupName, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            paymentService.updatePayment(groupName, principal.getName());
            return "redirect:/groups/sendRequest/" + groupName;
        } catch (UserNotFoundException | GroupNotFoundException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/groups/requestToJoinGroup";
        } catch (DataAccessException dataAccessException) {
            redirectAttributes.addFlashAttribute("error", "Error While Processing Payment.Please try or contact support");
            return "redirect:/groups/requestToJoinGroup";
        }
    }
}
