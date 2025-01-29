package com.example.bookjourneybackend.domain.room.service;

import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.room.domain.RoomType;
import com.example.bookjourneybackend.domain.room.domain.SearchType;
import com.example.bookjourneybackend.domain.room.domain.SortType;
import com.example.bookjourneybackend.domain.room.domain.repository.RoomRepository;
import com.example.bookjourneybackend.domain.room.dto.response.*;
import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import com.example.bookjourneybackend.domain.room.dto.request.PostRoomCreateRequest;
import com.example.bookjourneybackend.domain.room.dto.response.GetRoomDetailResponse;
import com.example.bookjourneybackend.domain.room.dto.response.GetRoomInfoResponse;
import com.example.bookjourneybackend.domain.room.dto.response.PostRoomCreateResponse;
import com.example.bookjourneybackend.domain.room.dto.response.RoomMemberInfo;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.UserImage;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.domain.userRoom.domain.UserRole;
import com.example.bookjourneybackend.domain.userRoom.domain.UserRoom;
import com.example.bookjourneybackend.domain.userRoom.domain.repository.UserRoomRepository;
import com.example.bookjourneybackend.global.entity.EntityStatus;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.example.bookjourneybackend.domain.record.domain.Record;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import com.example.bookjourneybackend.global.util.AladinApiUtil;
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

import static com.example.bookjourneybackend.domain.room.domain.SortType.LASTEST;
import static com.example.bookjourneybackend.global.entity.EntityStatus.*;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;
    private final AladinApiUtil aladinApiUtil;

    public GetRoomDetailResponse showRoomDetails(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_ROOM));
        List<RoomMemberInfo> members = getRoomMemberInfoList(room);

        LocalDate recruitEndDate = room.getRecruitEndDate(); // recruitEndDate를 Room 객체에서 직접 가져옴
        String recruitDday = calculateDday(recruitEndDate); // D-day 계산

        return GetRoomDetailResponse.of(
                room.getRoomName(),
                room.isPublic(),
                calculateLastActivityTime(room.getRecords()),
                room.getRoomPercentage().intValue(),
                formatDate(room.getStartDate()),
                formatDate(room.getProgressEndDate()),
                recruitDday,
                formatDate(recruitEndDate),
                room.getRecruitCount(),
                members // DELETED가 아닌 유저들만 포함
        );

    }

    public GetRoomInfoResponse showRoomInfo(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_ROOM));
        List<RoomMemberInfo> members = getRoomMemberInfoList(room);

        return GetRoomInfoResponse.of(
                room.getBook().getBookTitle(),
                room.getRoomName(),
                room.isPublic(),
                room.getRoomPercentage().intValue(),
                calculateDday(room.getProgressEndDate()),
                members // DELETED가 아닌 유저들만 포함
        );
    }

    public GetRoomSearchResponse searchRooms(
            String searchTerm, String searchType, String genre,
            String recruitStartDate, String recruitEndDate,
            String roomStartDate, String roomEndDate,
            Integer recordCount, Integer page
    ) {
        // 필수 값 검증
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new GlobalException(EMPTY_SEARCH_TERM);
        }
        if (searchType == null || searchType.trim().isEmpty()) {
            throw new GlobalException(INVALID_SEARCH_TYPE);
        }
        if (page == null) {
            throw new GlobalException(INVALID_PAGE);
        }

        SearchType effectiveSearchType = SearchType.from(searchType);
        GenreType genreType = genre != null && !genre.isEmpty() ? GenreType.fromGenreType(genre) : null;

        Slice<Room> rooms = roomRepository.findRoomsByFilters(
                genreType,
                recruitStartDate != null ? LocalDate.parse(recruitStartDate) : null,
                recruitEndDate != null ? LocalDate.parse(recruitEndDate) : null,
                roomStartDate != null ? LocalDate.parse(roomStartDate) : null,
                roomEndDate != null ? LocalDate.parse(roomEndDate) : null,
                recordCount,
                PageRequest.of(page, 10)
        );

        List<Room> filteredRooms = rooms.stream()
                .filter(room -> room.getStatus() == EntityStatus.ACTIVE) // 상태가 ACTIVE인 방
                .filter(room -> room.getRoomType() == RoomType.TOGETHER) // 같이읽기 방만 포함
                .filter(room -> switch (effectiveSearchType) {
                    case ROOM_NAME -> room.getRoomName().contains(searchTerm);
                    case BOOK_TITLE -> room.getBook().getBookTitle().contains(searchTerm);
                    case AUTHOR_NAME -> room.getBook().getAuthorName().contains(searchTerm);
                })
                .toList();

        List<RoomInfo> roomInfos = filteredRooms.stream()
                .map(room -> new RoomInfo(
                        room.getRoomId(),
                        room.isPublic(),
                        room.getBook().getBookTitle(),
                        room.getBook().getAuthorName(),
                        room.getRoomName(),
                        room.getUserRooms().size(),
                        room.getRecruitCount(),
                        room.getRoomPercentage().intValue(),
                        formatDate(room.getStartDate()),
                        formatDate(room.getProgressEndDate())
                ))
                .collect(Collectors.toList());

        return GetRoomSearchResponse.of(roomInfos);
    }

    private List<RoomMemberInfo> getRoomMemberInfoList(Room room) {
        return room.getUserRooms().stream()
                .filter(userRoom -> userRoom.getStatus() != EntityStatus.DELETED) // DELETED 상태 제외
                .map(userRoom -> {
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

    private String calculateLastActivityTime(List<Record> records) {
        Optional<LocalDateTime> lastModifiedAtOpt = records.stream()
                .map(Record::getModifiedAt)
                .max(LocalDateTime::compareTo);

        // 수정 기록이 없는 경우
        if (lastModifiedAtOpt.isEmpty()) {
            return "기록 없음";
        }

        LocalDateTime lastModifiedAt = lastModifiedAtOpt.get();
        long minutes = Duration.between(lastModifiedAt, LocalDateTime.now()).toMinutes();

        if (minutes < 1) {
            return "방금 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else {
            long hours = minutes / 60;
            return hours + "시간 전";
        }
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
     *
     * @param postRoomCreateRequest, userId
     * @return postRoomCreateResponse
     */
    @Transactional
    public PostRoomCreateResponse createRoom(PostRoomCreateRequest postRoomCreateRequest, Long userId) {
        log.info("------------------------[RoomService.createRoom]------------------------");
        Book book = bookRepository.findByIsbn(postRoomCreateRequest.getIsbn())
                .map(existingBook -> {
                    if (existingBook.getPageCount() == null) {
                        Book updatedBook = saveBookFromAladinApi(postRoomCreateRequest.getIsbn());
                        existingBook.setPageCount(updatedBook.getPageCount());
                        return bookRepository.save(existingBook); // 기존 book 업데이트
                    }
                    return existingBook; // 기존 book 그대로 반환
                })
                .orElseGet(() -> saveBookFromAladinApi(postRoomCreateRequest.getIsbn())); // book이 없을 경우 새로 생성

        UserRoom userRoom = UserRoom.builder()
                .user(userRepository.findByUserIdAndStatus(userId, ACTIVE)
                        .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER)))
                .userRole(UserRole.HOST)
                .currentPage(0)
                .userPercentage(0.0)
                .build();

        Room room;

        if (postRoomCreateRequest.getRecruitCount() == 1) { //혼자읽기
            room = Room.makeReadAloneRoom(book);
            room.addUserRoom(userRoom);
        } else {    //같이읽기
            //String -> LocalDate 파싱
            LocalDate startDate = parseToLocalDate(postRoomCreateRequest.getProgressStartDate());
            LocalDate progressEndDate = parseToLocalDate(postRoomCreateRequest.getProgressEndDate());

            room = Room.makeReadTogetherRoom(postRoomCreateRequest.getRoomName(), book,
                    postRoomCreateRequest.isPublic(), postRoomCreateRequest.getPassword(),
                    startDate, progressEndDate, calculateRecruitEndDate(startDate, progressEndDate), postRoomCreateRequest.getRecruitCount());
            room.addUserRoom(userRoom);
        }

        book.addRoom(room);
        bookRepository.save(book);
        roomRepository.save(room); //CascadeType.All 옵션을 제거하고 room도 save (이유 : CascadeType.ALL을 했더니 roomRepository에 메서드가 종료되고 저장되어서 roomId가 null이 뜨는 현상이 발생

        //DB에 존재하는 book을 매핑할 경우 영속성 컨텍스트에만 Room이 존재하고 Repository에는 바로 저장이 안되므로 RoomId가 null로 반환되는 현상이 발생.
        //따라서 flush() 호출 또는 명시적으로 save 호출
//        roomRepository.flush();
//        bookRepository.flush();
        log.info("Created RoomID: {}", room.getRoomId());
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
     */
    private LocalDate calculateRecruitEndDate(LocalDate startDate, LocalDate progressEndDate) {
        long totalDays = ChronoUnit.DAYS.between(startDate, progressEndDate);
        long halfDays = Math.round(totalDays / 2.0);

        return startDate.plusDays(halfDays);
    }

    private LocalDate parseToLocalDate(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }

    /**
     * 최신순, 유저 진행도순 정렬
     * 방이 ACTIVE인 것만 보여줌
     */
    //todo 추후에 예외처리 메시지 수정
    @Transactional(readOnly = true)
    public GetRoomActiveResponse searchActiveRooms(String sort, Long userId) {
        log.info("------------------------[RoomService.searchActiveRooms]------------------------");
        log.info("sort: {}", sort);
        SortType sortType = (sort == null) ? LASTEST : SortType.from(sort);
        List<UserRoom> userRooms;

        switch (sortType) {
            //최신순 정렬
            case LASTEST -> userRooms = userRoomRepository.findUserRoomsOrderByModifiedAt(userId);

            //유저 진행도순 정렬
            case PROGRESS -> userRooms = userRoomRepository.findUserRoomsOrderByUserPercentage(userId);

            default -> throw new GlobalException(INVALID_SORT_TYPE);
        }

        return GetRoomActiveResponse.of(parsingUserRoomsToRecordInfo(userRooms));
    }

    private List<RecordInfo> parsingUserRoomsToRecordInfo(List<UserRoom> userRooms) {
        if (userRooms.isEmpty()) {
            throw new GlobalException(CANNOT_FOUND_BOOK);
        }

        return userRooms.stream()
                .map(userRoom -> {
                    Room room = userRoom.getRoom();
                    Book book = room.getBook();
                    return RecordInfo.builder()
                            .roomId(room.getRoomId())
                            .imageUrl(book.getImageUrl())
                            .bookTitle(book.getBookTitle())
                            .authorName(book.getAuthorName())
                            .roomType(room.getRoomType().getRoomType())
                            .modifiedAt(calculateLastActivityTime(room.getRecords()))
                            .userPercentage(userRoom.getUserPercentage())
                            .build();
                }).toList();
    }

    /**
     * 진행중인 기록 삭제
     * UserRoom의 status를 INACTIVE로 변경
     */
    @Transactional
    public Void putRoomsInactive(Long roomId, Long userId) {
        log.info("------------------------[RoomService.putRoomsInactive]------------------------");
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_ROOM));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));


        UserRoom userRoom = userRoomRepository.findUserRoomByRoomAndUserAndStatus(room, user, ACTIVE)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER_ROOM));

        userRoom.setStatus(EntityStatus.INACTIVE);
        userRoomRepository.save(userRoom);

        return null;
    }
}
