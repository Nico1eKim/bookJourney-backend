package com.example.bookjourneybackend.domain.book.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookInfo {     //책 목록에서 나오는 책 정보들
    private String bookTitle;

    private String authorName;

    private String isbn;

    private String imageUrl;

}
