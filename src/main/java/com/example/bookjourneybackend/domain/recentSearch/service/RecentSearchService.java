package com.example.bookjourneybackend.domain.recentSearch.service;

import com.example.bookjourneybackend.domain.recentSearch.domain.RecentSearch;
import com.example.bookjourneybackend.domain.recentSearch.domain.dto.response.GetRecentSearchResponse;
import com.example.bookjourneybackend.domain.recentSearch.domain.dto.response.RecentSearchInfo;
import com.example.bookjourneybackend.domain.recentSearch.domain.repository.RecentSearchRepository;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.example.bookjourneybackend.global.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.*;

@Service
@Transactional
@RequiredArgsConstructor
public class RecentSearchService {

    private final RecentSearchRepository recentSearchRepository;
    private final UserRepository userRepository;
    private final DateUtil dateUtil;

    /**
     * 로그인 한 유저의 최근검색어 리스트 조회
     * @param userId
     * @return GetRecentSearchResponse
     */
    @Transactional(readOnly = true)
    public GetRecentSearchResponse showRecentSearch(Long userId) {

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        //사용자의 최근 검색어 조회
        Optional<List<RecentSearch>> recentSearchList = recentSearchRepository.findTop12ByUserOrderByModifiedAtDesc(user);

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

    /**
     * 로그인 한 유저의 특정 최근 검색어 삭제
     * @param recentSearchId,userId
     */
    public Void deleteRecentSearch(Long recentSearchId, Long userId) {
        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        //사용자가 삭제하고자 하는 최근 검색어 조회
        RecentSearch recentSearch = recentSearchRepository.findByUserAndRecentSearchId(user, recentSearchId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_RECENT_SEARCH));

        recentSearchRepository.delete(recentSearch);
        return null;
    }

    /**
     * 로그인 한 유저의 최근검색어 전체 삭제
     * @param userId
     */
    public Void deleteRecentSearchAll(Long userId) {

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        //사용자의 최근 검색어 조회
        Optional<List<RecentSearch>> recentSearchList = recentSearchRepository.findByUser(user);

        // 최근 검색어가 존재하면 전체 삭제
        if (recentSearchList.isPresent() && !recentSearchList.get().isEmpty()) {
            recentSearchRepository.deleteAll(recentSearchList.get());
            return null;
        }
        // 존재하지 않으면 예외 처리
        throw new GlobalException(CANNOT_DELETE_RECENT_SEARCH);
    }

    /**
     * 로그인 한 유저의 최근검색어 추가
     * 이미 해당 유저가 최근 검색어를 추가한 경우에는 해당 RecentSearch의 modified_at을 현재 시간으로 업데이트
     * 해당 유저의 최근검색어의 개수가 12개 이상인 경우에는 가장 오래된 최근검색어를 삭제하고 새로운 최근검색어를 추가
     * @param userId, recentSearch
     */
    @Transactional
    public void addRecentSearch(Long userId, String recentSearch) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        //사용자의 최근 검색어 조회
        Optional<RecentSearch> recentSearchOptional = recentSearchRepository.findByUserAndRecentSearch(user, recentSearch);

        // 최근 검색어가 존재하면 modified_at 업데이트하고 끝
        if (recentSearchOptional.isPresent()) {
            recentSearchOptional.get().setModifiedAt(dateUtil.getCurrentTime());
            return;
        }

        // 최근 검색어의 개수가 12개 이상인 경우 가장 오래된 최근 검색어 삭제
        recentSearchRepository.countRecentSearchByUser(user)
                .ifPresent(count -> {
                    if (count >= 12) {
                        recentSearchRepository.findTop1ByUserOrderByModifiedAtAsc(user)
                                .ifPresent(oldRecentSearch -> {
                                    recentSearchRepository.delete(oldRecentSearch);
                                    recentSearchRepository.flush(); // 즉시 삭제
                                });
                    }
                });

        // 최근 검색어가 존재하지 않으면 새로 추가
        RecentSearch newRecentSearch = RecentSearch.builder()
                .user(user)
                .recentSearch(recentSearch)
                .build();

        recentSearchRepository.save(newRecentSearch);
        recentSearchRepository.flush();
    }
}
