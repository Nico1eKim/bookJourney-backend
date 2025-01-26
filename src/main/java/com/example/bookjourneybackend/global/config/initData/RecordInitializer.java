package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.record.domain.Record;
import com.example.bookjourneybackend.domain.record.domain.RecordType;
import com.example.bookjourneybackend.domain.record.domain.repository.RecordRepository;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.room.domain.repository.RoomRepository;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.domain.userRoom.domain.UserRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecordInitializer {

    private final RecordRepository recordRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional // 트랜잭션 추가
    public void initializeRecords() {
        List<Room> rooms = roomRepository.findAll();
        List<User> users = userRepository.findAll();

        for (int i = 1; i <= 30; i++) {
            Room room = rooms.get(i % rooms.size());
            User user = users.get(i % users.size());

            Record record = Record.builder()
                    .room(room)
                    .user(user)
                    .recordTitle("Record Title " + i)
                    .recordType(i % 2 == 0 ? RecordType.PAGE : RecordType.ENTIRE)
                    .recordPage(10 * i)
                    .content("Content of record " + i)
                    .build();

            // 연관관계 설정
            room.addRecord(record); // `Room` 엔티티의 addRecord 사용
            recordRepository.save(record);
        }
    }
}
