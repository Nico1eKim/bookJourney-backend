package com.example.bookjourneybackend.domain.book.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetBookInfoResponse {
    private String genre;

    private String imageUrl;

    private String bookTitle;

    private String authorName;

    @Setter
    private boolean favorite;

    private String publisher;

    private String publishedDate;

    private String isbn;

    private String description;

    public GetBookInfoResponse(String genre, String imageUrl, String bookTitle, String authorName, String publisher, String publishedDate, String isbn, String description) {
        this.genre = genre;
        this.imageUrl = imageUrl;
        this.bookTitle = bookTitle;
        this.authorName = authorName;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.isbn = isbn;
        this.description = description;
    }

    public static GetBookInfoResponse of(String genre, String imageUrl, String bookTitle, String authorName, boolean favorite, String publisher, String publishedDate, String isbn, String description) {
        return new GetBookInfoResponse(genre, imageUrl, bookTitle, authorName, favorite, publisher, publishedDate, isbn, description);
    }
}
