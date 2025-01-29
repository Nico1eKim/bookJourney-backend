package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookInitializer {

    private final BookRepository bookRepository;

    public void initializeBooks() {

        GenreType[] genres = GenreType.values();
        List<GenreType> validGenres = Arrays.stream(genres)
                .filter(genre -> genre != GenreType.UNKNOWN) // UNKNOWN 장르 제외
                .toList();

        int index = 1;

        for (GenreType genre : validGenres) {
            // bestSeller = true 책 생성
            Book bestSellerBook = Book.builder()
                    .genre(genre)
                    .bookTitle("Best Seller Book " + index)
                    .publisher("Publisher " + index)
                    .publishedDate(LocalDate.of(2020, 1, 1))
                    .isbn("123456789012" + index % 10)
                    .pageCount(200 + index)
                    .description("Description for Best Seller Book " + index)
                    .authorName("Author " + index)
                    .imageUrl("http://example.com/image" + index)
                    .bestSeller(true)
                    .build();
            bookRepository.save(bestSellerBook);

            index++;

            // bestSeller = false 책 생성
            Book normalBook = Book.builder()
                    .genre(genre)
                    .bookTitle("Normal Book " + index)
                    .publisher("Publisher " + index)
                    .publishedDate(LocalDate.of(2020, 1, 1))
                    .isbn("123456789012" + index % 10)
                    .pageCount(200 + index)
                    .description("Description for Normal Book " + index)
                    .authorName("Author " + index)
                    .imageUrl("http://example.com/image" + index)
                    .bestSeller(false)
                    .build();
            bookRepository.save(normalBook);

            index++;
        }
    }
}

