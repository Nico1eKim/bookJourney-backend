package com.example.bookjourneybackend.domain.recentSearch.domain.repository;

import com.example.bookjourneybackend.domain.recentSearch.domain.RecentSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecentSearchRepository extends JpaRepository<RecentSearch, Long> {
}
