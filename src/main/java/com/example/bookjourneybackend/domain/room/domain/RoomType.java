package com.example.bookjourneybackend.domain.room.domain;

import com.example.bookjourneybackend.global.exception.GlobalException;
import lombok.Getter;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.*;

@Getter
public enum RoomType {

    TOGETHER("같이읽기"), ALONE("혼자읽기");

    private final String roomType;

    RoomType(String roomType) {
        this.roomType = roomType;
    }

    public static RoomType from(String roomType) throws GlobalException {
        for (RoomType type : RoomType.values()) {
            if (type.getRoomType().equals(roomType)) {
                return type;
            }
        }
        throw new GlobalException(INVALID_ROOM_TYPE);
    }

}
