package com.example.bookjourneybackend.domain.auth.domain.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class PostAuthLoginResponse {

    /**
     * 로그인 response dto
     */

    private Long userId;
    private String accessToken;
    private String refreshToken;

    public static PostAuthLoginResponse of(Long userId,String accessToken,String refreshToken) {
        return new PostAuthLoginResponse(userId,accessToken,refreshToken);
    }


}
