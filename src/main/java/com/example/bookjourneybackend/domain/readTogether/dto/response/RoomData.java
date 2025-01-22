package com.example.bookjourneybackend.domain.readTogether.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RoomData {
    private Long roomId;
    private String bookTitle;
    private String roomName;
    private boolean isPublic;
    private int roomPercentage;
    private String progressEndDay;
    private int memberCount;
    private int recruitCount;
    private List<RoomMemberInfo> memberList;
}
