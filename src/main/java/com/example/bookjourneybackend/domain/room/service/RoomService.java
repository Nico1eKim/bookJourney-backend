package com.example.bookjourneybackend.domain.room.service;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.room.domain.repository.RoomRepository;
import com.example.bookjourneybackend.domain.room.dto.request.PostRoomCreateRequest;
import com.example.bookjourneybackend.domain.room.dto.response.GetRoomDetailResponse;
import com.example.bookjourneybackend.domain.room.dto.response.GetRoomInfoResponse;
import com.example.bookjourneybackend.domain.room.dto.response.PostRoomCreateResponse;
import com.example.bookjourneybackend.domain.room.dto.response.RoomMemberInfo;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.UserImage;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.example.bookjourneybackend.global.util.AladinApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.CANNOT_FIND_ROOM;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final BookRepository bookRepository;
    private final AladinApiUtil aladinApiUtil;

    public GetRoomDetailResponse showRoomDetails(Long roomId) {
        Room room = findRoomById(roomId);
        List<RoomMemberInfo> members = getRoomMemberInfoList(room);

        LocalDate recruitEndDate = room.getRecruitEndDate(); // recruitEndDate를 Room 객체에서 직접 가져옴
        String recruitDday = calculateDday(recruitEndDate); // D-day 계산

        return GetRoomDetailResponse.of(
                room.getRoomName(),
                room.isPublic(),
                calculateLastActivityTime(room.getLastActivityTime()),
                room.getRoomPercentage().intValue(),
                formatDate(room.getStartDate()),
                formatDate(room.getProgressEndDate()),
                recruitDday,
                formatDate(recruitEndDate),
                room.getRecruitCount(),
                members
        );

    }

    public GetRoomInfoResponse showRoomInfo(Long roomId) {
        Room room = findRoomById(roomId);
        List<RoomMemberInfo> members = getRoomMemberInfoList(room);

        return GetRoomInfoResponse.of(
                room.getBook().getBookTitle(),
                room.getRoomName(),
                room.isPublic(),
                room.getRoomPercentage().intValue(),
                calculateDday(room.getProgressEndDate()),
                room.getRecruitCount(),
                room.getRecordCount(),
                members
        );
    }

    private Room findRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(()-> new GlobalException(CANNOT_FIND_ROOM));
    }

    private List<RoomMemberInfo> getRoomMemberInfoList(Room room) {
        return room.getUserRooms().stream().map(userRoom -> {
            User user = userRoom.getUser();
            return new RoomMemberInfo(
                    userRoom.getUserRole(),
                    Optional.ofNullable(user.getUserImage())
                            .map(UserImage::getImageUrl)
                            .orElse(null),
                    user.getNickname(),
                    userRoom.getUserPercentage().intValue()
            );
        }).collect(Collectors.toList());
    }

    private String calculateLastActivityTime(LocalDateTime lastActivityTime) {
        LocalDateTime now = LocalDateTime.now();
        long hours = Duration.between(lastActivityTime, now).toHours();
        return hours + "시간 전";
    }

    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return date.format(formatter);
    }

    private String calculateDday(LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), endDate);
        if (days < 0) {
            return "D+" + Math.abs(days);
        }
        return "D-" + days;
    }

    /**
     * 1. Book 테이블에 존재하면, 그대로 매핑
     * 2. Book 테이블에 존재하지 않으면, 알라딘 api를 통해 book 정보를 가져와서 repository에 저장후 매핑
     * @param postRoomCreateRequest
     * @return
     */
    @Transactional
    public PostRoomCreateResponse createRoom(PostRoomCreateRequest postRoomCreateRequest) {
        log.info("------------------------[RoomService.createRoom]------------------------");
        Book book = bookRepository.findByIsbn(postRoomCreateRequest.getIsbn())
                .orElseGet(() -> saveBookFromAladinApi(postRoomCreateRequest.getIsbn()));

        LocalDate startDate = parseToLocalDate(postRoomCreateRequest.getProgressStartDate());
        LocalDate progressEndDate = parseToLocalDate(postRoomCreateRequest.getProgressEndDate());

        Room room = Room.builder()
                .roomName(postRoomCreateRequest.getRoomName())
                .book(book)
                .isPublic(postRoomCreateRequest.isPublic())
                .password(Integer.parseInt(postRoomCreateRequest.getPassword()))
                .startDate(startDate)    //방의 생성기간 = 방의 시작 기간 = 방의 모집 시작 기간
                .progressEndDate(progressEndDate)
                .recruitEndDate(calculateRecruitEndDate(startDate, progressEndDate)) //방의 모집종료 기간 = {(방의 종료기간 - 방의 시작기간)/2} + 방의 시작기간
                .recruitCount(postRoomCreateRequest.getRecruitCount())
                .roomPercentage(0.0)
                .recordCount(0)
                .build();

        book.addRoom(room);
        bookRepository.save(book);  //Cascade.All 옵션에 의해 room도 save
//        roomRepository.save(room);

        return PostRoomCreateResponse.of(room);
    }

    private Book saveBookFromAladinApi(String isbn) {
        log.info("[saveBookFromAladinApi] isbn: {}", isbn);

        String requestUrl = aladinApiUtil.buildLookUpApiUrl(isbn);
        String currentResponse = aladinApiUtil.requestBookInfoFromAladinApi(requestUrl);

        return aladinApiUtil.parseAladinApiResponseToBook(currentResponse);
    }

    /**
     * 방의 모집종료 기간 = {(방의 종료기간 - 방의 시작기간)/2} + 방의 시작기간
     * @param startDate
     * @param progressEndDate
     * @return
     */
    private LocalDate calculateRecruitEndDate(LocalDate startDate, LocalDate progressEndDate) {
        long totalDays = ChronoUnit.DAYS.between(startDate, progressEndDate);
        long halfDays = Math.round(totalDays / 2.0);

        return startDate.plusDays(halfDays);
    }

    private LocalDate parseToLocalDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }
}
