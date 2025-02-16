package com.example.bookjourneybackend.domain.record.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRecordResponse {
    private Long recordId;
    private Integer recordCount;

    public PostRecordResponse(Long recordId, Integer recordCount) {
        this.recordId = recordId;
        this.recordCount = recordCount;
    }

    public static PostRecordResponse of(Long recordId, Integer recordCount) {
        return new PostRecordResponse(recordId, recordCount);
    }
}
