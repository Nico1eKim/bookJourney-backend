package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.room.domain.repository.RoomRepository;
import com.example.bookjourneybackend.global.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import static com.example.bookjourneybackend.global.entity.EntityStatus.EXPIRED;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomInitializer {

    private final RoomRepository roomRepository;
    private final BookRepository bookRepository;
    private final DateUtil dateUtil;
    Random random = new Random();

    List<String> roomNames = List.of(
            "함께 읽어요: ", "깊이 있는 독서 모임 - ", "이달의 책: ", "조용한 독서시간 - ",
            "한달 완독 챌린지 - ", "심야 독서회 - ", "독서 토론방: ", "책과 함께하는 시간 - ",
            "주말 독서모임: ", "고전 탐독 클럽 - ", "문학 감상회: ", "베스트셀러 읽기 - "
    );

    //1~20 ACTIVE 21~30 EXPIRED 같이읽기 짝수 공개방 홀수 비공개방 비밀번호 1234로 고정 모집인원 방아이디 +2
    //32~47 혼자읽기 ACTIVE 48~55 혼자읽기 EXPIRED
    public void initializeRooms() {

        List<Book> books = bookRepository.findAll();

        // 1~30 같이 읽기 방 생성 (1~20 ACTIVE, 21~30 EXPIRED)
        for (int index = 0; index < 31; index++) {

            LocalDate startDate = index < 20 ? LocalDate.now() : LocalDate.of(2024, 8, 1);
            LocalDate progressEndDate = startDate.plusMonths(2);
            LocalDate recruitEndDate = dateUtil.calculateRecruitEndDate(startDate, progressEndDate);

            Room room = Room.makeReadTogetherRoom(
                    roomNames.get(random.nextInt(roomNames.size())),
                    books.get(index),
                    (index + 1) % 2 == 0, // 짝수 공개, 홀수 비공개
                    (index + 1) % 2 == 0 ? null : 1234, // 비공개일 때 비밀번호 1234
                    startDate,
                    progressEndDate,
                    recruitEndDate,
                    index + 2
            );

            if (index >= 20) { // 21~30 EXPIRED 설정
                room.setStatus(EXPIRED);
            }

            roomRepository.save(room);
        }

        // 32~55 혼자 읽기 방 생성 (32~47 ACTIVE, 48~55 EXPIRED)
        for (int index = 0; index < 24; index++) {
            Room room = Room.makeReadAloneRoom(books.get(index));

            if (index >= 16) { // 48~55 EXPIRED 설정
                room.setStatus(EXPIRED);
                room.setProgressEndDate(LocalDate.now());
                room.setRoomPercentage(100.0);
            }

            roomRepository.save(room);
        }
    }

}
