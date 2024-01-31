package com.ssm.controller;

import com.ssm.dto.BuyLikeSuggestionDTO;
import com.ssm.dto.BuySuggestionDTO;
import com.ssm.dto.SellLikeSuggestionDTO;
import com.ssm.dto.SellSuggestionDTO;
import com.ssm.entity.BuySuggestion;
import com.ssm.entity.SellSuggestion;
import com.ssm.entity.UserGroup;
import com.ssm.exception.*;
import com.ssm.service.GroupService;
import com.ssm.service.PaymentService;
import com.ssm.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/suggestion/")
@SessionAttributes("userGroup")
public class SuggestionController {


    @Autowired
    SuggestionService suggestionService;

    @Autowired
    PaymentService paymentService;
    @Autowired
    GroupService groupService;

    List<String> priorities = Arrays.asList("HIGH", "LOW");

    public SuggestionController(SuggestionService suggestionService, PaymentService paymentService) {
        this.suggestionService = suggestionService;
        this.paymentService = paymentService;
    }

    @GetMapping("/groupIndex/{id}/{groupName}")
    public String groupIndex(@PathVariable("id") Long groupId,
                             @PathVariable("groupName") String groupName,
                             RedirectAttributes redirectAttributes,
                             Model model, Principal principal) {
        try {
            UserGroup userGroup = groupService.getUserGroupBasedOnGroupId(groupId);
            model.addAttribute("userGroup", userGroup);
            boolean isAdmin = principal.getName().equals(userGroup.getAdmin().getUserName());
            // if he is admin of the group no need to check the paid stuff
            if (UserGroup.Subscription.PAID.equals(userGroup.getSubscription()) && !isAdmin) {
                String subscriptionStatusOrMessage = paymentService.validatePaymentSubscription(userGroup, principal.getName());
                if (!"ACTIVE".equals(subscriptionStatusOrMessage))
                    model.addAttribute("info", subscriptionStatusOrMessage);
            }
            model.addAttribute("adminOfTheGroup", userGroup.getAdmin().getUserName());
            model.addAttribute("isAdmin", isAdmin);
            List<BuyLikeSuggestionDTO> buySuggestionList = suggestionService.getBuyLikeSuggestions(userGroup, principal.getName());
            List<SellLikeSuggestionDTO> sellSuggestionList = suggestionService.getSellLikeSuggestions(userGroup, principal.getName());
            model.addAttribute("buySuggestions", buySuggestionList);
            model.addAttribute("sellSuggestions", sellSuggestionList);
            return "groupindex";
        } catch (GroupNotFoundException | UserNotFoundException | LikesException | SubscriptionExpiredException |
                 PaymentNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/index";
        } catch (DataAccessException dataAccessException) {
            redirectAttributes.addFlashAttribute("error", "Error While Accessing Data while Fetching the Group");
            return "redirect:/index";
        }
    }

    //TODO: PREVENT DIRECT ACCESS TO THIS URL;
    @GetMapping("/suggestionForm")
    public String showSuggestionForm(@SessionAttribute(value = "userGroup", required = false) UserGroup userGroup, Principal principal, Model model) {
        if (ObjectUtils.isEmpty(userGroup))
            return "redirect:/logout?groupSessionExpired";
        model.addAttribute("userGroup", userGroup);
        boolean isAdmin = principal.getName().equals(userGroup.getAdmin().getUserName());
        model.addAttribute("adminOfTheGroup", userGroup.getAdmin().getUserName());
        model.addAttribute("isAdmin", isAdmin);
        try {
            if (ObjectUtils.isEmpty(userGroup))
                throw new GroupSessionException("Group Session Expired.Please Re-Login again");
        } catch (GroupSessionException groupSessionException) {
            return "redirect:/logout?groupSessionExpired";
        }
        model.addAttribute("groupName", userGroup.getGroupName());
        return "suggestionform";
    }

    @PostMapping("/submitBuy")
    public String submitBuy(@SessionAttribute(value = "userGroup", required = false) UserGroup userGroup, @ModelAttribute("buySuggestion") BuySuggestionDTO buySuggestionDTO, RedirectAttributes redirectAttributes) {
        if (ObjectUtils.isEmpty(userGroup))
            return "redirect:/logout?groupSessionExpired";
        try {
            suggestionService.addBuySuggestion(userGroup, buySuggestionDTO);
            return "redirect:/suggestion/groupIndex/" + userGroup.getId() + "/" + userGroup.getGroupName();
        } catch (DataAccessException dataAccessException) {
            redirectAttributes.addFlashAttribute("error", "Error While Saving Buy Data");
            return "redirect:/suggestion/suggestionForm";
        } catch (GroupSessionException groupSessionException) {
            return "redirect:/logout?groupSessionExpired";
        }
    }

    @PostMapping("/submitSell")
    public String submitSell(@SessionAttribute(value = "userGroup", required = false) UserGroup userGroup, @ModelAttribute("sellSuggestion") SellSuggestionDTO sellSuggestionDTO, RedirectAttributes redirectAttributes) {
        if (ObjectUtils.isEmpty(userGroup))
            return "redirect:/logout?groupSessionExpired";
        try {
            suggestionService.addSellSuggestion(userGroup, sellSuggestionDTO);
            return "redirect:/suggestion/groupIndex/" + userGroup.getId() + "/" + userGroup.getGroupName();
        } catch (DataAccessException dataAccessException) {
            redirectAttributes.addFlashAttribute("error", "Error While Saving Sell Data");
            return "redirect:/suggestion/suggestionForm";
        } catch (GroupSessionException groupSessionException) {
            return "redirect:/logout?groupSessionExpired";
        }

    }

    @GetMapping("/editBuySuggestion/{id}")
    public String showEditBuySuggestion(@SessionAttribute(value = "userGroup", required = false) UserGroup userGroup, @PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        if (ObjectUtils.isEmpty(userGroup))
            return "redirect:/logout?groupSessionExpired";
        try {
            if (ObjectUtils.isEmpty(userGroup))
                throw new GroupSessionException("Group Session Expired.Please Re-Login again");
            BuySuggestion buySuggestion = suggestionService.getBuySuggestionById(id);
            model.addAttribute("id", id);
            model.addAttribute("priorities", priorities);
            model.addAttribute("buySuggestion", buySuggestion);
        } catch (SuggestionNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/suggestion/groupIndex/" + userGroup.getId() + "/" + userGroup.getGroupName();
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "System Error: while Accessing the data");
            return "redirect:/suggestion/groupIndex/" + userGroup.getId() + "/" + userGroup.getGroupName();
        } catch (GroupSessionException groupSessionException) {
            return "redirect:/logout?groupSessionExpired";
        }
        return "editbuysuggestion";
    }

    @GetMapping("/editSellSuggestion/{id}")
    public String showEditSellSuggestion(@SessionAttribute(value = "userGroup", required = false) UserGroup userGroup, @PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        if (ObjectUtils.isEmpty(userGroup))
            return "redirect:/logout?groupSessionExpired";
        try {
            SellSuggestion sellSuggestion = suggestionService.getSellSuggestionById(id);
            model.addAttribute("id", id);
            model.addAttribute("priorities", priorities);
            model.addAttribute("sellSuggestion", sellSuggestion);
        } catch (SuggestionNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/suggestion/groupIndex/" + userGroup.getId() + "/" + userGroup.getGroupName();
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "System Error: while Accessing the data");
            return "redirect:/suggestion/groupIndex/" + userGroup.getId() + "/" + userGroup.getGroupName();
        }
        return "editsellsuggestion";
    }

    @PostMapping("/editBuySuggestion")
    public String updateBuySuggestion(@SessionAttribute(value = "userGroup", required = false) UserGroup userGroup, @RequestParam("id") Long id, @ModelAttribute("buySuggestion") BuySuggestionDTO buySuggestionDTO, Principal principal, RedirectAttributes redirectAttributes) {
        if (ObjectUtils.isEmpty(userGroup))
            return "redirect:/logout?groupSessionExpired";
        try {
            suggestionService.updateBuySuggestion(userGroup, id, buySuggestionDTO);
        } catch (SuggestionNotFoundException | DataAccessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/suggestion/groupIndex/" + userGroup.getId() + "/" + userGroup.getGroupName();
        } catch (GroupSessionException groupSessionException) {
            return "redirect:/logout?groupSessionExpired";
        }
        redirectAttributes.addFlashAttribute("info", "Suggestion Update Success");
        return "redirect:/suggestion/groupIndex/" + userGroup.getId() + "/" + userGroup.getGroupName();
    }

    @PostMapping("/editSellSuggestion")
    public String updateSellSuggestion(@SessionAttribute(value = "userGroup", required = false) UserGroup userGroup, @RequestParam("id") Long id, @ModelAttribute("SellSuggestionDTO") SellSuggestionDTO sellSuggestionDTO, Principal principal, RedirectAttributes redirectAttributes) {
        if (ObjectUtils.isEmpty(userGroup))
            return "redirect:/logout?groupSessionExpired";
        try {
            suggestionService.updateSellSuggestion(userGroup, id, sellSuggestionDTO);
        } catch (SuggestionNotFoundException | DataAccessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/suggestion/groupIndex/" + userGroup.getId() + "/" + userGroup.getGroupName();
        } catch (GroupSessionException groupSessionException) {
            return "redirect:/logout?groupSessionExpired";
        }
        return "redirect:/suggestion/groupIndex/" + userGroup.getId() + "/" + userGroup.getGroupName();
    }


    @GetMapping("/deleteSuggestion/{id}")
    public String deleteSuggestion(@SessionAttribute(value = "userGroup", required = false) UserGroup userGroup, @PathVariable Long id, Principal principal, RedirectAttributes redirectAttributes) {
        if (ObjectUtils.isEmpty(userGroup))
            return "redirect:/logout?groupSessionExpired";
        try {
            suggestionService.deleteSuggestion(userGroup, id);
        } catch (SuggestionNotFoundException | DataAccessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/suggestion/groupIndex/" + userGroup.getId() + "/" + userGroup.getGroupName();
        }
        return "redirect:/suggestion/groupIndex/" + userGroup.getId() + "/" + userGroup.getGroupName();

    }

    //TODO: session expiry exception handling;;
    @GetMapping("/deleteGroup")
    public String deleteGroup(@SessionAttribute(value = "userGroup", required = false) UserGroup userGroup, Principal principal, Model model) {
        if (ObjectUtils.isEmpty(userGroup))
            return "redirect:/logout?groupSessionExpired";
        model.addAttribute("username", principal.getName());
        model.addAttribute("groupName", userGroup.getGroupName());
        return "deletegroup";
    }


    @PostMapping("/deleteGroup")
    public String deleteGroup(@RequestParam("groupName") String groupName, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            groupService.deleteGroup(groupName, principal.getName());
            redirectAttributes.addFlashAttribute("info", "Group Deleted Successfully");
            return "redirect:/index";
        } catch (UserNotFoundException userNotFoundException) {
            redirectAttributes.addFlashAttribute("info", userNotFoundException.getMessage());
            return "redirect:/index";
        } catch (DataAccessException dataAccessException) {
            redirectAttributes.addFlashAttribute("error", "Trouble While Performing Delete Action.Please Try Again");
            return "redirect:/index";
        }
    }


    @GetMapping("/leaveGroup")
    public String leaveGroup(@SessionAttribute(value = "userGroup", required = false) UserGroup userGroup, Principal principal, RedirectAttributes redirectAttributes) {
        if (ObjectUtils.isEmpty(userGroup))
            return "redirect:/logout?groupSessionExpired";
        try {
            groupService.leaveGroup(userGroup.getGroupName(), principal.getName());
        } catch (GroupNotFoundException groupNotFoundException) {
            redirectAttributes.addFlashAttribute("error", groupNotFoundException.getMessage());
            return "redirect:/index";
        } catch (DataAccessException dataAccessException) {
            redirectAttributes.addFlashAttribute("error", "Trouble While Performing Leave Action.Please Try Again");
            return "redirect:/suggestion/groupIndex/" + userGroup.getId() + "/" + userGroup.getGroupName();
        } catch (LeaveGroupException leaveGroupException) {
            redirectAttributes.addFlashAttribute("error", "Trouble While Performing Leave Action.Please Try Again");
            return "redirect:/index"; // should go to group Index
        }
        return "redirect:/index";
    }

    @GetMapping("/remove/{member}")
    public String removeMemberFromGroup(@SessionAttribute(value = "userGroup", required = false) UserGroup userGroup, @PathVariable("member") String member, Principal principal, RedirectAttributes redirectAttributes) {
        if (ObjectUtils.isEmpty(userGroup))
            return "redirect:/logout?groupSessionExpired";
        try {
            groupService.removeMember(userGroup.getGroupName(), principal.getName(), member);
        } catch (GroupNotFoundException | AccessException | UserNotFoundException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/suggestion/showMembers";
        } catch (DataAccessException dataAccessException) {
            redirectAttributes.addFlashAttribute("error", "Trouble While Performing Remove Action.Please Try Again");
            return "redirect:/suggestion/showMembers";
        }
        return "redirect:/suggestion/showMembers";
    }

    @PostMapping("/buyLike")
    public String buyLike(@SessionAttribute(value = "userGroup", required = false) UserGroup userGroup, @RequestParam("buySuggestionId") Long buySuggestionId, @RequestParam("buyLiked") boolean buyLiked, Principal principal, RedirectAttributes redirectAttributes) {
        if (ObjectUtils.isEmpty(userGroup))
            return "redirect:/logout?groupSessionExpired";
        try {
            suggestionService.likeBuySuggestion(buySuggestionId, buyLiked, principal.getName());
        } catch (SuggestionNotFoundException | UserNotFoundException | LikesException e) {
            redirectAttributes.addFlashAttribute("error", "Something wrong happened while liking the suggestion");
            return "redirect:/suggestion/groupIndex/" + userGroup.getId() + "/" + userGroup.getGroupName();
        }
        return "redirect:/suggestion/groupIndex/" + userGroup.getId() + "/" + userGroup.getGroupName();
    }

    @PostMapping("/sellLike")
    public String sellLike(@SessionAttribute(value = "userGroup", required = false) UserGroup userGroup, @RequestParam("sellSuggestionId") Long sellSuggestionId, @RequestParam("sellLiked") boolean sellLiked, Principal principal, RedirectAttributes redirectAttributes) {
        if (ObjectUtils.isEmpty(userGroup))
            return "redirect:/logout?groupSessionExpired";
        try {
            suggestionService.likeSellSuggestion(sellSuggestionId, sellLiked, principal.getName());
        } catch (SuggestionNotFoundException | UserNotFoundException | LikesException e) {
            redirectAttributes.addFlashAttribute("error", "Something wrong happened while liking the suggestion");
            return "redirect:/suggestion/groupIndex/" + userGroup.getId() + "/" + userGroup.getGroupName();
        }
        return "redirect:/suggestion/groupIndex/" + userGroup.getId() + "/" + userGroup.getGroupName();
    }

    @GetMapping("/showMembers")
    public String showMembers(@SessionAttribute(value = "userGroup", required = false) UserGroup userGroup, Principal principal, Model model) throws GroupSessionException {
        if (ObjectUtils.isEmpty(userGroup))
            return "redirect:/logout?groupSessionExpired";
        model.addAttribute("groupName", userGroup.getGroupName());
        boolean isAdmin = principal.getName().equals(userGroup.getAdmin().getUserName());
        model.addAttribute("adminOfTheGroup", userGroup.getAdmin().getUserName());
        model.addAttribute("isAdmin", isAdmin);
        try {
            Set<String> members = groupService.getMemberOfTheGroup(userGroup);
            model.addAttribute("members", members);
            return "showmembers";
        } catch (GroupNotFoundException | DataAccessException exception) {
            model.addAttribute("error", exception.getMessage());
            return "showmembers";
        }
    }
}
