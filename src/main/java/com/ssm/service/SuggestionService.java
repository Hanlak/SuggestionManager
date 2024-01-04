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
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
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

    //Usergroup is just to session expiry here
    public void updateBuySuggestion(UserGroup userGroup, Long id, BuySuggestionDTO buySuggestionDTO) throws SuggestionNotFoundException, DataAccessException, GroupSessionException {
        if (ObjectUtils.isEmpty(userGroup))
            throw new GroupSessionException("Group Session Expired.Please Re-Login again");
        BuySuggestion buySuggestion = getBuySuggestionById(id);
        buySuggestion.setMaxBuy(buySuggestionDTO.getMaxBuy());
        buySuggestion.setMinBuy(buySuggestionDTO.getMinBuy());
        buySuggestion.setBuyPriority(buySuggestionDTO.getBuyPriority());
        buySuggestion.setQbos(buySuggestionDTO.getQbos());
        buySuggestion.setTarget(buySuggestionDTO.getTarget());
        buySuggestion.setStopLoss(buySuggestionDTO.getStopLoss());
        buySuggestion.setLastUpdated(new Date());
        suggestionRepository.save(buySuggestion);
    }

    public void updateSellSuggestion(UserGroup userGroup, Long id, SellSuggestionDTO suggestionDTO) throws SuggestionNotFoundException, DataAccessException, GroupSessionException {
        if (ObjectUtils.isEmpty(userGroup)) {
            throw new GroupSessionException("Group Session Expired.Please Re-login again");
        }
        SellSuggestion sellSuggestion = getSellSuggestionById(id);
        sellSuggestion.setWarnSellLevel(suggestionDTO.getWarnSellLevel());
        sellSuggestion.setDangerSellLevel(suggestionDTO.getDangerSellLevel());
        sellSuggestion.setSellPriority(suggestionDTO.getSellPriority());
        sellSuggestion.setPso(suggestionDTO.getPso());
        sellSuggestion.setLastUpdated(new Date());
        suggestionRepository.save(sellSuggestion);
    }

    public void deleteSuggestion(UserGroup userGroup, Long id) throws SuggestionNotFoundException, DataAccessException, GroupSessionException {
        if (ObjectUtils.isEmpty(userGroup)) {
            throw new GroupSessionException("Group Session Expired.Please Re-login again");
        }
        suggestionRepository.deleteById(id);
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

    public SellSuggestion getSellSuggestionById(Long id) throws SuggestionNotFoundException {
        return sellSuggestionRepository.findById(id).orElseThrow(() -> new SuggestionNotFoundException("Sell Suggestion Not Found to Edit"));
    }
}
