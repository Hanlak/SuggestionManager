package com.ssm.controller;

import com.ssm.exception.GroupNotFoundException;
import com.ssm.exception.PaymentException;
import com.ssm.exception.PaymentNotFoundException;
import com.ssm.exception.UserNotFoundException;
import com.ssm.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

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

    @GetMapping("/makePayment")
    public String makePayment(Principal principal, Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("username", principal.getName());
        try {
            List<String> groupNames = paymentService.getAllPaidNonAdminGroupsBasedOnUser(principal.getName());
            model.addAttribute("groupNames", groupNames);
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Error While Re Activating the Subscription:Contact Support");
            return "redirect:/index";
        }
        return "makepayment";
    }

    @PostMapping("/makePayment")
    public String postPayment(@RequestParam("groupName") String groupName, Principal principal, RedirectAttributes redirectAttributes) {
        System.out.println("GroupName:" + groupName);
        redirectAttributes.addFlashAttribute("username", principal.getName());
        try {
            paymentService.makePayment(groupName, principal.getName());
            redirectAttributes.addFlashAttribute("info", "Subscription ReActivated.");
            return "redirect:/index";
        } catch (UserNotFoundException | GroupNotFoundException | PaymentNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/groups/makePayment";
        } catch (PaymentException e) {
            redirectAttributes.addFlashAttribute("info", e.getMessage());
            return "redirect:/index";
        }
    }
}