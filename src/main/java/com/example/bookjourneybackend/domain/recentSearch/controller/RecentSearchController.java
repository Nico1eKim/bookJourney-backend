package com.example.bookjourneybackend.domain.recentSearch.controller;

import com.example.bookjourneybackend.domain.recentSearch.domain.dto.response.GetRecentSearchResponse;
import com.example.bookjourneybackend.domain.recentSearch.service.RecentSearchService;
import com.example.bookjourneybackend.global.annotation.LoginUserId;
import com.example.bookjourneybackend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/recent-search")
public class RecentSearchController {

    private final RecentSearchService recentSearchService;

    @GetMapping()
    public BaseResponse<GetRecentSearchResponse> viewRecentSearch(@LoginUserId final Long userId) {
        log.info("[RecentSearchController.viewRecentSearch]");
        return BaseResponse.ok(recentSearchService.showRecentSearch(userId));
    }

}
