package com.example.bookjourneybackend.domain.recentSearch.domain.repository;

import com.example.bookjourneybackend.domain.recentSearch.domain.RecentSearch;
import com.example.bookjourneybackend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecentSearchRepository extends JpaRepository<RecentSearch, Long> {
    Optional<List<RecentSearch>>  findTop12ByUserOrderByModifiedAtDesc(User user);
    Optional<List<RecentSearch>>  findByUser(User user);
    Optional<RecentSearch> findByUserAndRecentSearchId(User user, Long recentSearchId);
    Optional<RecentSearch> findByUserAndRecentSearch(User user, String recentSearch);

    Optional<Integer> countRecentSearchByUser(User user);

    Optional<RecentSearch> findTop1ByUserOrderByModifiedAtAsc(User user);
}
