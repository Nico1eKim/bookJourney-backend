package com.example.bookjourneybackend.domain.recentSearch.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecentSearchInfo {
    private Long recentSearchId;
    private String recentSearch;
}
