package com.ssm.controller;

import com.ssm.dto.BuySuggestionDTO;
import com.ssm.dto.SellSuggestionDTO;
import com.ssm.entity.BuySuggestion;
import com.ssm.entity.SellSuggestion;
import com.ssm.entity.UserGroup;
import com.ssm.exception.GroupNotFoundException;
import com.ssm.exception.GroupSessionException;
import com.ssm.exception.SuggestionNotFoundException;
import com.ssm.service.GroupService;
import com.ssm.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/suggestion/")
@SessionAttributes("userGroup")
public class SuggestionController {


    @Autowired
    SuggestionService suggestionService;

    @Autowired
    GroupService groupService;

    List<String> priorities = Arrays.asList("HIGH", "LOW");

    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    @GetMapping("/groupIndex/{id}/{groupName}")
    public String groupIndex(@PathVariable("id") Long groupId,
                             @PathVariable("groupName") String groupName,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        try {
            UserGroup userGroup = groupService.getUserGroupBasedOnGroupId(groupId);
            model.addAttribute("userGroup", userGroup);
            List<BuySuggestion> buySuggestionList = suggestionService.getBuySuggestions();
            List<SellSuggestion> sellSuggestionList = suggestionService.getSellSuggestions();
            model.addAttribute("buySuggestions", buySuggestionList);
            model.addAttribute("sellSuggestions", sellSuggestionList);
            return "groupindex";
        } catch (GroupNotFoundException groupNotFoundException) {
            redirectAttributes.addFlashAttribute("error", groupNotFoundException.getMessage());
            return "redirect:/index";
        } catch (DataAccessException dataAccessException) {
            redirectAttributes.addFlashAttribute("error", "Error While Accessing Data while Fetching the Group");
            return "redirect:/index";
        }
    }

    //TODO: PREVENT DIRECT ACCESS TO THIS URL;
    @GetMapping("/suggestionForm")
    public String showSuggestionForm(Model model) {
        return "suggestionform";
    }

    @PostMapping("/submitBuy")
    public String submitBuy(@SessionAttribute("userGroup") UserGroup userGroup, @ModelAttribute("buySuggestion") BuySuggestionDTO buySuggestionDTO, RedirectAttributes redirectAttributes) {
        try {
            System.out.println(userGroup.getGroupName());
            suggestionService.addBuySuggestion(userGroup, buySuggestionDTO);
            return "redirect:/suggestion/groupIndex/" + userGroup.getId() + "/" + userGroup.getGroupName();
        } catch (DataAccessException dataAccessException) {
            dataAccessException.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error While Saving Buy Data");
            return "redirect:/suggestion/suggestionForm";
        } catch (GroupSessionException groupSessionException) {
            return "redirect:/logout?groupSessionExpired";
        }
    }

    @PostMapping("/submitSell")
    public String submitSell(@SessionAttribute("userGroup") UserGroup userGroup, @ModelAttribute("sellSuggestion") SellSuggestionDTO sellSuggestionDTO, RedirectAttributes redirectAttributes) {
        // Handle SellSuggestion submission
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
    public String showEditBuySuggestion(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            BuySuggestion buySuggestion = suggestionService.getBuySuggestionById(id);
            model.addAttribute("id", id);
            model.addAttribute("priorities", priorities);
            model.addAttribute("buySuggestion", buySuggestion);
        } catch (SuggestionNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/groupindex";
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "System Error: while Accessing the data");
            return "redirect:/groupindex";
        }
        return "editbuysuggestion";
    }

    @GetMapping("/editSellSuggestion/{id}")
    public String showEditSellSuggestion(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            SellSuggestion sellSuggestion = suggestionService.getSellSuggestion(id);
            model.addAttribute("id", id);
            model.addAttribute("sellSuggestion", sellSuggestion);
        } catch (SuggestionNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/groupindex";
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "System Error: while Accessing the data");
            return "redirect:/groupindex";
        }
        return "editsellsuggestion";
    }

    @PostMapping("/editBuySuggestion")
    public String updateBuySuggestion(@RequestParam("id") Long id, @ModelAttribute("buySuggestion") BuySuggestionDTO buySuggestionDTO, Principal principal, RedirectAttributes redirectAttributes) {
        System.out.println("ID:" + id);
        System.out.println(buySuggestionDTO.getStockName());
        System.out.println(buySuggestionDTO.getMinBuy());
        System.out.println(buySuggestionDTO.getMaxBuy());
        System.out.println(buySuggestionDTO.getTarget());
        System.out.println(buySuggestionDTO.getStopLoss());
        redirectAttributes.addFlashAttribute("info", "UserGroup Created Successfully");
        return "redirect:/index";
    }


}
