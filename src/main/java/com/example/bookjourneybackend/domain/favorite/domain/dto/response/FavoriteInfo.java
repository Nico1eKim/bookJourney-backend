package com.example.bookjourneybackend.domain.favorite.domain.dto.response;

import com.example.bookjourneybackend.domain.book.dto.response.BookInfo;
import com.example.bookjourneybackend.domain.favorite.domain.Favorite;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class FavoriteInfo {

    private Long favoriteId;
    private BookInfo bookInfo;

    public static FavoriteInfo of(Favorite favorite) {
        return new FavoriteInfo(favorite.getFavoriteId(), BookInfo.of(favorite.getBook()));
    }
}
