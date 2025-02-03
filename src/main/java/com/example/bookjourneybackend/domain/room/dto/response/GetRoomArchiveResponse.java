package com.example.bookjourneybackend.domain.room.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GetRoomArchiveResponse {

    private List<RecordInfo> recordList;

    public GetRoomArchiveResponse(List<RecordInfo> recordList) {
        this.recordList = recordList;
    }

    public static GetRoomArchiveResponse of(List<RecordInfo> recordList) {
        return new GetRoomArchiveResponse(recordList);
    }
}
