package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.comment.domain.Comment;
import com.example.bookjourneybackend.domain.comment.domain.repository.CommentRepository;
import com.example.bookjourneybackend.domain.record.domain.Record;
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
public class CommentInitializer {

    private final CommentRepository commentRepository;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;

    @Transactional // 트랜잭션 범위를 추가
    public void initializeComments() {
        List<Record> records = recordRepository.findAll(); // Record를 트랜잭션 내에서 로드
        List<User> users = userRepository.findAll();

        for (int i = 1; i <= 100; i++) {
            Record record = records.get(i % records.size()); // 연관된 Record를 가져옴
            User user = users.get(i % users.size());

            Comment comment = Comment.builder()
                    .record(record) // Record 설정
                    .user(user)
                    .content("This is comment " + i)
                    .build();

            // 연관관계 설정
            record.addComment(comment); // addComment를 호출하여 Record와 연관관계를 설정

            // Comment 저장
            commentRepository.save(comment);
        }
    }
}
