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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.CANNOT_FIND_ROOM;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final BookRepository bookRepository;

    public GetRoomDetailResponse showRoomDetails(Long roomId) {
        Room room = findRoomById(roomId);
        List<RoomMemberInfo> members = getRoomMemberInfoList(room);

        LocalDateTime recruitEndDate = room.getRecruitEndDate(); // recruitEndDate를 Room 객체에서 직접 가져옴
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

    private String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return date.format(formatter);
    }

    private String calculateDday(LocalDateTime endDate) {
        long days = ChronoUnit.DAYS.between(LocalDateTime.now(), endDate);
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
    public PostRoomCreateResponse createRoom(PostRoomCreateRequest postRoomCreateRequest) {
        Book book = bookRepository.findByIsbn(postRoomCreateRequest.getIsbn())
                .orElseGet(() -> saveBookFromAladinApi(postRoomCreateRequest.getIsbn()));

        return PostRoomCreateResponse.of(Room.builder()
                .roomName(postRoomCreateRequest.getRoomName())
                .book(book)
                .isPublic(postRoomCreateRequest.isPublic())
                .password(postRoomCreateRequest.getPassword())
                .startDate(postRoomCreateRequest.getProgressStartDate())    //방의 생성기간 = 방의 시작 기간 = 방의 모집 시작 기간
                .progressEndDate(postRoomCreateRequest.getProgressEndDate())
                .recruitEndDate(calculateRecruitEndDate(postRoomCreateRequest)) //방의 모집종료 기간 = {(방의 종료기간 - 방의 시작기간)/2} + 방의 시작기간
                .recruitCount(postRoomCreateRequest.getRecruitCount())
                .roomPercentage(0.0)
                .recordCount(0)
                .build());
    }

    private Book saveBookFromAladinApi(@NotBlank(message = "ISBN cannot be blank.") @Pattern(regexp = "\\d{10,13}", message = "ISBN은 10이나 13자리로 이루어집니다.") String isbn) {
        return null;
    }

    /**
     * //방의 모집종료 기간 = {(방의 종료기간 - 방의 시작기간)/2} + 방의 시작기간
     * @param postRoomCreateRequest
     * @return
     */
    private LocalDateTime calculateRecruitEndDate(PostRoomCreateRequest postRoomCreateRequest) {
        long totalDays = ChronoUnit.DAYS.between(postRoomCreateRequest.getProgressStartDate(), postRoomCreateRequest.getProgressEndDate());
        long halfDays = Math.round(totalDays / 2.0);

        return postRoomCreateRequest.getProgressStartDate().plusDays(halfDays);
    }
}
