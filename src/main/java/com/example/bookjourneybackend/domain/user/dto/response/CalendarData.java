package com.example.bookjourneybackend.domain.user.dto.response;

import lombok.Builder;

@Builder
public record CalendarData(
        String date,
        String imageUrl,
        String bookTitle,
        String authorName,
        String roomType
){
}
