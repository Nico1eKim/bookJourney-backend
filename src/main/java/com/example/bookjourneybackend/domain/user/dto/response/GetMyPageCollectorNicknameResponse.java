package com.example.bookjourneybackend.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetMyPageCollectorNicknameResponse {

    private String collectorNickname;
    private Integer recordCount;

    public static GetMyPageCollectorNicknameResponse of(String collectorNickname, Integer recordCount) {
        return new GetMyPageCollectorNicknameResponse(collectorNickname, recordCount);
    }
}
