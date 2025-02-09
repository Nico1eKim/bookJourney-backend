package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.comment.domain.Comment;
import com.example.bookjourneybackend.domain.comment.domain.CommentLike;
import com.example.bookjourneybackend.domain.comment.domain.repository.CommentLikeRepository;
import com.example.bookjourneybackend.domain.comment.domain.repository.CommentRepository;
import com.example.bookjourneybackend.domain.record.domain.Record;
import com.example.bookjourneybackend.domain.record.domain.RecordLike;
import com.example.bookjourneybackend.domain.record.domain.repository.RecordRepository;
import com.example.bookjourneybackend.domain.room.domain.Room;
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

@Component
@RequiredArgsConstructor
public class CommentLikeInitializer {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;
    private final Random random = new Random();

    @Transactional // 트랜잭션 추가
    public void initializeCommentLikes() {
        List<User> users = userRepository.findAll(); // User 리스트 로드
        List<Record> records = recordRepository.findAll();

        for (Record record : records) { //유저가 참가하는 모든 방에대해서 모든 기록에대해 랜덤 좋아요

            Room room = record.getRoom();
            List<Comment> comments = commentRepository.findByRecord(record);

            for(Comment comment : comments) {

                for (User user : users) {

                    Optional<UserRoom> userRoomOpt = userRoomRepository.findUserRoomByRoomAndUser(room, user);
                    if (userRoomOpt.isEmpty()) {
                        continue;
                    }

                    //랜덤 댓글 좋아요
                    if (random.nextBoolean()) {
                        CommentLike commentLike = CommentLike.builder()
                                .comment(comment)
                                .user(user)
                                .build();

                        // 연관관계 설정
                        comment.addCommentLike(commentLike);

                        // CommentLike 저장
                        commentLikeRepository.save(commentLike);
                    }

                }

            }

        }
    }
}
