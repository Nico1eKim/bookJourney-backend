package com.example.bookjourneybackend.domain.room.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class GetRoomActiveResponse {

    private List<RecordInfo> recordList;

    public GetRoomActiveResponse(List<RecordInfo> recordList) {
        this.recordList = recordList;
    }

    public static GetRoomActiveResponse of(List<RecordInfo> recordList) {
        return new GetRoomActiveResponse(recordList);
    }
}
