package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.room.domain.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RoomInitializer {

    private final RoomRepository roomRepository;
    private final BookRepository bookRepository;

    public void initializeRooms() {
        List<Book> books = bookRepository.findAll();

        for (int i = 1; i <= 20; i++) {
            Room room = Room.makeReadTogetherRoom(
                    "Room " + i,
                    books.get(i % books.size()), // 연관된 책 설정
                    i % 2 == 0,
                    null,
                    LocalDate.now(),
                    LocalDate.now().plusMonths(1),
                    LocalDate.now().plusDays(10),
                    5
            );

            // 더미 데이터를 저장
            roomRepository.save(room);
        }
    }
}
