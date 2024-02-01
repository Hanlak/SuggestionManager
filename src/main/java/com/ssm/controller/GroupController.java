package com.ssm.controller;

import com.ssm.dto.UserGroupDTO;
import com.ssm.entity.GroupRequest;
import com.ssm.exception.*;
import com.ssm.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @ModelAttribute("userGroup")
    public UserGroupDTO userGroupDTO() {
        return new UserGroupDTO();
    }

    @GetMapping("/createGroup")
    public ModelAndView createGroup(Principal principal) {
        ModelAndView modelAndView = new ModelAndView("creategroup");
        modelAndView.addObject("username", principal.getName());
        return modelAndView;
    }

    @PostMapping("/createGroup")
    public String createGroup(@ModelAttribute("userGroup") UserGroupDTO userGroupDTO, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            groupService.createGroup(userGroupDTO, principal.getName());
            System.out.println(userGroupDTO.getGroupType() + " " + userGroupDTO.getSubscription());
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
    public String requestToJoinGroup(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        List<UserGroupDTO> groups = groupService.getAllGroups();
        model.addAttribute("groups", groups);
        return "joingroup";
    }

    @GetMapping("/joinGroup/{groupId}/{groupName}")
    public String joinGroup(Principal principal, @PathVariable("groupId") Long id, @PathVariable("groupName") String groupName, RedirectAttributes redirectAttributes, Model model) {
        try {
            if (groupService.isPaymentEligible(groupName, principal.getName())) {
                //Check if there is a payment entry for the user and if it's there if it's a success payment or not;
                model.addAttribute("groupName", groupName);
                model.addAttribute("username", principal.getName());
                return "paypage";
            } else {
                return "redirect:/groups/sendRequest/" + groupName;
            }
        } catch (GroupNotFoundException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/groups/requestToJoinGroup";
        } catch (UserAlreadyExistsException exception) {
            redirectAttributes.addFlashAttribute("info", exception.getMessage());
            return "redirect:/suggestion/groupIndex/" + id + "/" + groupName;
        }

    }


    @GetMapping("/sendRequest/{groupName}")
    public String sendRequestToJoinGroup(@PathVariable("groupName") String groupName, Principal principal, RedirectAttributes redirectAttributes) {
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
        } catch (UserAlreadyExistsException | PendingRequestException userAlreadyExistsException) {
            redirectAttributes.addFlashAttribute("info", userAlreadyExistsException.getMessage());
            return "redirect:/index";
        }

        return "redirect:/index";
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
