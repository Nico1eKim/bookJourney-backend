package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.record.domain.Record;
import com.example.bookjourneybackend.domain.record.domain.RecordLike;
import com.example.bookjourneybackend.domain.record.domain.repository.RecordLikeRepository;
import com.example.bookjourneybackend.domain.record.domain.repository.RecordRepository;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecordLikeInitializer {

    private final RecordLikeRepository recordLikeRepository;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;

    @Transactional // 트랜잭션 추가
    public void initializeRecordLikes() {
        List<Record> records = recordRepository.findAll(); // Record 리스트 로드
        List<User> users = userRepository.findAll(); // User 리스트 로드

        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i); // 특정 Record 가져오기
            User user = users.get(i % users.size()); // 특정 User 가져오기

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
