package com.example.bookjourneybackend.domain.auth.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostAuthLoginRequest {

    /**
     * 로그인 request dto
     */

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,16}$",
            message = "영어와 숫자를 포함해서 8자 이상 16자 이내로 입력해주세요.")
    private String password;

    @Builder
    public PostAuthLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
