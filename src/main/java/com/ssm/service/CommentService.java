package com.ssm.service;

import com.ssm.dto.CommentDTO;
import com.ssm.entity.Comment;
import com.ssm.entity.StockSuggestion;
import com.ssm.entity.User;
import com.ssm.exception.SuggestionNotFoundException;
import com.ssm.exception.UserNotFoundException;
import com.ssm.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    SuggestionService suggestionService;

    @Autowired
    UserService userService;

    public CommentService(CommentRepository commentRepository, SuggestionService suggestionService, UserService userService) {
        this.commentRepository = commentRepository;
        this.suggestionService = suggestionService;
        this.userService = userService;
    }

    public List<CommentDTO> getCommentsBasedOnSuggestion(long suggestionId) throws SuggestionNotFoundException, DataAccessException {
        StockSuggestion suggestion = suggestionService.getStockSuggestion(suggestionId);
        return suggestion.getComments().stream().map(this::mapCommentsEntityToDTO).collect(Collectors.toList());
    }

    public CommentDTO addCommentBasedOnSuggestion(long suggestionId, String username, String commentText) throws DataAccessException, SuggestionNotFoundException, UserNotFoundException {
        StockSuggestion suggestion = suggestionService.getStockSuggestion(suggestionId);
        User user = userService.getUserByUsername(username);
        Comment comment = mapCommentEntity(suggestion, user, commentText);
        suggestion.getComments().add(comment);
        suggestionService.addCommentToSuggestion(suggestion);
        return mapCommentsEntityToDTO(comment);
    }

    private CommentDTO mapCommentsEntityToDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setCommentId(comment.getId());
        commentDTO.setCommentText(comment.getCommentText());
        commentDTO.setUsername(comment.getUser().getUserName());
        commentDTO.setSuggestionId(comment.getStockSuggestion().getId());
        return commentDTO;
    }

    private Comment mapCommentEntity(StockSuggestion suggestion, User user, String commentText) {
        Comment comment = new Comment();
        comment.setStockSuggestion(suggestion);
        comment.setUser(user);
        comment.setCommentText(commentText);
        comment.setCreatedAt(LocalDateTime.now());
        return comment;
    }
}
