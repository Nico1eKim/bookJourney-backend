package com.example.bookjourneybackend.domain.user.dto.request;

import com.example.bookjourneybackend.domain.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PatchUserInfoRequest {

    private String nickName;
    private String imageUrl;

}
