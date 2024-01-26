package com.ssm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private long commentId;
    private long suggestionId;
    private String username;
    private String commentText;
    private LocalDateTime commentTime;
}
