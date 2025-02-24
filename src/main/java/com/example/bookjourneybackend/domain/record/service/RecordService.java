package com.example.bookjourneybackend.domain.record.service;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.record.domain.RecordLike;
import com.example.bookjourneybackend.domain.record.domain.RecordSortType;
import com.example.bookjourneybackend.domain.record.domain.Record;
import com.example.bookjourneybackend.domain.record.domain.RecordType;
import com.example.bookjourneybackend.domain.record.domain.repository.RecordLikeRepository;
import com.example.bookjourneybackend.domain.record.domain.repository.RecordRepository;
import com.example.bookjourneybackend.domain.record.dto.request.PostRecordRequest;
import com.example.bookjourneybackend.domain.record.dto.response.PostRecordPageResponse;
import com.example.bookjourneybackend.domain.record.dto.response.PostRecordLikeResponse;
import com.example.bookjourneybackend.domain.record.dto.response.RecordInfo;
import com.example.bookjourneybackend.domain.record.dto.response.GetRecordResponse;
import com.example.bookjourneybackend.domain.record.dto.response.PostRecordResponse;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.room.domain.repository.RoomRepository;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.domain.userRoom.domain.UserRoom;
import com.example.bookjourneybackend.domain.userRoom.domain.repository.UserRoomRepository;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.example.bookjourneybackend.global.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.bookjourneybackend.domain.record.domain.RecordSortType.PAGE_ORDER;
import static com.example.bookjourneybackend.domain.room.domain.RoomType.ALONE;
import static com.example.bookjourneybackend.global.entity.EntityStatus.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.bookjourneybackend.domain.record.domain.RecordSortType.LATEST;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordService {
    private final RecordRepository recordRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;
    private final RecordLikeRepository recordLikeRepository;
    private final DateUtil dateUtil;

    @Transactional
    public PostRecordResponse createRecord(PostRecordRequest postRecordRequest, Long roomId, Long userId) {

        Room room = roomRepository.findById(roomId).orElseThrow(() -> new GlobalException(CANNOT_FOUND_ROOM));
        User user = userRepository.findById(userId).orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));
        UserRoom userRoom = userRoomRepository.findUserRoomByRoomAndUser(room, user).orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER_ROOM));

        // UserRoom이 INACTIVE 상태이면 ACTIVE로 변경
        if (userRoom.getStatus() == INACTIVE) {
            userRoom.setStatus(ACTIVE);
            userRoomRepository.save(userRoom);
        }

        // 방이 EXPIRED 상태이면 기록을 남길 수 없음
        if (room.getStatus() == EXPIRED) {
            throw new GlobalException(CANNOT_WRITE_IN_EXPIRED_ROOM);
        }

        RecordType recordType = RecordType.from(postRecordRequest.getRecordType());
        validateRecordRequest(postRecordRequest, recordType);

        Book book = room.getBook();
        int totalPages = book.getPageCount();

        //  페이지 기록일 때 입력한 페이지 가 책의 페이지 수보다 크면 오류 발생
        if (recordType == RecordType.PAGE && postRecordRequest.getRecordPage() > totalPages) {
            throw new GlobalException(INVALID_PAGE_NUMBER);
        }

        Record newRecord = Record.builder()
                .room(room)
                .user(user)
                .recordType(recordType)
                .recordTitle(postRecordRequest.getRecordTitle())
                .recordPage(postRecordRequest.getRecordPage())
                .content(postRecordRequest.getContent())
                .build();

        recordRepository.save(newRecord);

        // 기록 개수 조회
        int recordCount = userRepository.countRecordsByUserId(userId);

        return PostRecordResponse.of(newRecord.getRecordId(), recordCount);

    }

    /**
     * 특정 방(roomId)의 전체 기록 조회
     */
    @Transactional(readOnly = true)
    public GetRecordResponse showEntireRecords(Long roomId, Long userId, String sortType) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        RecordSortType recordSortType = (sortType == null) ? LATEST : RecordSortType.from(sortType);

        List<Record> records = findRecordsByRoomId(roomId, recordSortType)
                .stream()
                .filter(record -> record.getRecordType().isEntireRecord())
                .collect(Collectors.toList());

        List<RecordInfo> recordInfoList = parseEntireRecordsToResponse(records, user);
        return GetRecordResponse.of(recordInfoList);
    }

    /**
     * 특정 방(roomId)의 페이지 별 기록 조회
     */
    @Transactional(readOnly = true)
    public GetRecordResponse showPageRecords(Long roomId, Long userId, String sortType, Integer pageStart, Integer pageEnd) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        RecordSortType recordSortType = (sortType == null) ? PAGE_ORDER : RecordSortType.from(sortType);

        List<Record> records = findRecordsByRoomId(roomId, recordSortType).stream()
                .filter(record -> record.getRecordType().isPageRecord())
                .filter(record -> isWithinPageRange(record, pageStart, pageEnd))
                .collect(Collectors.toList());

        List<RecordInfo> recordInfoList = parsePageRecordsToResponse(records, user);
        return GetRecordResponse.of(recordInfoList);
    }

    /**
     * 기록의 페이지가 주어진 범위(pageStart, pageEnd) 내에 있는지 확인
     */
    private boolean isWithinPageRange(Record record, Integer pageStart, Integer pageEnd) {
        Integer recordPage = record.getRecordPage();
        return recordPage != null &&
                (pageStart == null || recordPage >= pageStart) &&
                (pageEnd == null || recordPage <= pageEnd);
    }

    /**
     * roomId와 기록의 정렬 순서로 기록 찾기
     */
    private List<Record> findRecordsByRoomId(Long roomId, RecordSortType sortType) {
        Optional<List<Record>> records = switch (sortType) {
            case LATEST -> recordRepository.findRecordsOrderByLatest(roomId, sortType);
            case MOST_COMMENTS -> recordRepository.findRecordsOrderByMostComments(roomId, sortType);
            case PAGE_ORDER -> recordRepository.findRecordsOrderByPage(roomId, sortType);
            default -> throw new GlobalException(INVALID_RECORD_SORT_TYPE);
        };

        return records.orElseThrow(() -> new GlobalException(CANNOT_FOUND_RECORD));
    }

    private List<RecordInfo> parseEntireRecordsToResponse(List<Record> records, User user) {
        return records.stream()
                .map(record -> {
                    boolean isLiked = recordLikeRepository.existsByRecordAndUser(record, user);
                    return RecordInfo.fromEntireRecord(
                            record.getUser().getUserId(),
                            record.getRecordId(),
                            (record.getUser().getImageUrl()),
                            record.getUser().getNickname(),
                            record.getRecordTitle(),
                            dateUtil.formatLocalDateTime(record.getCreatedAt()),
                            record.getContent(),
                            record.getComments().size(),
                            record.getRecordLikes().size(),
                            isLiked
                    );
                }).collect(Collectors.toList());
    }

    private List<RecordInfo> parsePageRecordsToResponse(List<Record> records, User user) {
        return records.stream()
                .map(record -> {
                    boolean isLiked = recordLikeRepository.existsByRecordAndUser(record, user);
                    return RecordInfo.fromPageRecord(
                            record.getUser().getUserId(),
                            record.getRecordId(),
                            (record.getUser().getImageUrl()),
                            record.getUser().getNickname(),
                            record.getRecordPage(),
                            dateUtil.formatLocalDateTime(record.getCreatedAt()),
                            record.getContent(),
                            record.getComments().size(),
                            record.getRecordLikes().size(),
                            isLiked
                    );
                }).collect(Collectors.toList());
    }

    private void validateRecordRequest(PostRecordRequest postRecordRequest, RecordType recordType) {
        // recordType에 따른 필수값 검증
        if (recordType == RecordType.PAGE && postRecordRequest.getRecordPage() == null) {
            throw new GlobalException(INVALID_RECORD_PAGE);
        }
        if (recordType == RecordType.ENTIRE && postRecordRequest.getRecordTitle() == null) {
            throw new GlobalException(INVALID_RECORD_TITLE);
        }
    }

    /**
     * recordId로 기록에 좋아요 toggle하기
     *
     * @param recordId
     * @return PostRecordLikeResponse
     */
    @Transactional
    public PostRecordLikeResponse toggleRecordLike(Long recordId, Long userId) {
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_RECORD));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        Room room = record.getRoom();

        // 방에 속해있지 않으면 좋아요를 누를 수 없음
        if (!userRoomRepository.existsByRoomAndUser(room, user)) {
            throw new GlobalException(NOT_PARTICIPATING_IN_ROOM);
        }

        // 방이 EXPIRED 상태이면 좋아요를 누를 수 없음
        if (room.getStatus() == EXPIRED) {
            throw new GlobalException(CANNOT_LIKE_IN_EXPIRED_ROOM);
        }

        Optional<RecordLike> existingLike = recordLikeRepository.findByRecordAndUser(record, user);

        if (existingLike.isPresent()) {
            recordLikeRepository.delete(existingLike.get());
            return new PostRecordLikeResponse(false);
        } else {
            RecordLike newLike = RecordLike.builder()
                    .record(record)
                    .user(user)
                    .build();
            recordLikeRepository.save(newLike);
            return new PostRecordLikeResponse(true);
        }
    }

    /**
     * currentPage로 어디까지 읽었는지 기록 남기기
     *
     * @param currentPage
     * @return PostRecordPageResponse
     */
    @Transactional
    public PostRecordPageResponse enterRecordPage(Long roomId, Long userId, Integer currentPage) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new GlobalException(CANNOT_FOUND_ROOM));
        User user = userRepository.findById(userId).orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));
        UserRoom userRoom = userRoomRepository.findUserRoomByRoomAndUser(room, user).orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER_ROOM));

        Book book = room.getBook();
        int totalPages = book.getPageCount();

        // 방이 EXPIRED 상태이면 페이지 입력 불가
        if (room.getStatus() == EXPIRED) {
            throw new GlobalException(CANNOT_ENTER_PAGE_IN_EXPIRED_ROOM);
        }

        if (currentPage > totalPages) {
            throw new GlobalException(INVALID_PAGE_NUMBER);
        }

        // 유저의 진행률 & current page 업데이트
        double userPercentage = ((double) currentPage / totalPages) * 100;
        userRoom.updateUserProgress(userPercentage, currentPage);

        //유저가 책을 다읽으면 독서달력을 위해 현재 시각을 저장
        if (userPercentage >= 100) {
            userRoom.setCompletedUserPercentageAt(LocalDateTime.now());
        }

        // 방의 진행률 업데이트
        List<UserRoom> roomMembers = userRoomRepository.findAllByRoom(room);
        double roomPercentage = roomMembers.stream()
                .mapToDouble(UserRoom::getUserPercentage)
                .average()
                .orElse(0.0);
        room.updateRoomPercentage(roomPercentage);

        // 혼자 읽기인 경우 진행률이 100%가 되면  종료 날짜를 현재 날짜로 설정
        // room, userRoom 둘다 stauts EXPIRED로 설정
        if (room.getRoomType() == ALONE && userPercentage >= 100) {
            room.setProgressEndDate(LocalDate.now());
            room.setStatus(EXPIRED);
            userRoom.setStatus(EXPIRED);
        }

        return PostRecordPageResponse.of(currentPage);
    }

    /**
     * 기록 삭제
     */
    @Transactional
    public void deleteRecord(Long recordId, Long userId) {
        // 기록 조회
        Record record = recordRepository.findById(recordId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_RECORD));
        Room room = record.getRoom();
        User user = userRepository.findById(userId).orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));
        UserRoom userRoom = userRoomRepository.findUserRoomByRoomAndUser(room, user).orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER_ROOM));

        // 기록 작성자가 아닌 경우 삭제 불가능
        if (!record.getUser().getUserId().equals(userId)) {
            throw new GlobalException(UNAUTHORIZED_DELETE_RECORD);
        }

        // 방이 expired 상태이면 기록 삭제 불가능
        if (userRoom.getStatus() == EXPIRED) {
            throw new GlobalException(CANNOT_DELETE_IN_EXPIRED_ROOM);
        }

        // 기록 삭제
        recordRepository.deleteByRecordId(recordId);
    }
}

