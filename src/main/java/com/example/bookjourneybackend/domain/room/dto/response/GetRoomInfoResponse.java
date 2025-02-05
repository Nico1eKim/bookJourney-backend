package com.example.bookjourneybackend.domain.room.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetRoomInfoResponse {
    private Long roomId;
    private String bookTitle;
    private String roomName;
    private boolean isPublic;
    private int roomPercentage;
    private String progressEndDate;
    private boolean isMember;
    private List<RoomMemberInfo> memberList;

    public static GetRoomInfoResponse of(Long roomId, String bookTitle, String roomName, boolean isPublic, int roomPercentage, String progressEndDate, boolean isMember, List<RoomMemberInfo> memberList) {
        return new GetRoomInfoResponse(roomId, bookTitle, roomName, isPublic, roomPercentage, progressEndDate, isMember, memberList);
    }
}