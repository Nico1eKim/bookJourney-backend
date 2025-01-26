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

    private String email;
    private String password;

    @Builder
    public PostAuthLoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

}
