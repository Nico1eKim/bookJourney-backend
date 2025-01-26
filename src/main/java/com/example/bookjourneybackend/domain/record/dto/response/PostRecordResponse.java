package com.example.bookjourneybackend.domain.record.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRecordResponse {
    private Long recordId;

    public PostRecordResponse(Long recordId) {
        this.recordId = recordId;
    }

    public static PostRecordResponse of(Long recordId) {
        return new PostRecordResponse(recordId);
    }
}
