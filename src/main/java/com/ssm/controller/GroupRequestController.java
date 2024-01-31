package com.ssm.controller;


import com.ssm.exception.NotifyException;
import com.ssm.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GroupRequestController {

    @Autowired
    GroupService groupService;

    public GroupRequestController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/request/{id}/{status}")
    public ModelAndView requestUpdate(@PathVariable("id") long id, @PathVariable("status") String status, ModelMap model) {

        try {
            Boolean isStatusUpdated = groupService.requestStatusUpdate(id, status);
            if (!isStatusUpdated) {
                model.addAttribute("error", "SYSTEM ERROR: Error while Updating the Status");
            } else {
                groupService.notifyUserAboutRequest(id, status);
                model.addAttribute("info", "User Request Status Updated");
            }
        } catch (DataAccessException dataAccessException) {
            model.addAttribute("error", "SYSTEM ERROR: Error while Updating the Status");
            return new ModelAndView("redirect:/groups/displayRequests", model);
        } catch (NotifyException e) {
            System.out.println("Throwing this error silently as this wont impact the functionality" + e.getMessage());
        }
        return new ModelAndView("redirect:/groups/displayRequests", model);

    }
}
