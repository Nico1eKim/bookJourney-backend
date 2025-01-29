package com.example.bookjourneybackend.domain.record.service;

import com.example.bookjourneybackend.domain.record.domain.Record;
import com.example.bookjourneybackend.domain.record.domain.RecordType;
import com.example.bookjourneybackend.domain.record.domain.repository.RecordRepository;
import com.example.bookjourneybackend.domain.record.dto.request.PostRecordRequest;
import com.example.bookjourneybackend.domain.record.dto.response.PostRecordResponse;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.room.domain.repository.RoomRepository;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordService {
    private final RecordRepository recordRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostRecordResponse createRecord(PostRecordRequest postRecordRequest, Long roomId, Long userId) {
        log.info("------------------------[RecordService.createRecord]------------------------");

        Room room = roomRepository.findById(roomId).orElseThrow(() -> new GlobalException(CANNOT_FOUND_ROOM));
        User user = userRepository.findById(userId).orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

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
