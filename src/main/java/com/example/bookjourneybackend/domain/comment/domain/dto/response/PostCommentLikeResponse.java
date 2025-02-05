package com.example.bookjourneybackend.domain.comment.domain.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostCommentLikeResponse {
    private boolean isLiked;

    public PostCommentLikeResponse(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public static PostCommentLikeResponse of(boolean isLiked) {
        return new PostCommentLikeResponse(isLiked);
    }
}
