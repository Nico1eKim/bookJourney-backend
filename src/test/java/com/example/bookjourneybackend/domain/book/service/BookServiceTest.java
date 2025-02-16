package com.example.bookjourneybackend.domain.book.service;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import com.example.bookjourneybackend.domain.book.dto.request.GetBookListRequest;
import com.example.bookjourneybackend.domain.book.dto.response.GetBookBestSellersResponse;
import com.example.bookjourneybackend.domain.book.dto.response.GetBookInfoResponse;
import com.example.bookjourneybackend.domain.book.dto.response.GetBookListResponse;
import com.example.bookjourneybackend.domain.book.dto.response.GetBookPopularResponse;
import com.example.bookjourneybackend.domain.favorite.domain.Favorite;
import com.example.bookjourneybackend.domain.favorite.domain.repository.FavoriteRepository;
import com.example.bookjourneybackend.domain.recentSearch.service.RecentSearchService;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.room.domain.repository.RoomRepository;
import com.example.bookjourneybackend.domain.user.domain.FavoriteGenre;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.FavoriteGenreRepository;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.domain.userRoom.domain.UserRoom;
import com.example.bookjourneybackend.domain.userRoom.domain.repository.UserRoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.example.bookjourneybackend.domain.book.domain.GenreType.*;
import static com.example.bookjourneybackend.domain.room.domain.RoomType.ALONE;
import static com.example.bookjourneybackend.domain.userRoom.domain.UserRole.HOST;
import static com.example.bookjourneybackend.global.entity.EntityStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class BookServiceTest {

    private final String email = "email@email.com";

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private BookCacheService bookCacheService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRoomRepository userRoomRepository;

    @Autowired
    private RecentSearchService recentSearchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FavoriteGenreRepository favoriteGenreRepository;

    @Autowired
    private EntityManager em;

    @BeforeEach
    void beforeEach() {
        User user = User.builder()
                .imageUrl("abcd.jpg")
                .email(email)
                .nickname("장현준")
                .password("secretsecret123")
                .build();

        userRepository.save(user);
        em.flush();
        em.clear();
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        bookRepository.deleteAll();
    }

    @Test
    @DisplayName("알라딘 api 호출이 제대로 되고 책 제목 검색이 제대로 되는지 테스트")
    public void test_searchBook_byTitle() throws Exception {
        //given
        User user = userRepository.findByEmailAndStatus(email, ACTIVE).get();
        String searchTerm = "해리포터";
        GetBookListRequest getBookListRequest = GetBookListRequest.builder()
                .searchTerm(searchTerm)
                .genre(NOVEL_POETRY_DRAMA)
                .queryType("Title")
                .page(1)
                .build();

        //when
        GetBookListResponse response = bookService.searchBook(getBookListRequest, user.getUserId());

        //then
        assertThat(response.getBookList().size()).isEqualTo(10);    //10개의 책이 잘 반환되었는지 확인
        for (int i = 0; i < response.getBookList().size(); i++) {   //모든 검색어가 searchTerm으로 검색된 책인지 확인
            assertThat(response.getBookList().get(i).getBookTitle().replace(" ","")).contains(searchTerm);
        }
    }

    @Test
    @Rollback(value = false)
    @DisplayName("알라딘 api 호출이 제대로 되고 작가 이름 검색이 제대로 되는지 테스트")
    public void test_searchBook_byAuthor() throws Exception {
        //given
        User user = userRepository.findByEmailAndStatus(email, ACTIVE).get();
        String searchTerm = "한강";
        GetBookListRequest request = GetBookListRequest.builder()
                .searchTerm(searchTerm)
                .genre(NOVEL_POETRY_DRAMA)
                .queryType("Author")
                .page(1)
                .build();

        //when

        GetBookListResponse response = bookService.searchBook(request, user.getUserId());

        //then
        assertThat(response.getBookList().size()).isEqualTo(10);    //10개의 책이 잘 반환되었는지 확인
        for (int i = 0; i < response.getBookList().size(); i++) {   //모든 검색어가 searchTerm으로 검색된 책인지 확인
            assertThat(response.getBookList().get(i).getAuthorName().replace(" ","")).contains(searchTerm);
        }
    }

    @Test
    @DisplayName("요청한 isbn이 DB에 존재하지 않을 경우 알라딘 api를 호출하여 해당하는 책 정보를 반환하는지 테스트")
    void test_showBookInfo_isNotExistDB() throws Exception {
        // given
        User user = userRepository.findByEmailAndStatus(email, ACTIVE).get();
        String isbn = "9791193790694";

        // when
        GetBookInfoResponse response = bookService.showBookInfo(isbn, user.getUserId());

        //then
        assertThat(response.getIsbn()).isEqualTo(isbn);
    }
    
    @Test
    @DisplayName("요청한 isbn이 DB에 존재하고 사용자가 이미 즐겨찾기해뒀을 경우 책 정보를 알맞게 반환하는지 테스트")
    void test_showBookInfo_isExistDb() throws Exception {
        //given
        User user = userRepository.findByEmailAndStatus(email, ACTIVE).get();
        String isbn = "9791193790663";
        Book book = Book.builder()
                .bookTitle("해리 포터와 비밀의 방 (양장)")
                .authorName("J.K. 롤링 지음, 강동혁 옮김")
                .isbn(isbn)
                .imageUrl("https://image.aladin.co.kr/product/35493/7/cover150/k562035555_1.jpg")
                .genre(NOVEL_POETRY_DRAMA)
                .publisher("문학수첩")
                .build();

        Favorite favorite = Favorite.builder()
                .book(book)
                .user(user)
                .build();

        //when
        bookRepository.save(book);
        favoriteRepository.save(favorite);
        GetBookInfoResponse response = bookService.showBookInfo(isbn, user.getUserId());

        //then
        assertThat(response.getIsbn()).isEqualTo(isbn);
        assertThat(response.isFavorite()).isTrue();
        assertThat(response.getBookTitle()).isEqualTo(book.getBookTitle());
        assertThat(response.getAuthorName()).isEqualTo(book.getAuthorName());
        assertThat(response.getImageUrl()).isEqualTo(book.getImageUrl());
        assertThat(response.getGenre()).isEqualTo(book.getGenre().getGenreType());
        assertThat(response.getPublisher()).isEqualTo(book.getPublisher());
    }

    @Test
    @DisplayName("읽기횟수가 가장 많은 책을 알맞게 조회하는지 테스트")
    public void test_showPopularBook() throws Exception {
        //given
        User user = userRepository.findByEmailAndStatus(email, ACTIVE).get();

        Book book = Book.builder()
                .bookTitle("해리 포터와 불의 잔 2 (양장)")
                .authorName("J.K. 롤링 지음, 강동혁 옮김")
                .isbn("9791193790694")
                .imageUrl("https://image.aladin.co.kr/product/35775/5/cover150/k232036847_1.jpg")
                .genre(NOVEL_POETRY_DRAMA)
                .publisher("문학수첩")
                .pageCount(200)
                .build();

        Room room = Room.builder()
                .roomName("room")
                .isPublic(false)
                .recruitCount(1)
                .roomType(ALONE)
                .roomPercentage(0.0)
                .startDate(LocalDate.ofEpochDay(2025-02-13))
                .book(book)
                .build();

        UserRoom userRoom = UserRoom.builder()
                .room(room)
                .user(user)
                .userPercentage(0.0)
                .userRole(HOST)
                .currentPage(0)
                .build();

        //when
        bookRepository.save(book);
        roomRepository.save(room);
        userRoomRepository.save(userRoom);
        GetBookPopularResponse response = bookService.showPopularBook();

        //then
        assertThat(response.getBookTitle()).isEqualTo(book.getBookTitle());
        assertThat(response.getAuthorName()).isEqualTo(book.getAuthorName());
        assertThat(response.getImageUrl()).isEqualTo(book.getImageUrl());
        assertThat(response.getDescription()).isEqualTo(book.getDescription());
        assertThat(response.getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("사용자의 관심장르에 따른 베스트셀러를 알맞게 조회하는지 테스트")
    void test_showBestSeller() throws Exception {
        //given
        User user = userRepository.findByEmailAndStatus(email, ACTIVE).get();
        Book book = Book.builder()
                .bookTitle("해리 포터와 비밀의 방 (양장)")
                .authorName("J.K. 롤링 지음, 강동혁 옮김")
                .isbn("9791193790663")
                .imageUrl("https://image.aladin.co.kr/product/35493/7/cover150/k562035555_1.jpg")
                .genre(NOVEL_POETRY_DRAMA)
                .publisher("문학수첩")
                .bestSeller(true)
                .build();

        FavoriteGenre favoriteGenre = FavoriteGenre.builder()
                .book(book)
                .genre(NOVEL_POETRY_DRAMA)
                .user(user)
                .build();

        //when
        bookRepository.save(book);
        favoriteGenreRepository.save(favoriteGenre);

        GetBookBestSellersResponse response = bookService.showBestSellers(user.getUserId());

        //then
        assertThat(response.getBestSellerList().size()).isEqualTo(1);
        assertThat(response.getBestSellerList().get(0).getImageUrl()).isEqualTo(book.getImageUrl());
    }

}