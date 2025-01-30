package com.example.bookjourneybackend.domain.user.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PostUsersSignUpRequest {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9]).{8,16}$",
            message = "영어와 숫자를 포함해서 8자 이상 16자 이내로 입력해주세요.")
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

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class favoriteGenres {

        @NotBlank(message = "장르 선택은 필수입니다.")
        private String genreName;
    }
}
