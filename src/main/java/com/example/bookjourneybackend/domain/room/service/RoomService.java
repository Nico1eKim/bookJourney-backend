package com.example.bookjourneybackend.domain.room.service;

import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.domain.record.domain.repository.RecordRepository;
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
import com.example.bookjourneybackend.global.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import com.example.bookjourneybackend.global.util.AladinApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.bookjourneybackend.domain.room.domain.RoomType.TOGETHER;
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
    private final DateUtil dateUtil;
    private final RecordRepository recordRepository;

    /**
     * 방 상세정보 조회
     */
    public GetRoomDetailResponse showRoomDetails(Long roomId) {
        log.info("------------------------[RoomService.showRoomDetails]------------------------");

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_ROOM));
        List<RoomMemberInfo> members = getRoomMemberInfoList(room);

        return GetRoomDetailResponse.of(
                room.getRoomName(),
                room.isPublic(),
                dateUtil.calculateLastActivityTime(room.getRecords()),
                room.getRoomPercentage().intValue(),
                dateUtil.formatDate(room.getStartDate()),
                dateUtil.formatDate(room.getProgressEndDate()),
                dateUtil.calculateDday(room.getRecruitEndDate()),   // D-day 계산
                dateUtil.formatDate(room.getRecruitEndDate()),
                room.getRecruitCount(),
                members // DELETED가 아닌 유저들만 포함
        );

    }

    /**
     * 방 정보 조회
     */
    public GetRoomInfoResponse showRoomInfo(Long roomId) {
        log.info("------------------------[RoomService.showRoomInfo]------------------------");

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_ROOM));
        List<RoomMemberInfo> members = getRoomMemberInfoList(room);

        return GetRoomInfoResponse.of(
                room.getBook().getBookTitle(),
                room.getRoomName(),
                room.isPublic(),
                room.getRoomPercentage().intValue(),
                dateUtil.calculateDday(room.getProgressEndDate()),
                members // DELETED가 아닌 유저들만 포함
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
        log.info("------------------------[RoomService.searchRooms]------------------------");

        validateSearchParams(searchTerm, searchType, page);

        SearchType effectiveSearchType = SearchType.from(searchType);
        GenreType genreType = genre != null && !genre.isEmpty() ? GenreType.fromGenreType(genre) : null;

        Slice<Room> rooms = roomRepository.findRoomsByFilters(
                genreType,
                dateUtil.parseDate(recruitStartDate),
                dateUtil.parseDate(recruitEndDate),
                dateUtil.parseDate(roomStartDate),
                dateUtil.parseDate(roomEndDate),
                recordCount,
                PageRequest.of(page, 10)
        );

        List<RoomInfo> roomInfos = rooms.stream()
                .filter(room -> room.getStatus() == ACTIVE) // 상태가 ACTIVE인 방
                .filter(room -> room.getRoomType() == TOGETHER) // 같이읽기 방만 포함
                .filter(room -> filterRooms(room, effectiveSearchType, searchTerm))
                .map(this::mapRoomToRoomInfo)
                .toList();

        return GetRoomSearchResponse.of(roomInfos);
    }

    private void validateSearchParams(String searchTerm, String searchType, Integer page) {
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
                .progressStartDate(dateUtil.formatDate(room.getStartDate()))
                .progressEndDate(dateUtil.formatDate(room.getProgressEndDate()))
                .build();
    }

    //Room 객체의 UserRoom 정보를 RoomMemberInfo 객체로 매핑
    private List<RoomMemberInfo> getRoomMemberInfoList(Room room) {
        return room.getUserRooms().stream()
                .filter(userRoom -> userRoom.getStatus() != DELETED) // DELETED 상태 제외
                .map(userRoom -> {
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

        return aladinApiUtil.parseAladinApiResponseToBook(currentResponse,false,false);
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
            LocalDate startDate = dateUtil.parseToLocalDate(request.getProgressStartDate());
            LocalDate progressEndDate = dateUtil.parseToLocalDate(request.getProgressEndDate());
            room = Room.makeReadTogetherRoom(
                    request.getRoomName(), book, request.isPublic(), request.getPassword(),
                    startDate, progressEndDate, dateUtil.calculateRecruitEndDate(startDate, progressEndDate), request.getRecruitCount()
            );
        }
        room.addUserRoom(userRoom);
        return room;
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
                            .modifiedAt(dateUtil.calculateLastActivityTime(room.getRecords()))
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
        UserRoom userRoom = getUserRoom(roomId, userId);

        userRoom.setStatus(EntityStatus.INACTIVE);
        userRoomRepository.save(userRoom);

        return null;
    }

    private UserRoom getUserRoom(Long roomId, Long userId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_ROOM));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));


        UserRoom userRoom = userRoomRepository.findUserRoomByRoomAndUserAndStatus(room, user, ACTIVE)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER_ROOM));
        return userRoom;
    }

    /**
     * 혼자읽기일 경우 무조건 Room 삭제
     * 같이읽기일 경우 UserRole= Member면 -> Room 삭제 X
     * 같이읽기일 경우 UserRole= Host면 -> 자신만 남았다면 나갈수 있고 Room 삭제
     */
    @Transactional
    public Void exitRoom(Long roomId, Long userId) {
        log.info("------------------------[RoomService.exitRoom]------------------------");

        UserRoom userRoom = getUserRoom(roomId, userId);
        Room room = userRoom.getRoom();

        if (room.getRoomType() == TOGETHER) {
            handleTogetherRoomExit(userRoom, room);

        } else {
            roomRepository.delete(room);    // 혼자읽기 방은 나가면 방 삭제
        }

        return null;
    }

    private void handleTogetherRoomExit(UserRoom userRoom, Room room) {
        if (userRoom.getUserRole() == UserRole.HOST) {
            removeHostFromRoom(room);
        } else {
            removeMemberFromRoom(userRoom, room);
        }
    }

    private void removeHostFromRoom(Room room) {
        if (room.getUserRooms().size() == 1) {
            roomRepository.delete(room);    // 같이읽기 방에서 방장이 혼자 남아 있는 있을때 방을 나가면 방 삭제
        } else {
            throw new GlobalException(HOST_CANNOT_LEAVE_ROOM);  // 같이읽기 방에서 방장이 혼자 남아 있지 않으면 방장은 나갈 수 없음
        }
    }

    private void removeMemberFromRoom(UserRoom userRoom, Room room) {
        recordRepository.deleteAllByRoomAndUser(room, userRoom.getUser());
        userRoomRepository.delete(userRoom);    // 같이읽기 방에서 멤버가 나가도 방 삭제 X (해당 사용자와 관련된 방 정보 삭제)
    }



    /**
     * 방 참여
     */
    @Transactional
    public PostJoinRoomResponse joinRoom(Long roomId, Long userId, Integer password) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_ROOM));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        // 이미 방에 참여한 유저인지 확인
        if (userRoomRepository.existsByRoomAndUser(room, user)) {
            throw new GlobalException(ALREADY_JOINED_ROOM);
        }

        // 모집 기간이 지났는지 확인
        if (LocalDate.now().isAfter(room.getRecruitEndDate())) {
            throw new GlobalException(ROOM_NOT_RECRUITING);
        }

        // 인원 초과 여부 확인
        if (room.getUserRooms().size() >= room.getRecruitCount()) {
            throw new GlobalException(ROOM_FULL);
        }

        // 비공개 방의 경우 비밀번호 확인
        if (!room.isPublic() && !Objects.equals(room.getPassword(), password)) {
            throw new GlobalException(INVALID_ROOM_PASSWORD);
        }

        UserRoom userRoom = UserRoom.builder()
                .user(user)
                .room(room)
                .userRole(UserRole.MEMBER)
                .isMember(true)
                .userPercentage(0.0)
                .currentPage(0)
                .build();
        userRoomRepository.save(userRoom);

        return PostJoinRoomResponse.of(room);
    }
}
