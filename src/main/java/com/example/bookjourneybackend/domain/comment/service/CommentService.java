package com.example.bookjourneybackend.domain.comment.service;

import com.example.bookjourneybackend.domain.comment.domain.Comment;
import com.example.bookjourneybackend.domain.comment.domain.CommentLike;
import com.example.bookjourneybackend.domain.comment.domain.dto.response.CommentInfo;
import com.example.bookjourneybackend.domain.comment.domain.dto.response.GetCommentListResponse;
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
import com.example.bookjourneybackend.domain.userRoom.domain.repository.UserRoomRepository;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.example.bookjourneybackend.global.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.bookjourneybackend.domain.record.domain.RecordType.PAGE;
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
    private final RecordLikeRepository recordLikeRepository;
    private final DateUtil dateUtil;
    private final UserRoomRepository userRoomRepository;

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
                    (record.getUser().getUserImage() != null) ? record.getUser().getUserImage().getImageUrl() : null,
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
                    (record.getUser().getUserImage() != null) ? record.getUser().getUserImage().getImageUrl() : null,
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

        boolean isLiked = commentLikeRepository.existsByCommentAndUser(comment, user);

        if (isLiked) {
            commentLikeRepository.deleteByCommentAndUser(comment, user);
        } else {
            commentLikeRepository.save(CommentLike.builder().comment(comment).user(user).build());
        }

        return PostCommentLikeResponse.of(!isLiked);
    }
}
