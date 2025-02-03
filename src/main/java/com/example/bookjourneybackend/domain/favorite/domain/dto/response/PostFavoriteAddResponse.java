package com.example.bookjourneybackend.domain.favorite.domain.dto.response;

import com.example.bookjourneybackend.domain.recentSearch.domain.dto.response.RecentSearchInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = PRIVATE)
public class PostFavoriteAddResponse {

    private Long bookId;
    private boolean favorite;

    public static PostFavoriteAddResponse of(Long bookId, boolean favorite) {
        return new PostFavoriteAddResponse(bookId, favorite);
    }
}
