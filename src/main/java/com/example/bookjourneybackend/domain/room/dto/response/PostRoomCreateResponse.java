package com.example.bookjourneybackend.domain.room.dto.response;

import com.example.bookjourneybackend.domain.room.domain.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostRoomCreateResponse {
    private Long roomId;

    public static PostRoomCreateResponse of(Room room) {
        return new PostRoomCreateResponse(room.getRoomId());
    }
}
