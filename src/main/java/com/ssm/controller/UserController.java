package com.ssm.controller;

import com.ssm.dto.RegisterDTO;
import com.ssm.exception.EmailMismatchException;
import com.ssm.exception.EmailSendingException;
import com.ssm.exception.PasswordUpdateException;
import com.ssm.exception.UserNotFoundException;
import com.ssm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

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

    @GetMapping("/accountInformation")
    public String accountInformation(Principal principal, RedirectAttributes redirectAttributes,
                                     Model model) {
        try {
            RegisterDTO registerDTO = userService.accountInformation(principal.getName());
            model.addAttribute("accountInfo", registerDTO);
            model.addAttribute("username", principal.getName());
            return "accountinfo";
        } catch (DataAccessException | UserNotFoundException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/index";
        }

    }

    @PostMapping("/editFullName")
    public String editFullName(@RequestParam("fullName") String name, Principal principal, RedirectAttributes redirectAttributes) {

        try {
            userService.updateFullName(name, principal.getName());
            redirectAttributes.addFlashAttribute("username", principal.getName());
            redirectAttributes.addFlashAttribute("Successfully Updated FullName");
            return "redirect:/accountInformation";
        } catch (UserNotFoundException | DataAccessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/accountInformation";
        }
    }

    @PostMapping("/editEmail")
    public String editEmail(@RequestParam("email") String email, Principal principal, RedirectAttributes redirectAttributes, Model model) {
        try {
            userService.updateEmail(email, principal.getName());
            redirectAttributes.addFlashAttribute("username", principal.getName());
            redirectAttributes.addFlashAttribute("Successfully Updated Email");
            return "redirect:/accountInformation";
        } catch (UserNotFoundException | DataAccessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/accountInformation";
        }
    }

    @PostMapping("/editPass")
    public String editPass(@RequestParam("currentPass") String currentPass, @RequestParam("newPass") String newPass, Principal principal, RedirectAttributes redirectAttributes, Model model) {
        try {
            userService.updatePass(currentPass, newPass, principal.getName());
            redirectAttributes.addFlashAttribute("username", principal.getName());
            redirectAttributes.addFlashAttribute("Successfully Updated Password");
            return "redirect:/accountInformation";
        } catch (PasswordUpdateException | UserNotFoundException | DataAccessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/accountInformation";
        }
    }
}
