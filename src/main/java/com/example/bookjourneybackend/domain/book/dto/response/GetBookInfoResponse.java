package com.example.bookjourneybackend.domain.book.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetBookInfoResponse {
    private String genre;

    private String imageUrl;

    private String bookTitle;

    private String authorName;

    private boolean favorite;

    private String publisher;

    private String publishedDate;

    private String isbnCode;

    private String description;

    public GetBookInfoResponse(String genre, String imageUrl, String bookTitle, String authorName, String publisher, String publishedDate, String isbnCode, String description) {
        this.genre = genre;
        this.imageUrl = imageUrl;
        this.bookTitle = bookTitle;
        this.authorName = authorName;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.isbnCode = isbnCode;
        this.description = description;
    }

    public static GetBookInfoResponse of(String genre, String imageUrl, String bookTitle, String authorName, boolean favorite, String publisher, String publishedDate, String isbnCode, String description) {
        return new GetBookInfoResponse(genre, imageUrl, bookTitle, authorName, favorite, publisher, publishedDate, isbnCode, description);
    }
}
