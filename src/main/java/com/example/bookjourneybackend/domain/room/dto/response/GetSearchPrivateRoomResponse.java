package com.example.bookjourneybackend.domain.room.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetSearchPrivateRoomResponse {
    private Long roomId;
    private String roomName;
    private String hostName;
    private Integer password;

    public static GetSearchPrivateRoomResponse of(Long roomId, String roomName, String hostName, Integer password) {
        return new GetSearchPrivateRoomResponse(roomId, roomName, hostName, password);
    }
}
