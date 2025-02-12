package com.example.bookjourneybackend.domain.room.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GetRoomArchiveResponse {

    private String nickName;
    private List<RecordInfo> recordList;

    public GetRoomArchiveResponse(String nickName, List<RecordInfo> recordList) {
        this.nickName = nickName;
        this.recordList = recordList;
    }

    public static GetRoomArchiveResponse of(String nickName, List<RecordInfo> recordList) {
        return new GetRoomArchiveResponse(nickName, recordList);
    }
}
