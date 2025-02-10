package com.example.bookjourneybackend.domain.user.dto.response;

import com.example.bookjourneybackend.domain.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetMyPageUserInfoResponse {
    private String imageUrl;
    private String nickname;
    private String email;

    public static GetMyPageUserInfoResponse of(User user) {
        return new GetMyPageUserInfoResponse(user.getImageUrl(), user.getNickname(), user.getEmail());
    }
}
