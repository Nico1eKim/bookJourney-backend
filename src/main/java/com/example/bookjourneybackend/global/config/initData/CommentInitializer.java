package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.comment.domain.Comment;
import com.example.bookjourneybackend.domain.comment.domain.repository.CommentRepository;
import com.example.bookjourneybackend.domain.record.domain.Record;
import com.example.bookjourneybackend.domain.record.domain.repository.RecordRepository;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.domain.userRoom.domain.UserRoom;
import com.example.bookjourneybackend.domain.userRoom.domain.repository.UserRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class CommentInitializer {

    private final CommentRepository commentRepository;
    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;
    private final Random random = new Random(); // 랜덤 객체 생성

    private final List<String> commentContents = List.of(
            "정말 공감돼요! 저도 비슷한 생각을 했어요.",
            "이런 시각으로 볼 수도 있군요. 흥미로운 감상이에요!",
            "좋은 리뷰네요. 저도 다시 읽어봐야겠어요!",
            "이 감상평을 보니 책이 더 궁금해졌어요!",
            "이 부분에 대한 해석이 인상적이네요. 저도 공감해요.",
            "저랑은 다르게 느끼셨군요. 다양한 의견이 있어 좋네요!",
            "책을 읽고 나서 이런 감상을 할 수도 있군요. 신선해요!",
            "이 감상 덕분에 책을 다시 보고 싶어졌어요!",
            "글을 읽다 보니 저도 감상평을 남겨보고 싶네요.",
            "저도 이 부분이 인상 깊었어요! 공감됩니다.",
            "감상을 보니 책을 다시 읽어보고 싶어졌어요.",
            "다른 사람의 감상을 보니 또 다른 시각이 생기네요!",
            "와, 이런 해석은 생각도 못 했어요. 좋은 의견이에요!",
            "이 감상 덕분에 책을 더 깊이 이해하게 되었어요.",
            "감상이 정말 섬세하고 인상적이에요! 잘 읽었습니다.",
            "저는 다르게 느꼈지만, 이런 의견도 흥미롭네요!",
            "책을 다르게 바라볼 수 있는 계기가 된 것 같아요.",
            "감상을 보니 책에 대한 애정이 느껴지네요!",
            "이 부분에 대한 해석이 새롭고 흥미롭네요.",
            "감상평을 보니 저도 제 생각을 정리해보고 싶어요!",
            "이렇게도 해석할 수 있군요! 시야가 넓어졌어요.",
            "정말 좋은 리뷰네요! 저도 같은 부분에서 감동했어요.",
            "리뷰 덕분에 책을 읽고 싶어졌어요!",
            "책을 읽은 후 이런 감상이 나올 수 있다는 게 신기해요.",
            "이 감상평을 보고 새로운 관점을 배웠어요!",
            "저도 같은 부분에서 깊은 인상을 받았어요.",
            "책의 또 다른 면을 발견하게 해주는 감상평이네요!",
            "감상을 보니 다시 읽으면서 더 집중하고 싶어졌어요.",
            "좋은 감상평 덕분에 책에 대한 기대감이 높아졌어요!"
    );


    @Transactional // 트랜잭션 범위를 추가
    public void initializeComments() {
        List<Record> records = recordRepository.findAll();
        List<User> users = userRepository.findAll();

        for (Record record : records) {

            Room room = record.getRoom();

            for (User user : users) { //유저가 참가하는 모든 방에대해서 기록 랜덤 남김

                Optional<UserRoom> userRoomOpt = userRoomRepository.findUserRoomByRoomAndUser(room, user);
                if (userRoomOpt.isEmpty()) {
                    continue;
                }

                //랜덤 댓글
                if(random.nextBoolean()) {
                    Comment comment = Comment.builder()
                            .record(record) // Record 설정
                            .user(user)
                            .content(commentContents.get(random.nextInt(commentContents.size())))
                            .build();
                    // 연관관계 설정
                    record.addComment(comment); // addComment를 호출하여 Record와 연관관계를 설정

                    // Comment 저장
                    commentRepository.save(comment);
                }

            }

        }

    }
}
