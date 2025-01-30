package com.example.bookjourneybackend.domain.user.domain.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class PostUsersSignUpResponse {

    private Long userId;
    private String accessToken;
    private String refreshToken;

    public static PostUsersSignUpResponse of(Long userId, String accessToken, String refreshToken) {
        return new PostUsersSignUpResponse(userId,accessToken,refreshToken);
    }

}
