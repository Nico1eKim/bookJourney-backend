package com.example.bookjourneybackend.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class PostUsersValidationResponse {

    private boolean verified;

    public static PostUsersValidationResponse of(boolean verified) {
        return new PostUsersValidationResponse(verified);
    }
}
