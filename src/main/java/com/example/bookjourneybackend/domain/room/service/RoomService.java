package com.example.bookjourneybackend.domain.room.service;

import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.domain.room.domain.Room;
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

    /**
     * 방 상세정보 조회
     */
    public GetRoomDetailResponse showRoomDetails(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_ROOM));
        List<RoomMemberInfo> members = getRoomMemberInfoList(room);

        // recruitEndDate를 Room 객체에서 직접 가져옴
        String recruitDday = calculateDday(room.getRecruitEndDate()); // D-day 계산

        return GetRoomDetailResponse.of(
                room.getRoomName(),
                room.isPublic(),
                calculateLastActivityTime(room.getRecords()),
                room.getRoomPercentage().intValue(),
                formatDate(room.getStartDate()),
                formatDate(room.getProgressEndDate()),
                recruitDday,
                formatDate(room.getRecruitEndDate()),
                room.getRecruitCount(),
                members
        );

    }

    /**
     * 방 정보 조회
     */
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
                members
        );
    }

    /**
     * 방 검색
     */
    public GetRoomSearchResponse searchRooms(
            String searchTerm, String searchType, String genre,
            String recruitStartDate, String recruitEndDate,
            String roomStartDate, String roomEndDate,
            Integer recordCount, Integer page
    ) {
        validateSearchParams(searchTerm, searchType, page);

        SearchType effectiveSearchType = SearchType.from(searchType);
        GenreType genreType = genre != null && !genre.isEmpty() ? GenreType.fromGenreType(genre) : null;

        Slice<Room> rooms = roomRepository.findRoomsByFilters(
                genreType,
                parseDate(recruitStartDate),
                parseDate(recruitEndDate),
                parseDate(roomStartDate),
                parseDate(roomEndDate),
                recordCount,
                PageRequest.of(page, 10)
        );

        List<RoomInfo> roomInfos = rooms.stream()
                .filter(room ->  filterRooms(room, effectiveSearchType, searchTerm))
                .map(this::mapRoomToRoomInfo)
                .toList();

        return GetRoomSearchResponse.of(roomInfos);
    }

    private static void validateSearchParams(String searchTerm, String searchType, Integer page) {
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
    }

    //문자열을 LocalDate로 변환
    private LocalDate parseDate(String date) {
        return date != null ? LocalDate.parse(date) : null;
    }

    //검색 조건에 따라 방 필터링
    private boolean filterRooms(Room room, SearchType searchType, String searchTerm) {
        return switch (searchType) {
            case ROOM_NAME -> room.getRoomName().contains(searchTerm);
            case BOOK_TITLE -> room.getBook().getBookTitle().contains(searchTerm);
            case AUTHOR_NAME -> room.getBook().getAuthorName().contains(searchTerm);
        };
    }

    //Room 객체를 RoomInfo 객체로 매핑
    private RoomInfo mapRoomToRoomInfo(Room room) {
        return RoomInfo.builder()
                .roomId(room.getRoomId())
                .isPublic(room.isPublic())
                .bookTitle(room.getBook().getBookTitle())
                .authorName(room.getBook().getAuthorName())
                .roomName(room.getRoomName())
                .memberCount(room.getUserRooms().size())
                .recruitCount(room.getRecruitCount())
                .roomPercentage(room.getRoomPercentage().intValue())
                .progressStartDate(formatDate(room.getStartDate()))
                .progressEndDate(formatDate(room.getProgressEndDate()))
                .build();
    }

    //Room 객체의 UserRoom 정보를 RoomMemberInfo 객체로 매핑
    private List<RoomMemberInfo> getRoomMemberInfoList(Room room) {
        return room.getUserRooms().stream().map(userRoom -> {
            User user = userRoom.getUser();
            return RoomMemberInfo.builder()
                    .userRole(userRoom.getUserRole())
                    .imageUrl(Optional.ofNullable(user.getUserImage())
                            .map(UserImage::getImageUrl)
                            .orElse(null))
                    .nickName(user.getNickname())
                    .userPercentage(userRoom.getUserPercentage().intValue())
                    .build();
        }).collect(Collectors.toList());
    }

    //마지막 활동 시간 계산
    private String calculateLastActivityTime(List<Record> records) {
        return records.stream()
                .map(Record::getModifiedAt)
                .max(LocalDateTime::compareTo)
                .map(this::formatLastActivityTime)
                .orElse("기록 없음");
    }

    //마지막 활동 시간 포맷팅
    private String formatLastActivityTime(LocalDateTime lastModifiedAt) {
        long minutes = Duration.between(lastModifiedAt, LocalDateTime.now()).toMinutes();
        if (minutes < 1) return "방금 전";
        if (minutes < 60) return minutes + "분 전";
        return (minutes / 60) + "시간 전";
    }

    //날짜 포맷팅
    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return date.format(formatter);
    }

    //D-day 계산
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
        Book book = findOrCreateBook(postRoomCreateRequest.getIsbn());

        UserRoom userRoom = createUserRoom(userId);
        Room room = createRoom(postRoomCreateRequest, book, userRoom);

        book.addRoom(room);
        bookRepository.save(book);
        roomRepository.save(room); //CascadeType.All 옵션을 제거하고 room도 save (이유 : CascadeType.ALL을 했더니 roomRepository에 메서드가 종료되고 저장되어서 roomId가 null이 뜨는 현상이 발생

        log.info("Created RoomID: {}", room.getRoomId());
        return PostRoomCreateResponse.of(room);
    }

    //isbn을 통해 book을 찾거나, 없을 경우 알라딘 api를 통해 book 정보를 가져와서 저장
    private Book findOrCreateBook(String isbn) {
        return bookRepository.findByIsbn(isbn)
                .map(this::updateBookIfNeeded)
                .orElseGet(() -> saveBookFromAladinApi(isbn));
    }

    //책의 페이지수가 없을 경우, 알라딘 api를 통해 페이지수를 가져와서 업데이트
    private Book updateBookIfNeeded(Book existingBook) {
        if (existingBook.getPageCount() == null) {
            Book updatedBook = saveBookFromAladinApi(existingBook.getIsbn());
            existingBook.setPageCount(updatedBook.getPageCount());
            return bookRepository.save(existingBook);
        }
        return existingBook;
    }

    private Book saveBookFromAladinApi(String isbn) {
        log.info("[saveBookFromAladinApi] isbn: {}", isbn);

        String requestUrl = aladinApiUtil.buildLookUpApiUrl(isbn);
        String currentResponse = aladinApiUtil.requestBookInfoFromAladinApi(requestUrl);

        return aladinApiUtil.parseAladinApiResponseToBook(currentResponse);
    }

    //유저 정보를 통해 UserRoom 객체 생성
    private UserRoom createUserRoom(Long userId) {
        User user = userRepository.findByUserIdAndStatus(userId, ACTIVE)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));
        return UserRoom.builder()
                .user(user)
                .userRole(UserRole.HOST)
                .currentPage(0)
                .userPercentage(0.0)
                .build();
    }

    //방 생성
    private Room createRoom(PostRoomCreateRequest request, Book book, UserRoom userRoom) {
        Room room;
        if (request.getRecruitCount() == 1) {
            room = Room.makeReadAloneRoom(book);
        } else {
            LocalDate startDate = parseToLocalDate(request.getProgressStartDate());
            LocalDate progressEndDate = parseToLocalDate(request.getProgressEndDate());
            room = Room.makeReadTogetherRoom(
                    request.getRoomName(), book, request.isPublic(), request.getPassword(),
                    startDate, progressEndDate, calculateRecruitEndDate(startDate, progressEndDate), request.getRecruitCount()
            );
        }
        room.addUserRoom(userRoom);
        return room;
    }

    /**
     * 방의 모집종료 기간 = {(방의 종료기간 - 방의 시작기간)/2} + 방의 시작기간
     *
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
        SortType sortType = (sort == null)? LASTEST : SortType.from(sort);
        List<UserRoom> userRooms = findUserRoomsBySortType(sortType, userId);

        return GetRoomActiveResponse.of(parsingUserRoomsToRecordInfo(userRooms));
    }

    //정렬 타입에 따라 UserRoom 조회
    private List<UserRoom> findUserRoomsBySortType(SortType sortType, Long userId) {
        return switch (sortType) {
            case LASTEST -> userRoomRepository.findUserRoomsOrderByModifiedAt(userId);
            case PROGRESS -> userRoomRepository.findUserRoomsOrderByUserPercentage(userId);
            default -> throw new GlobalException(INVALID_SORT_TYPE);
        };
    }

    //UserRoom을 RecordInfo로 매핑
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
