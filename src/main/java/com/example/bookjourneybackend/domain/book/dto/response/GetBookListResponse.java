package com.example.bookjourneybackend.domain.book.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GetBookListResponse {
    private List<BookInfo> bookList;

    public GetBookListResponse(List<BookInfo> bookList) {
        this.bookList = bookList;
    }

    public void addBookInfo(BookInfo bookInfo) {
        this.bookList.add(bookInfo);
    }

    public static GetBookListResponse of(List<BookInfo> bookList) {
        return new GetBookListResponse(bookList);
    }
}
