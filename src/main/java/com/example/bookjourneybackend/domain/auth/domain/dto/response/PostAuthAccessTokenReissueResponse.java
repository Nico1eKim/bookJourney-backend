package com.example.bookjourneybackend.domain.auth.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostAuthAccessTokenReissueResponse {

    //엑세스 토큰 재발급 Response
    private String accessToken;

    public static PostAuthAccessTokenReissueResponse of(String accessToken) {
        return new PostAuthAccessTokenReissueResponse(accessToken);
    }
}
