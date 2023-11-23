package com.ssm.controller;


import com.ssm.dto.RegisterDTO;
import com.ssm.entity.User;
import com.ssm.exception.UserAlreadyExistsException;
import com.ssm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    @Autowired
    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @ModelAttribute("user")
    public RegisterDTO registerDTO() {
        return new RegisterDTO();
    }


    //REGISTER
    @GetMapping
    protected String register() {
        return "register";
    }

    @PostMapping
    protected String registerProcess(@ModelAttribute("user") RegisterDTO registerDTO, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.saveUser(registerDTO);
            if (!ObjectUtils.isEmpty(user)) {
                redirectAttributes.addFlashAttribute("info", "User registration successful.");
            } else {
                redirectAttributes.addFlashAttribute("error", "User Registration Failed");
            }
        } catch (DataAccessException dataAccessException) {
            if (dataAccessException.getCause().getCause().getMessage().contains("Duplicate")) {
                redirectAttributes.addFlashAttribute("info", "User Already Exists. Please Login");
            } else {
                redirectAttributes.addFlashAttribute("error", "There is a problem while saving your data. Please try again.");
            }
            return "redirect:/register";
        } catch (UserAlreadyExistsException userAlreadyExistsException) {
            redirectAttributes.addFlashAttribute("error", userAlreadyExistsException.getMessage());
            return "redirect:/register";
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", "System Error: Please contact support");
            return "redirect:/register";
        }
        return "redirect:/login";
    }
}
