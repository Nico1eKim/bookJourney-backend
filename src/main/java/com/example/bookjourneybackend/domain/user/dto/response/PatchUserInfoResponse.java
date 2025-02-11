package com.example.bookjourneybackend.domain.user.dto.response;

import com.example.bookjourneybackend.domain.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PatchUserInfoResponse {
    private String imageUrl;
    private String nickname;

    public static PatchUserInfoResponse of(User user) {
        return new PatchUserInfoResponse(user.getImageUrl(), user.getNickname());
    }
}
