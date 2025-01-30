package com.example.bookjourneybackend.domain.record.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GetRecordResponse {

    private List<RecordInfo> recordList;

    public GetRecordResponse(List<RecordInfo> entireRecords) {
        this.recordList = entireRecords;
    }

    public static GetRecordResponse of(List<RecordInfo> entireRecords) {
        return new GetRecordResponse(entireRecords);
    }
}
