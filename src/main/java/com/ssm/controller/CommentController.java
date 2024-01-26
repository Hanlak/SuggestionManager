package com.ssm.controller;


import com.ssm.dto.CommentDTO;
import com.ssm.exception.SuggestionNotFoundException;
import com.ssm.exception.UserNotFoundException;
import com.ssm.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
public class CommentController {


    @Autowired
    CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/suggestion/comment/{suggestionId}")
    public ResponseEntity<List<CommentDTO>> getCommentsBasedOnSuggestion(@PathVariable("suggestionId") Long suggestionId) {
        List<CommentDTO> comments = new ArrayList<>();
        try {
            comments = commentService.getCommentsBasedOnSuggestion(suggestionId);
            return new ResponseEntity<>(comments, HttpStatus.OK);
        } catch (SuggestionNotFoundException | DataAccessException e) {
            return new ResponseEntity<>(comments, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/suggestion/comment")
    public ResponseEntity<CommentDTO> addCommentForSuggestion(@RequestParam("suggestionId") Long suggestionId, @RequestParam("commentText") String commentText, Principal principal) {
        try {
            CommentDTO comment = commentService.addCommentBasedOnSuggestion(suggestionId, principal.getName(), commentText);
            return new ResponseEntity<>(comment, HttpStatus.OK);
        } catch (SuggestionNotFoundException | UserNotFoundException | DataAccessException e) {
            return new ResponseEntity<>(new CommentDTO(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
