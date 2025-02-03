package com.example.bookjourneybackend.domain.record.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRecordPageResponse {
    private Integer currentPage;

    public PostRecordPageResponse(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public static PostRecordPageResponse of(Integer currentPage) {
        return new PostRecordPageResponse(currentPage);
    }
}
