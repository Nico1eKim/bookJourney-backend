package com.example.bookjourneybackend.domain.book.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetBookPopularResponse {

    private Long bookId;
    private String isbn;    //혹시 나중에 해당 책을 눌렀을떄 상세보기로 넘어가는 화면을 위해..
    private String bookTitle;
    private String imageUrl;
    private String authorName;
    private Integer readCount;
    private String description;

    public GetBookPopularResponse(Long bookId, String isbn, String bookTitle, String imageUrl, String authorName, Integer readCount, String description) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.bookTitle = bookTitle;
        this.imageUrl = imageUrl;
        this.authorName = authorName;
        this.readCount = readCount;
        this.description = description;
    }

    public static GetBookPopularResponse of(Long bookId, String isbn, String bookTitle, String imageUrl, String authorName, Integer readCount, String description) {
        return new GetBookPopularResponse(bookId, isbn, bookTitle, imageUrl, authorName, readCount, description);
    }
}
