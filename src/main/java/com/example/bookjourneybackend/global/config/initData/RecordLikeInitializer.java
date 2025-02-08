package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.record.domain.Record;
import com.example.bookjourneybackend.domain.record.domain.RecordLike;
import com.example.bookjourneybackend.domain.record.domain.RecordType;
import com.example.bookjourneybackend.domain.record.domain.repository.RecordLikeRepository;
import com.example.bookjourneybackend.domain.record.domain.repository.RecordRepository;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.room.domain.repository.RoomRepository;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.domain.userRoom.domain.UserRoom;
import com.example.bookjourneybackend.domain.userRoom.domain.repository.UserRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.example.bookjourneybackend.domain.record.domain.RecordType.ENTIRE;
import static com.example.bookjourneybackend.domain.record.domain.RecordType.PAGE;

@Component
@RequiredArgsConstructor
public class RecordLikeInitializer {

    private final RecordLikeRepository recordLikeRepository;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;
    private final Random random = new Random(); // 랜덤 객체 생성

    @Transactional // 트랜잭션 추가
    public void initializeRecordLikes() {
        List<User> users = userRepository.findAll(); // User 리스트 로드
        List<Record> records = recordRepository.findAll();

            for (Record record : records) {

                Room room = record.getRoom();

                for (User user : users) {

                    Optional<UserRoom> userRoomOpt = userRoomRepository.findUserRoomByRoomAndUser(room, user);
                    if (userRoomOpt.isEmpty()) {
                        continue;
                    }

                    //랜덤 좋아요
                    if(random.nextBoolean()) {
                        RecordLike recordLike = RecordLike.builder()
                                .record(record)
                                .user(user)
                                .build();

                        // 연관관계 설정
                        record.addRecordLike(recordLike);
                        // RecordLike 저장
                        recordLikeRepository.save(recordLike);
                    }

                }

            }


    }
}
