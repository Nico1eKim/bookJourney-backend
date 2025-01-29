package com.example.bookjourneybackend.domain.record.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GetEntireRecordResponse {

    private List<EntireRecordInfo> recordList;

    public GetEntireRecordResponse(List<EntireRecordInfo> entireRecords) {
        this.recordList = entireRecords;
    }

    public static GetEntireRecordResponse of(List<EntireRecordInfo> entireRecords) {
        return new GetEntireRecordResponse(entireRecords);
    }
}
