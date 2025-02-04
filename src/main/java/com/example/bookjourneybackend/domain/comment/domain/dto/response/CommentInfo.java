package com.example.bookjourneybackend.domain.comment.domain.dto.response;

import com.example.bookjourneybackend.domain.comment.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CommentInfo {
    private Long commentId;
    private Long recordId;
    private Long userId;
    private String imageUrl;
    private String nickName;
    private String content;
    private int commentLikeCount;
    private boolean isLike;

    public static CommentInfo from(Comment comment, boolean isLiked) {
        return CommentInfo.builder()
                .commentId(comment.getCommentId())
                .recordId(comment.getRecord().getRecordId())
                .userId(comment.getUser().getUserId())
                .imageUrl(comment.getUser().getUserImage() != null ? comment.getUser().getUserImage().toString() : null)
                .nickName(comment.getUser().getNickname())
                .content(comment.getContent())
                .commentLikeCount(comment.getCommentLikes().size())
                .isLike(isLiked)
                .build();
    }
}
