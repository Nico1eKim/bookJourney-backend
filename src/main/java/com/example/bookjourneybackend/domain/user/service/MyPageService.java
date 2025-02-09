package com.example.bookjourneybackend.domain.user.service;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.room.domain.repository.RoomRepository;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.domain.user.dto.response.CalendarData;
import com.example.bookjourneybackend.domain.user.dto.response.GetMyPageCalendarResponse;
import com.example.bookjourneybackend.domain.userRoom.domain.repository.UserRoomRepository;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.example.bookjourneybackend.global.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_USER;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;
    private final DateUtil dateUtil;

    /**
     * 마이페이지 캘린더 조회
     * UserRoom에서 userPercentage가 100%인 데이터 조회
     * 파라미터로 넘어온 month와 year와 completedUserPercentageAt이 같은 데이터 조회
     * 같은 날짜의 completedUserPercentageAt이 있으면 가장 최근에 완료된 UserRoom의 책 이미지를 가져와서 반환
     */
    public GetMyPageCalendarResponse showMyPageCalendar(Long userId, Integer month, Integer year) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        if(month == null) {
            month = LocalDate.now().getMonthValue();
        }
        if(year == null) {
            year = LocalDate.now().getYear();
        }


        return GetMyPageCalendarResponse.of(parseUserRoomToCalendarData(userId, month, year));
    }

    private List<CalendarData> parseUserRoomToCalendarData(Long userId, Integer month, Integer year) {
        // UserRoom의 userPercentage가 100%인 Room 중에 현재 month와 completedUserPercentageAt이 같은 것을 조회
        List<CalendarData> calendarDataList = userRoomRepository.findUserRoomsByUserInCalendar(userId, year, month)
                .stream()
                .map(userRoom -> {
                    LocalDate localDate = userRoom.getCompletedUserPercentageAt().toLocalDate();
                    Book book = userRoom.getRoom().getBook();
                    return CalendarData.builder()
                            .date(dateUtil.formatDate(localDate))
                            .imageUrl(book.getImageUrl())
                            .build();
                }).collect(Collectors.toList());
        return calendarDataList;
    }
}
