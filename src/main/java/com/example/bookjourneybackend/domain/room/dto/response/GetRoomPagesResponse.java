package com.example.bookjourneybackend.domain.room.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetRoomPagesResponse {
    private int bookPage;
    private int currentPage;

    public static GetRoomPagesResponse of(int bookPage, int currentPage) {
        return new GetRoomPagesResponse(bookPage, currentPage);
    }
}
