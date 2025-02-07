package com.example.bookjourneybackend.domain.comment.domain.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostCommentResponse {
    private Long commentId;

    public PostCommentResponse(Long commentId) {
        this.commentId = commentId;
    }

    public static PostCommentResponse of(Long commentId) {
        return new PostCommentResponse(commentId);
    }
}
