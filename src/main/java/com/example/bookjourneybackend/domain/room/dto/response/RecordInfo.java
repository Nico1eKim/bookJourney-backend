package com.example.bookjourneybackend.domain.room.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class RecordInfo {

    private final Long roomId;
    private final String imageUrl;
    private final String bookTitle;
    private final String authorName;
    private final String roomType;
    private final String modifiedAt;
    private final Double userPercentage;
}
