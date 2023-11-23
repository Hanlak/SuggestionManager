package com.ssm.controller;

import com.ssm.exception.EmailMismatchException;
import com.ssm.exception.EmailSendingException;
import com.ssm.exception.PasswordUpdateException;
import com.ssm.exception.UserNotFoundException;
import com.ssm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //LOGIN:
    @GetMapping("/login")
    protected String login() {
        return "login";
    }

    //FORGOT PASSWORD:
    @GetMapping("/forgotpassword")
    protected String forgotPassword() {
        return "forgotpassword";
    }

    @PostMapping("/forgotpassword")
    public String forgotPassword(@RequestParam("username") String username,
                                 @RequestParam("email") String email,
                                 RedirectAttributes redirectAttributes) {
        try {
            userService.forgotPassword(username, email);
            redirectAttributes.addFlashAttribute("info", "Password reset instructions sent to your email.");
            return "redirect:/login";
        } catch (UserNotFoundException | EmailMismatchException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/forgotPassword";
        } catch (EmailSendingException | PasswordUpdateException e) {
            redirectAttributes.addFlashAttribute("error", "Encountered an issue. Please try again.");
            return "redirect:/forgotPassword";
        }
    }

}
