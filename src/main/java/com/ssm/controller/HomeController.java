package com.ssm.controller;


import com.ssm.entity.UserGroup;
import com.ssm.exception.UserNotFoundException;
import com.ssm.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    GroupService groupService;

    public HomeController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping({"/", "/index"})
    public ModelAndView index(Principal principal) {
        ModelAndView model = new ModelAndView("newindex");
        model.addObject("username", principal.getName());
        try {
            List<UserGroup> groups = groupService.getAllGroupsBasedOnUser(principal.getName());
            model.addObject("groups", groups);
        } catch (UserNotFoundException e) {
            model.addObject("error", e.getMessage());
        } catch (DataAccessException e) {
            model.addObject("error", "System Error: While Fetching Groups");
        }
        return model;
    }
}
