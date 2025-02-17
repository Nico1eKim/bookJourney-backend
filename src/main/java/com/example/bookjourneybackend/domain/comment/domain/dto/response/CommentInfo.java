package com.example.bookjourneybackend.domain.comment.domain.dto.response;

import com.example.bookjourneybackend.domain.comment.domain.Comment;
import com.example.bookjourneybackend.global.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@AllArgsConstructor
public class CommentInfo {
    private Long commentId;
    private Long recordId;
    private Long userId;
    private String imageUrl;
    private String nickName;
    private String createdAt;
    private String content;
    private int commentLikeCount;
    private boolean isLike;

    public static CommentInfo from(Comment comment, boolean isLiked) {
        return CommentInfo.builder()
                .commentId(comment.getCommentId())
                .recordId(comment.getRecord().getRecordId())
                .userId(comment.getUser().getUserId())
                .imageUrl(comment.getUser().getImageUrl())
                .nickName(comment.getUser().getNickname())
                .createdAt(comment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .content(comment.getContent())
                .commentLikeCount(comment.getCommentLikes().size())
                .isLike(isLiked)
                .build();
    }
}
