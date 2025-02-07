package com.example.bookjourneybackend.domain.recentSearch.controller;

import com.example.bookjourneybackend.domain.recentSearch.domain.dto.response.GetRecentSearchResponse;
import com.example.bookjourneybackend.domain.recentSearch.service.RecentSearchService;
import com.example.bookjourneybackend.global.annotation.LoginUserId;
import com.example.bookjourneybackend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recent-search")
public class RecentSearchController {

    private final RecentSearchService recentSearchService;

    @GetMapping()
    public BaseResponse<GetRecentSearchResponse> viewRecentSearch(@LoginUserId final Long userId) {
        return BaseResponse.ok(recentSearchService.showRecentSearch(userId));
    }

    @DeleteMapping("/{recentSearchId}")
    public BaseResponse<Void> deleteRecentSearch(@PathVariable("recentSearchId") final Long recentSearchId,@LoginUserId final Long userId) {
        return BaseResponse.ok(recentSearchService.deleteRecentSearch(recentSearchId,userId));
    }

    @DeleteMapping("/all")
    public BaseResponse<Void> deleteRecentSearchAll(@LoginUserId final Long userId) {
        return BaseResponse.ok(recentSearchService.deleteRecentSearchAll(userId));
    }

}
