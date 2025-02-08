package com.example.bookjourneybackend.global.config.initData;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final BookInitializer bookInitializer;
    private final UserInitializer userInitializer;
    private final RoomInitializer roomInitializer;
    private final RecordInitializer recordInitializer;
    private final CommentInitializer commentInitializer;
    private final FavoriteGenreInitializer favoriteGenreInitializer;
    private final UserRoomInitializer userRoomInitializer;
    private final FavoriteInitializer favoriteInitializer;
    private final RecentSearchInitializer recentSearchInitializer;
    private final RecordLikeInitializer recordLikeInitializer;
    private final CommentLikeInitializer commentLikeInitializer;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        bookInitializer.initializeBooks();
        userInitializer.initializeUsers();
        roomInitializer.initializeRooms();
        recordInitializer.initializeRecords();
        commentInitializer.initializeComments();
        favoriteGenreInitializer.initializeFavoriteGenres();
        userRoomInitializer.initializeUserRooms();
        favoriteInitializer.initializeFavorites();
        recentSearchInitializer.initializeRecentSearches();
        recordLikeInitializer.initializeRecordLikes();
        commentLikeInitializer.initializeCommentLikes();
    }
}
