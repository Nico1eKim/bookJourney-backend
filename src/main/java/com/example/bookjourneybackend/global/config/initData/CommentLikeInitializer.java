package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.comment.domain.Comment;
import com.example.bookjourneybackend.domain.comment.domain.CommentLike;
import com.example.bookjourneybackend.domain.comment.domain.repository.CommentLikeRepository;
import com.example.bookjourneybackend.domain.comment.domain.repository.CommentRepository;
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
public class CommentLikeInitializer {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional // 트랜잭션 추가
    public void initializeCommentLikes() {
        List<Comment> comments = commentRepository.findAll(); // Comment 리스트 로드
        List<User> users = userRepository.findAll(); // User 리스트 로드

        for (int i = 0; i < comments.size(); i++) {
            Comment comment = comments.get(i); // 특정 Comment 가져오기
            User user = users.get(i % users.size()); // 특정 User 가져오기

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
