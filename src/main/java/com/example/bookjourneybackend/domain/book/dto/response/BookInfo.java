package com.example.bookjourneybackend.domain.book.dto.response;

import com.example.bookjourneybackend.domain.book.domain.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookInfo {     //책 목록에서 나오는 책 정보들
    private String bookTitle;

    private String authorName;

    private String isbn;

    private String imageUrl;

    public static BookInfo of(Book book) {
        return new BookInfo(book.getBookTitle(), book.getAuthorName(), book.getIsbn(), book.getImageUrl());
    }

}
