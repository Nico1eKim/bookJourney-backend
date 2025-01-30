package com.example.bookjourneybackend.domain.user.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostUsersSignUpRequest {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String email;

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    private String password;

    @NotBlank(message = "닉네임 입력은 필수입니다.")
    private String nickName;

    @NotBlank(message = "프로필 사진은 필수입니다.")
    private String imageUrl;

    private List<favoriteGenres> favoriteGenres;

    @Builder
    public PostUsersSignUpRequest(String email, String password, String nickName, String imageUrl, List<favoriteGenres> favoriteGenres) {
        this.email = email;
        this.password = password;
        this.nickName = nickName;
        this.imageUrl = imageUrl;
        this.favoriteGenres = favoriteGenres;
    }

}
