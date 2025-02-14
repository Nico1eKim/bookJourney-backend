package com.example.bookjourneybackend.domain.room.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GetRoomArchiveResponse {

    private String userCreatedAt;
    private List<RecordInfo> recordList;

    public GetRoomArchiveResponse(String userCreatedAt, List<RecordInfo> recordList) {
        this.userCreatedAt = userCreatedAt;
        this.recordList = recordList;
    }

    public static GetRoomArchiveResponse of(String userCreatedAt, List<RecordInfo> recordList) {
        return new GetRoomArchiveResponse(userCreatedAt, recordList);
    }
}
