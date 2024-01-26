package com.ssm.service;


import com.ssm.dto.BuyLikeSuggestionDTO;
import com.ssm.dto.BuySuggestionDTO;
import com.ssm.dto.SellLikeSuggestionDTO;
import com.ssm.dto.SellSuggestionDTO;
import com.ssm.entity.*;
import com.ssm.exception.GroupSessionException;
import com.ssm.exception.LikesException;
import com.ssm.exception.SuggestionNotFoundException;
import com.ssm.exception.UserNotFoundException;
import com.ssm.mapper.IBuyLikeSuggestionMapper;
import com.ssm.mapper.IBuySuggestionMapper;
import com.ssm.mapper.ISellLikeSuggestionMapper;
import com.ssm.mapper.ISellSuggestionMapper;
import com.ssm.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    IBuyLikeSuggestionMapper buyLikeSuggestionMapper;
    @Autowired
    ISellLikeSuggestionMapper sellLikeSuggestionMapper;
    @Autowired
    UserRepository userRepository;

    @Autowired
    LikesRepository likesRepository;

    public SuggestionService(SuggestionRepository suggestionRepository, BuySuggestionRepository buySuggestionRepository, SellSuggestionRepository sellSuggestionRepository, IBuySuggestionMapper buySuggestionMapper, ISellSuggestionMapper sellSuggestionMapper, UserRepository userRepository, LikesRepository likesRepository, IBuyLikeSuggestionMapper buyLikeSuggestionMapper, ISellLikeSuggestionMapper sellLikeSuggestionMapper) {
        this.suggestionRepository = suggestionRepository;
        this.buySuggestionRepository = buySuggestionRepository;
        this.sellSuggestionRepository = sellSuggestionRepository;
        this.buySuggestionMapper = buySuggestionMapper;
        this.sellSuggestionMapper = sellSuggestionMapper;
        this.userRepository = userRepository;
        this.likesRepository = likesRepository;
        this.buyLikeSuggestionMapper = buyLikeSuggestionMapper;
        this.sellLikeSuggestionMapper = sellLikeSuggestionMapper;
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

    //User Group is just to session expiry here
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

    public void deleteSuggestion(UserGroup userGroup, Long id) throws SuggestionNotFoundException, DataAccessException {
        suggestionRepository.deleteById(id);
        likesRepository.deleteBySuggestionId(id);
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

    public StockSuggestion getStockSuggestion(Long id) throws SuggestionNotFoundException {
        return suggestionRepository.findById(id).orElseThrow(() -> new SuggestionNotFoundException("Suggestion Not Found"));
    }

    public void addCommentToSuggestion(StockSuggestion suggestion) throws DataAccessException {
        suggestionRepository.save(suggestion);
    }

    private User getUserForLikes(String username) throws UserNotFoundException {
        return userRepository.findByUserName(username).orElseThrow(() -> new UserNotFoundException("User Not Found While Fetching the Likes"));
    }


    /**
     * LIKE BUY SUGGESTIONS;
     *
     * @param buySuggestionId
     * @param isLiked
     */
    public void likeBuySuggestion(Long buySuggestionId, boolean isLiked, String username) throws SuggestionNotFoundException, UserNotFoundException, DataAccessException, LikesException {
        BuySuggestion buySuggestion = getBuySuggestionById(buySuggestionId);
        int noOfLikes = buySuggestion.getNoOfLikes();
        if (isLiked) {
            noOfLikes += 1;
        } else {
            if (noOfLikes != 0) {
                noOfLikes -= 1;
            }
        }
        buySuggestion.setNoOfLikes(noOfLikes);
        //update the table that has info about which user liked or unliked a suggestion;
        User user = userRepository.findByUserName(username).orElse(null);
        if (ObjectUtils.isEmpty(user))
            throw new UserNotFoundException("User Session Error In LikeBuy Suggestion. Please Login again");
        Optional<Likes> checkLiked = likesRepository.findByUserAndSuggestion(user, buySuggestion);
        // if a like by user and suggestion is already there and then just update the status of the liked state
        if (checkLiked.isPresent()) {
            if (checkLiked.get().isLiked() != isLiked) {
                checkLiked.get().setLiked(isLiked);
                likesRepository.save(checkLiked.get());
                buySuggestionRepository.save(buySuggestion);
            }
        } else {
            Likes buyLikes = mapBuyLikeSuggestion(buySuggestion, user, isLiked);
            likesRepository.save(buyLikes);
            buySuggestionRepository.save(buySuggestion);
        }
    }

    public void likeSellSuggestion(Long sellSuggestionId, boolean isLiked, String username) throws SuggestionNotFoundException, UserNotFoundException, DataAccessException, LikesException {
        SellSuggestion sellSuggestion = getSellSuggestionById(sellSuggestionId);
        int noOfLikes = sellSuggestion.getNoOfLikes();
        if (isLiked) {
            noOfLikes += 1;
        } else {
            if (noOfLikes != 0) {
                noOfLikes -= 1;
            }
        }
        sellSuggestion.setNoOfLikes(noOfLikes);
        //update the table that has info about which user liked or unliked a suggestion;
        User user = userRepository.findByUserName(username).orElse(null);
        if (ObjectUtils.isEmpty(user))
            throw new UserNotFoundException("User Session Error In LikeSell Suggestion. Please Login again");
        Optional<Likes> checkLiked = likesRepository.findByUserAndSuggestion(user, sellSuggestion);
        // if a like by user and suggestion is already there and then just update the status of the liked state
        if (checkLiked.isPresent()) {
            if (checkLiked.get().isLiked() != isLiked) {
                checkLiked.get().setLiked(isLiked);
                likesRepository.save(checkLiked.get());
                sellSuggestionRepository.save(sellSuggestion);
            }
        } else {
            Likes sellLikes = mapSellLikeSuggestion(sellSuggestion, user, isLiked);
            likesRepository.save(sellLikes);
            sellSuggestionRepository.save(sellSuggestion);
        }
    }

    private Likes mapBuyLikeSuggestion(BuySuggestion buySuggestion, User user, boolean isLiked) {
        Likes likes = new Likes();
        likes.setSuggestion(buySuggestion);
        likes.setUser(user);
        likes.setLiked(isLiked);
        return likes;
    }

    private Likes mapSellLikeSuggestion(SellSuggestion sellSuggestion, User user, boolean isLiked) {
        Likes likes = new Likes();
        likes.setSuggestion(sellSuggestion);
        likes.setUser(user);
        likes.setLiked(isLiked);
        return likes;
    }

    public Map<Long, Boolean> userSuggestionChoice(String username) throws UserNotFoundException, LikesException {
        Map<Long, Boolean> suggestionLikeBasedOnUser = new HashMap<>();
        User user = getUserForLikes(username);
        List<Likes> likes = likesRepository.findByUser(user).orElse(null);
        if (ObjectUtils.isEmpty(likes)) return suggestionLikeBasedOnUser;
        for (Likes like : likes) {
            suggestionLikeBasedOnUser.put(like.getSuggestion().getId(), like.isLiked());
        }
        return suggestionLikeBasedOnUser;
    }

    public List<BuyLikeSuggestionDTO> getBuyLikeSuggestions(String username) throws UserNotFoundException, LikesException {
        List<BuySuggestion> buySuggestionList = getBuySuggestions();
        Map<Long, Boolean> suggestionLikeBasedOnUser = userSuggestionChoice(username);
        return buySuggestionList.stream()
                .map(buySuggestion -> buyLikeSuggestionMapper.fromBuySuggestionAndChoice(
                        buySuggestion,
                        suggestionLikeBasedOnUser.getOrDefault(buySuggestion.getId(), false),
                        buySuggestion.getId()
                ))
                .sorted(Comparator
                        .comparing((BuyLikeSuggestionDTO dto) -> dto.getBuyPriority().equals("High") ? 0 : 1) //High priority first and then likes and low
                        .thenComparing(BuySuggestionDTO::getNoOfLikes))
                .collect(Collectors.toList());
    }

    public List<SellLikeSuggestionDTO> getSellLikeSuggestions(String username) throws UserNotFoundException, LikesException {
        List<SellSuggestion> sellSuggestionList = getSellSuggestions();
        Map<Long, Boolean> suggestionLikeBasedOnUser = userSuggestionChoice(username);
        return sellSuggestionList.stream()
                .map(sellSuggestion -> sellLikeSuggestionMapper.fromSellSuggestionAndChoice(
                        sellSuggestion,
                        suggestionLikeBasedOnUser.getOrDefault(sellSuggestion.getId(), false),
                        sellSuggestion.getId()
                ))
                .sorted(Comparator
                        .comparing((SellSuggestionDTO dto) -> dto.getSellPriority().equals("High") ? 0 : 1) //High priority first and then likes and low
                        .thenComparing(SellSuggestionDTO::getNoOfLikes))
                .collect(Collectors.toList());
    }


}
