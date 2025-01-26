package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class BookInitializer {

    private final BookRepository bookRepository;

    public void initializeBooks() {
        for (int i = 1; i <= 100; i++) {
            Book book = Book.builder()
                    .genre(GenreType.NOVEL_POETRY_DRAMA)
                    .bookTitle("Book Title " + i)
                    .publisher("Publisher " + i)
                    .publishedDate(LocalDate.of(2020, 1, 1))
                    .isbn("123456789012" + i % 10)
                    .pageCount(200 + i)
                    .description("Description for Book " + i)
                    .authorName("Author " + i)
                    .imageUrl("http://example.com/image" + i)
                    .bestSeller(i % 2 == 0)
                    .build();
            bookRepository.save(book);
        }
    }
}

