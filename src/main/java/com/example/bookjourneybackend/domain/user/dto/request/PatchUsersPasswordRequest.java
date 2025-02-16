package com.example.bookjourneybackend.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PatchUsersPasswordRequest {

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    private String currentPassword;

    @NotBlank(message = "새로운 비밀번호 입력은 필수입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,16}$",
            message = "영어와 숫자를 포함해서 8자 이상 16자 이내로 입력해주세요.")
    private String newPassword;

    @Builder
    public PatchUsersPasswordRequest(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }
}