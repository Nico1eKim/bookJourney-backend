package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.recentSearch.domain.RecentSearch;
import com.example.bookjourneybackend.domain.recentSearch.domain.repository.RecentSearchRepository;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import jakarta.transaction.Transactional;

import java.util.Random;

@Component
@RequiredArgsConstructor
public class RecentSearchInitializer {

    private final RecentSearchRepository recentSearchRepository;
    private final UserRepository userRepository;
    Random random = new Random();
    List<String> bookTitles = List.of(
            "어린 왕자", "해리 포터와 마법사의 돌", "셜록 홈즈의 모험", "1984", "데미안",
            "호밀밭의 파수꾼", "모비 딕", "오만과 편견", "죄와 벌", "위대한 개츠비",
            "반지의 제왕", "이방인", "안나 카레니나", "노인과 바다", "백년의 고독",
            "달과 6펜스", "제인 에어", "드라큘라", "데카메론", "레 미제라블",
            "어둠의 심연", "유토피아", "폭풍의 언덕", "오셀로", "햄릿",
            "파우스트", "변신", "마의 산", "별의 계승자", "지하로부터의 수기"
    );


    @Transactional // 트랜잭션 추가
    public void initializeRecentSearches() {
        List<User> users = userRepository.findAll(); // User 리스트 로드

            for (User user : users) {
                int recentSearchCount = random.nextInt(12) + 1 ;//사용자당 검색어기록 1~12개 사이 랜덤
                for (int i = 0; i < recentSearchCount; i++) {

                    String randomSearch = bookTitles.get(random.nextInt(bookTitles.size()));

                    RecentSearch recentSearch = RecentSearch.builder()
                            .user(user)
                            .recentSearch(randomSearch) // 랜덤 책 제목을 검색어로 저장
                            .build();

                    user.addRecentSearch(recentSearch);
                    recentSearchRepository.save(recentSearch);
                }
            }


    }
}
