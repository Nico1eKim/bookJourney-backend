package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookInitializer {

    private final BookRepository bookRepository;

    public void initializeBooks() {
        long baseIsbn = 1234567891011L; // 시작 ISBN 값

        for (int i = 1; i <= 100; i++) {
            String uniqueIsbn = String.valueOf(baseIsbn + i);

            Book book = Book.builder()
                    .genre(GenreType.NOVEL_POETRY_DRAMA)
                    .bookTitle("Book Title " + i)
                    .publisher("Publisher " + i)
                    .publishedDate(LocalDate.of(2020, 1, 1))
                    .isbn(uniqueIsbn)
                    .pageCount(200 + i)
                    .description("Description for Book " + i)
                    .authorName("Author " + i)
                    .imageUrl("http://example.com/image" + i)
                    .bestSeller(i % 2 == 0)
                    .build();
            bookRepository.save(book);
        }
    }

    // 고유한 ISBN 생성 (UUID 활용)
    private String generateUniqueIsbn() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 13); // 13자리 ISBN 생성
    }
}

