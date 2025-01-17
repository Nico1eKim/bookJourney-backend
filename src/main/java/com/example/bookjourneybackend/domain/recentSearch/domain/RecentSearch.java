package com.example.bookjourneybackend.domain.recentSearch.domain;

import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recent_search")
@Getter
@NoArgsConstructor
public class RecentSearch extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recent_search_id")
    private Long recentSearchId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "recent_search", nullable = false, length = 255)
    private String recentSearch;

    @Builder
    public RecentSearch(Long recentSearchId, User user, String recentSearch) {
        this.recentSearchId = recentSearchId;
        this.user = user;
        this.recentSearch = recentSearch;
    }
}
