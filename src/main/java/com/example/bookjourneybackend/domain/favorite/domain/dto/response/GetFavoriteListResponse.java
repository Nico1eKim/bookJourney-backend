package com.example.bookjourneybackend.domain.favorite.domain.dto.response;

import com.example.bookjourneybackend.domain.book.dto.response.BookInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class GetFavoriteListResponse {

    private List<BookInfo> bookList;

    public static GetFavoriteListResponse of(List<BookInfo> bookList) {
        return new GetFavoriteListResponse(bookList);
    }
}
