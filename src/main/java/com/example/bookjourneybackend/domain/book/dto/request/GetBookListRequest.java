package com.example.bookjourneybackend.domain.book.dto.request;

import com.example.bookjourneybackend.domain.book.domain.GenreType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GetBookListRequest {

    private String searchTerm;

    private GenreType genreType;

    private String queryType;

    private int page;

    @Builder
    public GetBookListRequest(String searchTerm, String genre, String queryType, int page) {
        this.searchTerm = searchTerm;
        this.genreType = GenreType.fromGenreType(genre);
        this.queryType = queryType;
        this.page = page;
    }

    public GetBookListRequest IncreasePage() {
        page++;
        return this;
    }
}
