package com.example.bookjourneybackend.domain.room.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class RecordInfo {

    private final Long roomId;
    private final String imageUrl;
    private final String bookTitle;
    private final String authorName;
    private final String roomType;
    private final String roomName;

    private String modifiedAt;
    private Double userPercentage;

    private String roomDate;


}
