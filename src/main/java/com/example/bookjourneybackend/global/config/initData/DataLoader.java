package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import com.example.bookjourneybackend.domain.comment.domain.repository.CommentLikeRepository;
import com.example.bookjourneybackend.domain.comment.domain.repository.CommentRepository;
import com.example.bookjourneybackend.domain.favorite.domain.repository.FavoriteRepository;
import com.example.bookjourneybackend.domain.recentSearch.domain.repository.RecentSearchRepository;
import com.example.bookjourneybackend.domain.record.domain.repository.RecordLikeRepository;
import com.example.bookjourneybackend.domain.record.domain.repository.RecordRepository;
import com.example.bookjourneybackend.domain.room.domain.repository.RoomRepository;
import com.example.bookjourneybackend.domain.user.domain.repository.FavoriteGenreRepository;
import com.example.bookjourneybackend.domain.user.domain.repository.UserImageRepository;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.domain.userRoom.domain.repository.UserRoomRepository;
import jakarta.annotation.PostConstruct;
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
    private final UserImageInitializer userImageInitializer;
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
         userRoomInitializer.initializeUserRooms();
         recordInitializer.initializeRecords();
         commentInitializer.initializeComments();
         favoriteGenreInitializer.initializeFavoriteGenres();
         favoriteInitializer.initializeFavorites();
         recentSearchInitializer.initializeRecentSearches();
         recordLikeInitializer.initializeRecordLikes();
        commentLikeInitializer.initializeCommentLikes();
    }
}
