package com.example.bookjourneybackend.domain.recentSearch.service;

import com.example.bookjourneybackend.domain.recentSearch.domain.RecentSearch;
import com.example.bookjourneybackend.domain.recentSearch.domain.dto.response.GetRecentSearchResponse;
import com.example.bookjourneybackend.domain.recentSearch.domain.dto.response.RecentSearchInfo;
import com.example.bookjourneybackend.domain.recentSearch.domain.repository.RecentSearchRepository;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_USER;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecentSearchService {

    private final RecentSearchRepository recentSearchRepository;
    private final UserRepository userRepository;

    /**
     * 로그인 한 유저의 최근검색어 리스트 조회
     * @param userId
     * @return GetRecentSearchResponse
     */
    public GetRecentSearchResponse showRecentSearch(Long userId) {
        log.info("[RecentSearchService.showRecentSearch]");

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        //사용자의 최근 검색어 조회
        Optional<List<RecentSearch>> recentSearchList = recentSearchRepository.findByUser(user);

        return getGetRecentSearchResponse(recentSearchList);
    }

    private GetRecentSearchResponse getGetRecentSearchResponse(Optional<List<RecentSearch>> recentSearchList) {

        // 최근 검색어 리스트가 존재하면 RecentSearchInfo로 변환하고, 없다면 빈 리스트 반환
        List<RecentSearchInfo> recentSearchInfoList = recentSearchList
                .map(list -> list.stream()
                        .map(recentSearch -> new RecentSearchInfo
                                (recentSearch.getRecentSearchId(),
                                recentSearch.getRecentSearch()))
                        .collect(Collectors.toList()))
                .orElse(List.of());

        return GetRecentSearchResponse.of(recentSearchInfoList);
    }


}
