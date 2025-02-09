package com.example.bookjourneybackend.domain.comment.service;

import com.example.bookjourneybackend.domain.comment.domain.Comment;
import com.example.bookjourneybackend.domain.comment.domain.CommentLike;
import com.example.bookjourneybackend.domain.comment.domain.dto.request.PostCommentRequest;
import com.example.bookjourneybackend.domain.comment.domain.dto.response.CommentInfo;
import com.example.bookjourneybackend.domain.comment.domain.dto.response.GetCommentListResponse;
import com.example.bookjourneybackend.domain.comment.domain.dto.response.PostCommentResponse;
import com.example.bookjourneybackend.domain.comment.domain.dto.response.PostCommentLikeResponse;
import com.example.bookjourneybackend.domain.comment.domain.repository.CommentLikeRepository;
import com.example.bookjourneybackend.domain.comment.domain.repository.CommentRepository;
import com.example.bookjourneybackend.domain.record.domain.Record;
import com.example.bookjourneybackend.domain.record.domain.repository.RecordLikeRepository;
import com.example.bookjourneybackend.domain.record.domain.repository.RecordRepository;
import com.example.bookjourneybackend.domain.record.dto.response.RecordInfo;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.domain.userRoom.domain.UserRoom;
import com.example.bookjourneybackend.domain.userRoom.domain.repository.UserRoomRepository;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.example.bookjourneybackend.global.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.example.bookjourneybackend.domain.record.domain.RecordType.PAGE;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_RECORD;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_USER;
import static com.example.bookjourneybackend.global.entity.EntityStatus.EXPIRED;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.*;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final RecordRepository recordRepository;
    private final UserRoomRepository userRoomRepository;
    private final RecordLikeRepository recordLikeRepository;
    private final DateUtil dateUtil;

    @Transactional(readOnly = true)
    public GetCommentListResponse showComments(Long recordId, Long userId) {
        Record record = recordRepository.findById(recordId).orElseThrow(() -> new GlobalException(CANNOT_FOUND_RECORD));
        User user = userRepository.findById(userId).orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        List<Comment> comments = commentRepository.findByRecord(record);

        List<CommentInfo> commentList = comments.stream()
                .map(comment -> CommentInfo.from(comment, commentLikeRepository.existsByCommentAndUser(comment, user)))
                .toList();

        RecordInfo recordInfo;
        boolean isLiked = recordLikeRepository.existsByRecordAndUser(record, user);

        if (record.getRecordType() == PAGE) {
            recordInfo = RecordInfo.fromPageRecord(
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
        } else {
            recordInfo = RecordInfo.fromEntireRecord(
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
        }

        return GetCommentListResponse.of(commentList, recordInfo);
    }

    @Transactional
    public PostCommentResponse createComment(Long recordId, Long userId, PostCommentRequest request) {
        Record record = recordRepository.findById(recordId).orElseThrow(() -> new GlobalException(CANNOT_FOUND_RECORD));
        User user = userRepository.findById(userId).orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));
        Room room = record.getRoom();
        UserRoom userRoom = userRoomRepository.findUserRoomByRoomAndUser(room, user).orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER_ROOM));

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

    @Transactional
    public PostCommentLikeResponse toggleCommentLike(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new GlobalException(CANNOT_FOUND_COMMENT));
        User user = userRepository.findById(userId).orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        Record record = comment.getRecord();
        Room room = record.getRoom();

        // 방에 속해있지 않으면 좋아요를 누를 수 없음
        if (!userRoomRepository.existsByRoomAndUser(room, user)) {
            throw new GlobalException(NOT_PARTICIPATING_IN_ROOM);
        }

        // 방이 EXPIRED 상태이면 좋아요를 누를 수 없음
        if (room.getStatus() == EXPIRED) {
            throw new GlobalException(CANNOT_LIKE_IN_EXPIRED_ROOM);
        }

        Optional<CommentLike> existingLike = commentLikeRepository.findByCommentAndUser(comment, user);

        if (existingLike.isPresent()) {
            commentLikeRepository.delete(existingLike.get());
            commentLikeRepository.flush(); // 즉시 db에 반영
            return new PostCommentLikeResponse(false);
        } else {
            CommentLike newLike = CommentLike.builder()
                    .comment(comment)
                    .user(user)
                    .build();
            commentLikeRepository.save(newLike);
            commentLikeRepository.flush(); // 즉시 db에 반영
            return new PostCommentLikeResponse(true);
        }
    }
}
