package com.example.bookjourneybackend.domain.comment.service;

import com.example.bookjourneybackend.domain.comment.domain.Comment;
import com.example.bookjourneybackend.domain.comment.domain.dto.request.PostCommentRequest;
import com.example.bookjourneybackend.domain.comment.domain.dto.response.CommentInfo;
import com.example.bookjourneybackend.domain.comment.domain.dto.response.GetCommentListResponse;
import com.example.bookjourneybackend.domain.comment.domain.dto.response.PostCommentResponse;
import com.example.bookjourneybackend.domain.comment.domain.repository.CommentLikeRepository;
import com.example.bookjourneybackend.domain.comment.domain.repository.CommentRepository;
import com.example.bookjourneybackend.domain.record.domain.Record;
import com.example.bookjourneybackend.domain.record.domain.repository.RecordRepository;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.domain.userRoom.domain.UserRoom;
import com.example.bookjourneybackend.domain.userRoom.domain.repository.UserRoomRepository;
import com.example.bookjourneybackend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.bookjourneybackend.global.entity.EntityStatus.DELETED;
import static com.example.bookjourneybackend.global.entity.EntityStatus.EXPIRED;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final RecordRepository recordRepository;
    private final UserRoomRepository userRoomRepository;

    @Transactional(readOnly = true)
    public GetCommentListResponse showComments(Long recordId, Long userId) {
        Record record = recordRepository.findById(recordId).orElseThrow(() -> new GlobalException(CANNOT_FOUND_RECORD));
        User user = userRepository.findById(userId).orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        List<Comment> comments = commentRepository.findByRecord(record);

        List<CommentInfo> commentList = comments.stream()
                .map(comment -> CommentInfo.from(comment, commentLikeRepository.existsByCommentAndUser(comment, user)))
                .toList();

        return GetCommentListResponse.of(commentList);
    }

    @Transactional
    public PostCommentResponse createComment(Long recordId, Long userId, PostCommentRequest request) {
        Record record = recordRepository.findById(recordId).orElseThrow(() -> new GlobalException(CANNOT_FOUND_RECORD));
        User user = userRepository.findById(userId).orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));
        Room room = record.getRoom();
        UserRoom userRoom = userRoomRepository.findFirstByRoomAndUser(room, user).orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER_ROOM));

        // 방이 EXPIRED 상태이면 댓글을 남길 수 없음
        if (userRoom.getStatus() == EXPIRED) {
            throw new GlobalException(CANNOT_COMMENT_IN_EXPIRED_ROOM);
        }

        Comment newComment = Comment.builder()
                .record(record)
                .user(user)
                .content(request.getContent())
                .build();

        commentRepository.save(newComment);

        return PostCommentResponse.of(newComment.getCommentId());
    }
}
