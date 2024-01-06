package com.ssm.controller;

import com.ssm.entity.GroupRequest;
import com.ssm.exception.GroupRequestException;
import com.ssm.exception.UserAlreadyExistsException;
import com.ssm.exception.UserNotFoundException;
import com.ssm.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;


    @GetMapping("/createGroup")
    public ModelAndView createGroup(Principal principal) {
        ModelAndView modelAndView = new ModelAndView("creategroup");
        modelAndView.addObject("username", principal.getName());
        return modelAndView;
    }

    @PostMapping("/createGroup")
    public String createGroup(@RequestParam("groupName") String groupName, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            groupService.createGroup(groupName, principal.getName());
            redirectAttributes.addFlashAttribute("info", "UserGroup Created Successfully");
            return "redirect:/index";
        } catch (UserNotFoundException | UserAlreadyExistsException userNotFoundException) {
            redirectAttributes.addFlashAttribute("error", userNotFoundException.getMessage());
            return "redirect:/groups/createGroup";
        } catch (DataAccessException dataAccessException) {
            redirectAttributes.addFlashAttribute("error", "System Error: UserGroup Creation Failed");
            return "redirect:/groups/createGroup";
        }
    }

    @GetMapping("/requestToJoinGroup")
    public ModelAndView requestToJoinGroup(Principal principal) {
        ModelAndView modelAndView = new ModelAndView("joingroup");
        modelAndView.addObject("username", principal.getName());
        return modelAndView;
    }


    @PostMapping("/sendRequest")
    public String sendRequestToJoinGroup(@RequestParam("groupName") String groupName, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            Boolean isRequestSent = groupService.sendRequestToJoinGroup(groupName, principal.getName());
            if (isRequestSent) {
                redirectAttributes.addFlashAttribute("info", "A request has been sent to join the group");
            } else {
                redirectAttributes.addFlashAttribute("error", "System Error: While Sending Request. Try Again");
                return "redirect:/groups/requestToJoinGroup";
            }
        } catch (DataAccessException dataAccessException) {
            System.out.println(dataAccessException.getMessage());
            redirectAttributes.addFlashAttribute("error", "System Error: While Sending Request. Try Again");
            return "redirect:/groups/requestToJoinGroup";
        } catch (UserNotFoundException | GroupRequestException customException) {
            redirectAttributes.addFlashAttribute("error", customException.getMessage());
            return "redirect:/groups/requestToJoinGroup";
        } catch (UserAlreadyExistsException userAlreadyExistsException) {
            redirectAttributes.addFlashAttribute("info", userAlreadyExistsException.getMessage());
            return "redirect:/index";
        }

        return "redirect:/index";
    }


    @GetMapping("/deleteGroup")
    public ModelAndView deleteGroup(Principal principal) {
        ModelAndView modelAndView = new ModelAndView("deletegroup");
        modelAndView.addObject("username", principal.getName());
        return modelAndView;
    }


    @PostMapping("/deleteGroup")
    public ModelAndView deleteGroup(@RequestParam("groupName") String groupName, Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            groupService.deleteGroup(groupName, principal.getName());
            modelAndView.addObject("info", "Group Deleted Successfully");
            modelAndView.setViewName("index");
            return modelAndView;
        } catch (UserNotFoundException userNotFoundException) {
            modelAndView.addObject("info", userNotFoundException.getMessage());
            modelAndView.setViewName("index");
            return modelAndView;
        } catch (DataAccessException dataAccessException) {
            modelAndView.addObject("error", "Trouble While Performing Delete Action.Please Try Again");
            modelAndView.setViewName("index");
            return modelAndView;
        }
    }


    @GetMapping("/displayRequests")
    public ModelAndView displayRequests(Principal principal) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("username", principal.getName());
        try {
            List<GroupRequest> pendingRequests = groupService.displayPendingRequests(principal.getName());
            modelAndView.addObject("pendingRequests", pendingRequests);
            modelAndView.setViewName("displayrequests");
            return modelAndView;
        } catch (UserNotFoundException userNotFoundException) {
            modelAndView.addObject("error", userNotFoundException.getMessage());
            modelAndView.setViewName("index");
            return modelAndView;
        }
    }
}
