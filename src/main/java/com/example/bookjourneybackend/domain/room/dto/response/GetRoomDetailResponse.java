package com.example.bookjourneybackend.domain.room.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetRoomDetailResponse {
    private String roomName;
    private boolean isPublic;
    private String lastActivityTime;
    private double roomPercentage;
    private String progressStartDate;
    private String progressEndDate;
    private String recruitDday;
    private String recruitEndDate;
    private int recruitCount;
    private List<RoomMemberInfo> memberList;

    public static GetRoomDetailResponse of(String roomName, boolean isPublic, String lastActivityTime, int roomPercentage,
                                           String progressStartDate, String progressEndDate, String recruitDday,
                                           String recruitEndDate, int recruitCount, List<RoomMemberInfo> memberList) {
        return new GetRoomDetailResponse(roomName, isPublic, lastActivityTime, roomPercentage, progressStartDate,
                progressEndDate, recruitDday, recruitEndDate, recruitCount, memberList);
    }
}
