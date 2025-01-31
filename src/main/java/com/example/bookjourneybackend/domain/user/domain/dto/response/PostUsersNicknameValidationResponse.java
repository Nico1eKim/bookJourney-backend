package com.example.bookjourneybackend.domain.user.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class PostUsersNicknameValidationResponse {

    private boolean verified;

    public static PostUsersNicknameValidationResponse of(boolean verified) {
        return new PostUsersNicknameValidationResponse(verified);
    }
}
