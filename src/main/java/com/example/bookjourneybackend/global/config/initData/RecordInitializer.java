package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.record.domain.Record;
import com.example.bookjourneybackend.domain.record.domain.RecordType;
import com.example.bookjourneybackend.domain.record.domain.repository.RecordRepository;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.room.domain.repository.RoomRepository;
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

import static com.example.bookjourneybackend.domain.record.domain.RecordType.ENTIRE;
import static com.example.bookjourneybackend.domain.record.domain.RecordType.PAGE;

@Component
@RequiredArgsConstructor
public class RecordInitializer {

    private final RecordRepository recordRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;
    private final Random random = new Random(); // 랜덤 객체 생성

    // 책에 대한 제목과 내용 생성
    private final List<String> titleWords = List.of(
            "책의 감동", "소설 속 여행", "영감을 주는 이야기", "불가능을 넘어서", "지혜의 책", "미래를 여는 책",
            "감동적인 결말", "모험의 시작", "삶을 바꾸는 책", "성장을 위한 책", "읽고 싶은 책", "책 속의 세계",
            "지식을 넓히는 책", "새로운 발견", "책과 나");

    private final List<String> contentWords = List.of(
            "이 책은 정말 감동적이었어요. 책을 읽고 나서 많은 생각을 했습니다.",
            "소설 속 이야기가 너무 흥미롭고 몰입감이 있었어요. 캐릭터들과 함께 여행하는 느낌이었어요.",
            "이 책을 읽으며 많은 영감을 얻었어요. 내 삶에 긍정적인 영향을 끼친 것 같아요.",
            "책 속에서 불가능을 넘는 이야기를 봤습니다. 정말 용기와 희망을 주었어요.",
            "이 책에서 배운 지혜는 정말 가치 있는 것이었어요. 계속해서 반복해서 읽고 싶습니다.",
            "책 속의 내용이 미래를 여는 중요한 메시지를 전달해 주었어요. 읽고 나서 새로운 시각을 가질 수 있었어요.",
            "이 책의 결말은 정말 감동적이었어요. 끝까지 눈을 뗄 수 없었어요.",
            "책 속에서 펼쳐지는 모험은 정말 흥미롭고 짜릿했어요. 끝까지 손에 땀을 쥐고 읽었어요.",
            "이 책은 내 삶을 바꿀 정도로 깊은 인상을 남겼어요. 많은 걸 깨닫게 해 주었어요.",
            "이 책은 내가 성장하는 데 도움이 되는 중요한 책이었어요. 계속해서 읽고 싶어요.",
            "이 책은 정말 읽고 싶은 책이에요. 내용이 너무 좋았어요. 나중에 다시 읽을 예정이에요.",
            "책 속의 세계는 상상 그 이상이었어요. 글자 하나하나가 마음에 깊게 남았어요.",
            "이 책을 읽으면서 새로운 지식을 많이 얻었어요. 나의 시야가 넓어졌어요.",
            "책을 읽으면서 많은 새로운 것을 발견했어요. 정말 유익한 시간이었어요.",
            "책을 읽고 나서 생각을 많이 했어요. 책은 정말 내게 많은 것을 알려주었어요."
    );



    @Transactional // 트랜잭션 추가
    public void initializeRecords() {

        List<Room> rooms = roomRepository.findAll();
        List<User> users = userRepository.findAll();

        for (Room room : rooms) {
            Book book = room.getBook();
            int totalPages = book.getPageCount();

            for (User user : users) {

                Optional<UserRoom> userRoomOpt = userRoomRepository.findUserRoomByRoomAndUser(room, user);
                if (userRoomOpt.isEmpty()) {
                    continue;
                }
                UserRoom userRoom =  userRoomOpt.get();
                RecordType recordType;

                //방아이디 + 유저아이디 짝수면 페이지기록, 홀수면 전체기록 // 혼자읽기 방일경우 페이지기록
                recordType = (room.getRoomId() >= 32) ? PAGE :
                        ((room.getRoomId() + user.getUserId()) % 2 == 0 ? PAGE : ENTIRE);

                String recordTitle = (room.getRoomId() >= 48 || recordType == PAGE) ? null : titleWords.get(random.nextInt(titleWords.size()));
                String content = contentWords.get(random.nextInt(contentWords.size()));
                Integer currentPage = (room.getRoomId() >= 48) ? totalPages :
                        ((recordType == PAGE) ? ((int) (Math.random() * (totalPages - 20)) + 1) : 0);

                // 유저 진행율 업데이트
                if (recordType == PAGE || room.getRoomId() >= 48) {
                    double userPercentage = (room.getRoomId() >= 48) ? 100.0 : ((double) currentPage / totalPages) * 100;
                    userRoom.updateUserProgress(userPercentage, currentPage);
                }

                Record record = Record.builder()
                        .room(room)
                        .user(user)
                        .recordTitle(recordTitle)
                        .recordType(recordType)
                        .recordPage(currentPage)
                        .content(content)
                        .build();

                // 연관관계 설정
                room.addRecord(record);
                recordRepository.save(record);
            }

            if(room.getRoomId()<48) {
                // 방 진행율 업데이트
                List<UserRoom> roomMembers = userRoomRepository.findAllByRoom(room);
                double roomPercentage = roomMembers.stream()
                        .mapToDouble(UserRoom::getUserPercentage)
                        .average()
                        .orElse(0.0);
                room.updateRoomPercentage(roomPercentage);
            }


        }
    }
}
