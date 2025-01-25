package com.example.bookjourneybackend.domain.room.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetRoomSearchResponse {
    private List<RoomInfo> roomList;

    public static GetRoomSearchResponse of(List<RoomInfo> roomList) {
        return new GetRoomSearchResponse(roomList);
    }
}
