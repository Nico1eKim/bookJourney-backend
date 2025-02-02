package com.example.bookjourneybackend.domain.room.dto.response;

import java.util.List;

public record GetRoomRecruitmentResponse(
    String weekOfMonth,
    List<RoomInfo> roomList
) {
    public static GetRoomRecruitmentResponse of(String weekOfMonth, List<RoomInfo> roomList) {
        return new GetRoomRecruitmentResponse(weekOfMonth, roomList);
    }
}
