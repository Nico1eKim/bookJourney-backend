package com.example.bookjourneybackend.domain.comment.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostCommentRequest {

    @NotBlank(message = "댓글 내용은 필수 입력값입니다.")
    private String content;
}
