package com.example.bookjourneybackend.domain.record.service;

import com.example.bookjourneybackend.domain.record.domain.RecordSortType;
import com.example.bookjourneybackend.domain.record.domain.Record;
import com.example.bookjourneybackend.domain.record.domain.RecordType;
import com.example.bookjourneybackend.domain.record.domain.repository.RecordLikeRepository;
import com.example.bookjourneybackend.domain.record.domain.repository.RecordRepository;
import com.example.bookjourneybackend.domain.record.dto.request.PostRecordRequest;
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

import static com.example.bookjourneybackend.global.entity.EntityStatus.*;

import java.util.List;
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
        log.info("------------------------[RecordService.createRecord]------------------------");

        Room room = roomRepository.findById(roomId).orElseThrow(() -> new GlobalException(CANNOT_FOUND_ROOM));
        User user = userRepository.findById(userId).orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));
        UserRoom userRoom = userRoomRepository.findUserRoomByRoomAndUser(room, user).orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER_ROOM));

        // UserRoom이 INACTIVE 상태이면 ACTIVE로 변경
        if (userRoom.getStatus() == INACTIVE) {
            userRoom.setStatus(ACTIVE);
            userRoomRepository.save(userRoom);
        }

        // 유저가 방에 속해 있지 않거나, 방에서 삭제된 경우 예외 발생
        if (!userRoom.isMember() || userRoom.getStatus() == DELETED) {
            throw new GlobalException(NOT_PARTICIPATING_IN_ROOM);
        }

        RecordType recordType = RecordType.from(postRecordRequest.getRecordType());
        validateRecordRequest(postRecordRequest, recordType);

        Record newRecord = Record.builder()
                .room(room)
                .user(user)
                .recordType(recordType)
                .recordTitle(postRecordRequest.getRecordTitle())
                .recordPage(postRecordRequest.getRecordPage())
                .content(postRecordRequest.getContent())
                .build();

        recordRepository.save(newRecord);
//        room.addRecord(newRecord);

        return PostRecordResponse.of(newRecord.getRecordId());

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
                .filter(record -> record.getRecordType() == RecordType.ENTIRE)
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

        RecordSortType recordSortType = (sortType == null) ? LATEST : RecordSortType.from(sortType);

        List<Record> records = findRecordsByRoomId(roomId, recordSortType).stream()
                .filter(record -> record.getRecordType() == RecordType.PAGE)
                .filter(record -> record.getRecordPage() != null && (
                        (pageStart == null || record.getRecordPage() >= pageStart) &&
                                (pageEnd == null || record.getRecordPage() <= pageEnd)
                ))
                .collect(Collectors.toList());

        List<RecordInfo> recordInfoList = parsePageRecordsToResponse(records, user);
        return GetRecordResponse.of(recordInfoList);
    }

    /**
     * roomId와 기록의 정렬 순서로 기록 찾기
     */
    private List<Record> findRecordsByRoomId(Long roomId, RecordSortType sortType) {
        return switch (sortType) {
            case LATEST -> recordRepository.findRecordsOrderByLatest(roomId, sortType);
            case MOST_COMMENTS -> recordRepository.findRecordsOrderByMostComments(roomId, sortType);
            case PAGE_ORDER -> recordRepository.findRecordsOrderByPage(roomId, sortType);
            default -> throw new GlobalException(INVALID_RECORD_SORT_TYPE);
        };
    }

    private List<RecordInfo> parseEntireRecordsToResponse(List<Record> records, User user) {
        return records.stream()
                .map(record -> {
                    boolean isLiked = recordLikeRepository.existsByRecordAndUser(record, user);
                    return RecordInfo.fromEntireRecord(
                            record.getUser().getUserId(),
                            record.getRecordId(),
                            (record.getUser().getUserImage() != null) ? record.getUser().getUserImage().getImageUrl() : null,
                            record.getUser().getNickname(),
                            record.getRecordTitle(),
                            dateUtil.formDateTime(record.getCreatedAt()),
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
                            (record.getUser().getUserImage() != null) ? record.getUser().getUserImage().getImageUrl() : null,
                            record.getUser().getNickname(),
                            record.getRecordPage(),
                            dateUtil.formDateTime(record.getCreatedAt()),
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
}
