package com.example.bookjourneybackend.domain.user.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostUsersNicknameValidationRequest {

    @NotBlank(message = "닉네임 입력은 필수입니다.")
    @Size(min=2, max = 20, message = "닉네임은 최소 2자 최대 20자까지 가능합니다.")
    private String nickName;

}
