package com.example.bookjourneybackend.domain.comment.domain.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GetCommentListResponse {

    private List<CommentInfo> comments;

    public GetCommentListResponse(List<CommentInfo> comments) {
        this.comments = comments;
    }

    public static GetCommentListResponse of(List<CommentInfo> comments) {
        return new GetCommentListResponse(comments);
    }
}
