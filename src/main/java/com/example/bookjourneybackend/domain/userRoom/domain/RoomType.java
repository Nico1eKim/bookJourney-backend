package com.example.bookjourneybackend.domain.userRoom.domain;

import lombok.Getter;

@Getter
public enum RoomType {

    TOGETHER("같이읽기"), ALONE("혼자읽기");

    private String type;

    RoomType(String type) {
        this.type = type;
    }

    public static RoomType from(String type) {
        for (RoomType roomType : RoomType.values()) {
            if (roomType.getType().equals(type)) {
                return roomType;
            }
        }
        //TODO 예외 엔티티 작성
        //throw new CustomException(ErrorCode.NO_SUCH_TYPE);
        return null;
    }

}
