package com.ssm.service;


import com.ssm.dto.BuySuggestionDTO;
import com.ssm.dto.SellSuggestionDTO;
import com.ssm.entity.BuySuggestion;
import com.ssm.entity.SellSuggestion;
import com.ssm.entity.UserGroup;
import com.ssm.exception.GroupSessionException;
import com.ssm.exception.SuggestionNotFoundException;
import com.ssm.mapper.IBuySuggestionMapper;
import com.ssm.mapper.ISellSuggestionMapper;
import com.ssm.repository.BuySuggestionRepository;
import com.ssm.repository.SellSuggestionRepository;
import com.ssm.repository.SuggestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Service
public class SuggestionService {

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Autowired
    BuySuggestionRepository buySuggestionRepository;

    @Autowired
    SellSuggestionRepository sellSuggestionRepository;

    @Autowired
    IBuySuggestionMapper buySuggestionMapper;

    @Autowired
    ISellSuggestionMapper sellSuggestionMapper;

    public SuggestionService(SuggestionRepository suggestionRepository, BuySuggestionRepository buySuggestionRepository, SellSuggestionRepository sellSuggestionRepository, IBuySuggestionMapper buySuggestionMapper, ISellSuggestionMapper sellSuggestionMapper) {
        this.suggestionRepository = suggestionRepository;
        this.buySuggestionRepository = buySuggestionRepository;
        this.sellSuggestionRepository = sellSuggestionRepository;
        this.buySuggestionMapper = buySuggestionMapper;
        this.sellSuggestionMapper = sellSuggestionMapper;
    }

    public void addBuySuggestion(UserGroup userGroup, BuySuggestionDTO buySuggestionDTO) throws GroupSessionException {
        if (ObjectUtils.isEmpty(userGroup))
            throw new GroupSessionException("Group Session Expired.Please Re-Login again");
        BuySuggestion buySuggestion = buySuggestionMapper.toBuySuggestionEntity(userGroup, buySuggestionDTO);
        suggestionRepository.save(buySuggestion);
    }

    public void addSellSuggestion(UserGroup userGroup, SellSuggestionDTO sellSuggestionDTO) throws GroupSessionException {
        if (ObjectUtils.isEmpty(userGroup))
            throw new GroupSessionException("Group Session Expired.Please Re-Login again");
        SellSuggestion sellSuggestion = sellSuggestionMapper.toSellSuggestionEntity(userGroup, sellSuggestionDTO);
        suggestionRepository.save(sellSuggestion);
    }

    //ALL These needs to be handled with proper exceptions;
    public List<BuySuggestion> getBuySuggestions() {
        return buySuggestionRepository.findAll();
    }

    public List<SellSuggestion> getSellSuggestions() {
        return sellSuggestionRepository.findAll();
    }

    public BuySuggestion getBuySuggestionById(Long id) throws SuggestionNotFoundException {
        return buySuggestionRepository.findById(id).orElseThrow(() -> new SuggestionNotFoundException("Buy Suggestion Not Found to Edit"));
    }

    public SellSuggestion getSellSuggestion(Long id) throws SuggestionNotFoundException {
        return sellSuggestionRepository.findById(id).orElseThrow(() -> new SuggestionNotFoundException("Sell Suggestion Not Found to Edit"));
    }
}
