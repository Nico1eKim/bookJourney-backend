package com.example.bookjourneybackend.domain.user.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GetMyPageCalendarResponse {
    private List<CalendarData> calendarList;

    public GetMyPageCalendarResponse(List<CalendarData> calendarList) {
        this.calendarList = calendarList;
    }

    public static GetMyPageCalendarResponse of(List<CalendarData> calendarList) {
        return new GetMyPageCalendarResponse(calendarList);
    }
}



