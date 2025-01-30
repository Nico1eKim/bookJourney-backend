package com.example.bookjourneybackend.domain.room.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostJoinRoomRequest {
    private Integer password;
}
