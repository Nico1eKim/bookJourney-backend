package com.example.bookjourneybackend.domain.user.service;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.domain.user.dto.request.PatchUserInfoRequest;
import com.example.bookjourneybackend.domain.user.dto.response.CalendarData;
import com.example.bookjourneybackend.domain.user.dto.response.GetMyPageCalendarResponse;
import com.example.bookjourneybackend.domain.user.dto.response.GetMyPageUserInfoResponse;
import com.example.bookjourneybackend.domain.user.dto.response.PatchUserInfoResponse;
import com.example.bookjourneybackend.domain.userRoom.domain.repository.UserRoomRepository;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.example.bookjourneybackend.global.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_USER;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.INVALID_DATE;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;
    private final DateUtil dateUtil;
    private final S3Service s3Service;

    /**
     * 마이페이지 캘린더 조회
     * UserRoom에서 userPercentage가 100%인 데이터 조회
     * 파라미터로 넘어온 month와 year와 completedUserPercentageAt이 같은 데이터 조회
     * 같은 날짜의 completedUserPercentageAt이 있으면 가장 최근에 완료된 UserRoom의 책 이미지를 가져와서 반환
     */
    public GetMyPageCalendarResponse showMyPageCalendar(Long userId, Integer month, Integer year) {
        if (month == null) {
            month = LocalDate.now().getMonthValue();
        }
        if (year == null) {
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

    /**
     * 독서달력에서 날짜하나 눌렀을 경우 해당 날짜에 종료된 모든 방 반환
     */
    public GetMyPageCalendarResponse showMyPageCalendarInfo(Long userId, Integer month, Integer year, Integer day) {
        if (month == null || year == null || day == null) {
            throw new GlobalException(INVALID_DATE);
        }

        List<CalendarData> calendarDataInfoList = userRoomRepository.findUserRoomsByUserInCalendarInfo(userId, year, month, day)
                .stream()
                .map(userRoom -> {
                    LocalDate localDate = userRoom.getCompletedUserPercentageAt().toLocalDate();
                    Room room = userRoom.getRoom();
                    Book book = room.getBook();
                    return CalendarData.builder()
                            .date(dateUtil.formatDateRange(room.getStartDate(), localDate))
                            .roomType(room.getRoomType().getRoomType())
                            .bookTitle(book.getBookTitle())
                            .authorName(book.getAuthorName())
                            .imageUrl(book.getImageUrl())
                            .build();
                }).toList();
        return GetMyPageCalendarResponse.of(calendarDataInfoList);
    }

    /**
     * 마이페이지 처음 진입했을 때 유저 정보를 반환
     */
    public GetMyPageUserInfoResponse showMyPageUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        return GetMyPageUserInfoResponse.of(user);
    }

    /**
     * 프로필 수정
     * 프로필 사진 -> 유저의 기존 사진이 동일한 유저가있다면 s3 삭제 XX 없다면 기존 사진 삭제 후 프로필 이미지 변경
     * 닉네임 변경 -> 닉네임 변경
     */
    @Transactional
    public PatchUserInfoResponse updateMyPageProfile(PatchUserInfoRequest patchUserInfoRequest,Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        //프로필 이미지 변경
        if (!patchUserInfoRequest.getImageUrl().isBlank()) {
            if (!userRepository.existsByImageUrlAndUserIdNot(user.getImageUrl(), userId))
                s3Service.deleteImageFromS3(user.getImageUrl());
            user.setImageUrl(patchUserInfoRequest.getImageUrl());
        }
        //닉네임 변경
        if(!patchUserInfoRequest.getNickName().isBlank()){
            user.setNickname(patchUserInfoRequest.getNickName());
        }
        userRepository.save(user);

        return PatchUserInfoResponse.of(user);
    }
}
