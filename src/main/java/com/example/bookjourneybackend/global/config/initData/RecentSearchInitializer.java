package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.recentSearch.domain.RecentSearch;
import com.example.bookjourneybackend.domain.recentSearch.domain.repository.RecentSearchRepository;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RecentSearchInitializer {

    private final RecentSearchRepository recentSearchRepository;
    private final UserRepository userRepository;

    @Transactional // 트랜잭션 추가
    public void initializeRecentSearches() {
        List<User> users = userRepository.findAll(); // User 리스트 로드

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i); // 특정 User 가져오기

            RecentSearch recentSearch = RecentSearch.builder()
                    .user(user)
                    .recentSearch("Search query " + i)
                    .build();

            // 연관관계 설정
            user.addRecentSearch(recentSearch);

            // RecentSearch 저장
            recentSearchRepository.save(recentSearch);
        }
    }
}
