package com.example.bookjourneybackend.domain.room.dto.response;

import com.example.bookjourneybackend.domain.room.domain.Room;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostJoinRoomResponse {
    private Long roomId;

    public PostJoinRoomResponse(Long roomId) {
        this.roomId = roomId;
    }

    public static PostJoinRoomResponse of(Room room) {
        return new PostJoinRoomResponse(room.getRoomId());
    }
}
