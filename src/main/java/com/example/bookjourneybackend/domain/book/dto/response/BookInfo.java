package com.example.bookjourneybackend.domain.book.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class BookInfo {
    private String bookTitle;

    private String authorName;

    private String isbnCode;

    private String imageUrl;

}
