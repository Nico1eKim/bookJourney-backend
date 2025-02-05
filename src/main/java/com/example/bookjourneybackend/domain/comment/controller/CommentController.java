package com.example.bookjourneybackend.domain.comment.controller;

import com.example.bookjourneybackend.domain.comment.domain.dto.response.GetCommentListResponse;
import com.example.bookjourneybackend.domain.comment.domain.dto.response.PostCommentLikeResponse;
import com.example.bookjourneybackend.domain.comment.service.CommentService;
import com.example.bookjourneybackend.global.annotation.LoginUserId;
import com.example.bookjourneybackend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{recordId}")
    public BaseResponse<GetCommentListResponse> getComments(
            @PathVariable final Long recordId,
            @LoginUserId final Long userId
    ) {
        return BaseResponse.ok(commentService.showComments(recordId, userId));
    }

    @PostMapping("/{commentId}/likes")
    public BaseResponse<PostCommentLikeResponse> likesComment(
            @PathVariable("commentId") final Long commentId,
            @LoginUserId final Long userId
    ) {
        return BaseResponse.ok(commentService.toggleCommentLike(commentId, userId));
    }
}
