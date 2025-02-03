package com.example.bookjourneybackend.domain.recentSearch.domain.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GetRecentSearchResponse {
    private List<RecentSearchInfo> recentSearchList;

    public GetRecentSearchResponse(List<RecentSearchInfo> recentSearchList) {
        this.recentSearchList = recentSearchList;
    }

    public static GetRecentSearchResponse of(List<RecentSearchInfo> recentSearchList) {
        return new GetRecentSearchResponse(recentSearchList);
    }
}
