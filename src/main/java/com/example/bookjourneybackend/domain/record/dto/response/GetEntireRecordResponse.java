package com.example.bookjourneybackend.domain.record.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GetEntireRecordResponse {

    private List<RecordInfo> recordList;

    public GetEntireRecordResponse(List<RecordInfo> entireRecords) {
        this.recordList = entireRecords;
    }

    public static GetEntireRecordResponse of(List<RecordInfo> entireRecords) {
        return new GetEntireRecordResponse(entireRecords);
    }
}
