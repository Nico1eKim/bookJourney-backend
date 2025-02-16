package com.example.bookjourneybackend.domain.book.service;

import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import com.example.bookjourneybackend.domain.book.dto.request.GetBookListRequest;
import com.example.bookjourneybackend.domain.book.dto.response.GetBookInfoResponse;
import com.example.bookjourneybackend.domain.book.dto.response.GetBookListResponse;
import com.example.bookjourneybackend.domain.favorite.domain.repository.FavoriteRepository;
import com.example.bookjourneybackend.domain.recentSearch.service.RecentSearchService;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static com.example.bookjourneybackend.domain.book.domain.GenreType.NOVEL_POETRY_DRAMA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookSearchTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private BookCacheService bookCacheService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecentSearchService recentSearchService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach() {
        objectMapper = new ObjectMapper();
        bookService = new BookService(bookRepository, favoriteRepository, objectMapper, bookCacheService, userRepository, recentSearchService);
    }

    @Test
    @DisplayName("알라딘 api를 통해 책 검색을 했을 때 10개의 책이 반환되고, 캐싱이 올바르게 되고 있는지 테스트")
    void searchBookByAladinApiWithCaching() {
        // given
        Long userId = 1L;
        GetBookListRequest request = GetBookListRequest.builder()
                .searchTerm("해리포터")
                .genre(NOVEL_POETRY_DRAMA)
                .queryType("Title")
                .page(1)
                .build();

        String currentResponse = "{ \"version\" : \"20070901\", \"title\" : \"알라딘 검색결과 - 해리포터\", \"link\" : \"http:\\/\\/www.aladin.co.kr\\/search\\/wsearchresult.aspx?KeyTitle=%c7%d8%b8%ae%c6%f7%c5%cd&amp;SearchTarget=book\", \"pubDate\" : \"Sat, 15 Feb 2025 09:00:22 GMT\", \"imageUrl\" : \"http:\\/\\/www.aladin.co.kr\\/ucl_editor\\/img_secur\\/header\\/2010\\/logo.jpg\", \"totalResults\" : 357, \"startIndex\" : 1, \"itemsPerPage\" : 10, \"query\" : \"해리포터\", \"searchCategoryId\" : 1, \"searchCategoryName\" : \"국내도서&gt;소설/시/희곡\", \"item\" : [  { \"title\" : \"해리 포터와 불의 잔 2 (양장)\", \"link\" : \"http:\\/\\/www.aladin.co.kr\\/shop\\/wproduct.aspx?ItemId=357750553&amp;copyPaper=1&amp;ttbkey=ttbbuzz033122112002&amp;start=api\", \"author\" : \"J.K. 롤링 지음, 강동혁 옮김\", \"pubDate\" : \"2025-02-26\", \"description\" : \"이번에 출간하는 《해리 포터》 시리즈는 지난 2019년에 새로운 번역을 선보인 버전이다.  J.K. 롤링이 작품 속에 이룩해놓은 문학적 성취가 완벽하게 구현되어 있다. 복선과 반전을 선사하는 문학적 장치들을 보다 정교하고 세련되게 다듬었으며, 인물들 사이의 관계나 그들의 숨겨진 비밀 그리고 성격이 도드라지는 말투의 미세한 뉘앙스까지 점검했다.\", \"creator\" : \"aladin\", \"isbn\" : \"K232036847\", \"isbn13\" : \"9791193790694\", \"itemId\" : 357750553, \"priceSales\" : 23850, \"priceStandard\" : 26500, \"stockStatus\" : \"예약판매\", \"mileage\" : 1320, \"cover\":\"https:\\/\\/image.aladin.co.kr\\/product\\/35775\\/5\\/cover150\\/k232036847_1.jpg\", \"categoryId\" : 51120, \"categoryName\" : \"국내도서>소설/시/희곡>판타지/환상문학>외국판타지/환상소설\", \"publisher\":\"문학수첩\", \"customerReviewRank\":0  } , { \"title\" : \"해리 포터와 불의 잔 1 (양장)\", \"link\" : \"http:\\/\\/www.aladin.co.kr\\/shop\\/wproduct.aspx?ItemId=357749531&amp;copyPaper=1&amp;ttbkey=ttbbuzz033122112002&amp;start=api\", \"author\" : \"J.K. 롤링 지음, 강동혁 옮김\", \"pubDate\" : \"2025-02-26\", \"description\" : \"이번에 출간하는 《해리 포터》 시리즈는 지난 2019년에 새로운 번역을 선보인 버전이다.  J.K. 롤링이 작품 속에 이룩해놓은 문학적 성취가 완벽하게 구현되어 있다. 복선과 반전을 선사하는 문학적 장치들을 보다 정교하고 세련되게 다듬었으며, 인물들 사이의 관계나 그들의 숨겨진 비밀 그리고 성격이 도드라지는 말투의 미세한 뉘앙스까지 점검했다.\", \"creator\" : \"aladin\", \"isbn\" : \"K062036847\", \"isbn13\" : \"9791193790687\", \"itemId\" : 357749531, \"priceSales\" : 23850, \"priceStandard\" : 26500, \"stockStatus\" : \"예약판매\", \"mileage\" : 1320, \"cover\":\"https:\\/\\/image.aladin.co.kr\\/product\\/35774\\/95\\/cover150\\/k062036847_1.jpg\", \"categoryId\" : 51120, \"categoryName\" : \"국내도서>소설/시/희곡>판타지/환상문학>외국판타지/환상소설\", \"publisher\":\"문학수첩\", \"customerReviewRank\":0  } , { \"title\" : \"해리 포터와 아즈카반의 죄수 (양장)\", \"link\" : \"http:\\/\\/www.aladin.co.kr\\/shop\\/wproduct.aspx?ItemId=356288081&amp;copyPaper=1&amp;ttbkey=ttbbuzz033122112002&amp;start=api\", \"author\" : \"J.K. 롤링 지음, 강동혁 옮김\", \"pubDate\" : \"2025-02-11\", \"description\" : \"여느 때처럼 괴로운 여름방학을 보내던 해리는 심한 모욕을 받고 화가 난 나머지, 더즐리 이모부의 여동생에게 무단으로 마법을 건다. 집을 뛰쳐나온 해리는 퇴학당할지도 모른다는 생각에 상심하지만, 그를 기다리는 건 더 큰 문제다. 바로 12년 동안 아즈카반이라는 마법사 감옥에 수감되어 있던 악명 높은 살인자, 시리우스 블랙이 탈옥해 해리를 노린다는 소식이다.\", \"creator\" : \"aladin\", \"isbn\" : \"K572036390\", \"isbn13\" : \"9791193790670\", \"itemId\" : 356288081, \"priceSales\" : 24750, \"priceStandard\" : 27500, \"stockStatus\" : \"\", \"mileage\" : 1370, \"cover\":\"https:\\/\\/image.aladin.co.kr\\/product\\/35628\\/80\\/cover150\\/k572036390_1.jpg\", \"categoryId\" : 51120, \"categoryName\" : \"국내도서>소설/시/희곡>판타지/환상문학>외국판타지/환상소설\", \"publisher\":\"문학수첩\", \"customerReviewRank\":0  } , { \"title\" : \"해리 포터와 비밀의 방 (양장)\", \"link\" : \"http:\\/\\/www.aladin.co.kr\\/shop\\/wproduct.aspx?ItemId=354930732&amp;copyPaper=1&amp;ttbkey=ttbbuzz033122112002&amp;start=api\", \"author\" : \"J.K. 롤링 지음, 강동혁 옮김\", \"pubDate\" : \"2025-01-14\", \"description\" : \"\", \"creator\" : \"aladin\", \"isbn\" : \"K562035555\", \"isbn13\" : \"9791193790663\", \"itemId\" : 354930732, \"priceSales\" : 23850, \"priceStandard\" : 26500, \"stockStatus\" : \"\", \"mileage\" : 1320, \"cover\":\"https:\\/\\/image.aladin.co.kr\\/product\\/35493\\/7\\/cover150\\/k562035555_1.jpg\", \"categoryId\" : 51120, \"categoryName\" : \"국내도서>소설/시/희곡>판타지/환상문학>외국판타지/환상소설\", \"publisher\":\"문학수첩\", \"customerReviewRank\":0  } , { \"title\" : \"해리 포터와 마법사의 돌 (양장)\", \"link\" : \"http:\\/\\/www.aladin.co.kr\\/shop\\/wproduct.aspx?ItemId=354930529&amp;copyPaper=1&amp;ttbkey=ttbbuzz033122112002&amp;start=api\", \"author\" : \"J.K. 롤링 지음, 강동혁 옮김\", \"pubDate\" : \"2025-01-14\", \"description\" : \"1997년 영국에서 출간된 이래 《해리 포터》 시리즈는 지금까지 200개국 이상 80개의 언어로 번역되고 출간되어 5억 부 이상을 판매했다. 이번에 출간하는 《해리 포터》 시리즈는 지난 2019년에 새로운 번역을 선보인 버전이다.\", \"creator\" : \"aladin\", \"isbn\" : \"K412035555\", \"isbn13\" : \"9791193790656\", \"itemId\" : 354930529, \"priceSales\" : 23850, \"priceStandard\" : 26500, \"stockStatus\" : \"\", \"mileage\" : 1320, \"cover\":\"https:\\/\\/image.aladin.co.kr\\/product\\/35493\\/5\\/cover150\\/k412035555_1.jpg\", \"categoryId\" : 51120, \"categoryName\" : \"국내도서>소설/시/희곡>판타지/환상문학>외국판타지/환상소설\", \"publisher\":\"문학수첩\", \"customerReviewRank\":0  } , { \"title\" : \"해리 포터와 죽음의 성물 4 (무선)\", \"link\" : \"http:\\/\\/www.aladin.co.kr\\/shop\\/wproduct.aspx?ItemId=352558700&amp;copyPaper=1&amp;ttbkey=ttbbuzz033122112002&amp;start=api\", \"author\" : \"J.K. 롤링 지음, 강동혁 옮김\", \"pubDate\" : \"2024-11-27\", \"description\" : \"이번에 출간하는 《해리 포터》 시리즈는 지난 2019년에 새로운 번역을 선보인 버전이다. 7권 《해리 포터와 죽음의 성물》로 완간된 기존의 《해리 포터》 시리즈는 빈틈없는 소설적 구성과 생생한 캐릭터 그리고 마법 세계를 정교하게 묘사하며 풍부한 상상력이 돋보이면서도 정밀한 세계관을 구축했다.\", \"creator\" : \"aladin\", \"isbn\" : \"K812934145\", \"isbn13\" : \"9791193790632\", \"itemId\" : 352558700, \"priceSales\" : 11700, \"priceStandard\" : 13000, \"stockStatus\" : \"\", \"mileage\" : 650, \"cover\":\"https:\\/\\/image.aladin.co.kr\\/product\\/35255\\/87\\/cover150\\/k812934145_1.jpg\", \"categoryId\" : 51120, \"categoryName\" : \"국내도서>소설/시/희곡>판타지/환상문학>외국판타지/환상소설\", \"publisher\":\"문학수첩\", \"customerReviewRank\":0  } , { \"title\" : \"해리 포터와 죽음의 성물 3 (무선)\", \"link\" : \"http:\\/\\/www.aladin.co.kr\\/shop\\/wproduct.aspx?ItemId=352558655&amp;copyPaper=1&amp;ttbkey=ttbbuzz033122112002&amp;start=api\", \"author\" : \"J.K. 롤링 지음, 강동혁 옮김\", \"pubDate\" : \"2024-11-27\", \"description\" : \"이번에 출간하는 《해리 포터》 시리즈는 지난 2019년에 새로운 번역을 선보인 버전이다. 7권 《해리 포터와 죽음의 성물》로 완간된 기존의 《해리 포터》 시리즈는 빈틈없는 소설적 구성과 생생한 캐릭터 그리고 마법 세계를 정교하게 묘사하며 풍부한 상상력이 돋보이면서도 정밀한 세계관을 구축했다.\", \"creator\" : \"aladin\", \"isbn\" : \"K872934145\", \"isbn13\" : \"9791193790625\", \"itemId\" : 352558655, \"priceSales\" : 11700, \"priceStandard\" : 13000, \"stockStatus\" : \"\", \"mileage\" : 650, \"cover\":\"https:\\/\\/image.aladin.co.kr\\/product\\/35255\\/86\\/cover150\\/k872934145_1.jpg\", \"categoryId\" : 51120, \"categoryName\" : \"국내도서>소설/시/희곡>판타지/환상문학>외국판타지/환상소설\", \"publisher\":\"문학수첩\", \"customerReviewRank\":0  } , { \"title\" : \"해리 포터와 죽음의 성물 2 (무선)\", \"link\" : \"http:\\/\\/www.aladin.co.kr\\/shop\\/wproduct.aspx?ItemId=352558625&amp;copyPaper=1&amp;ttbkey=ttbbuzz033122112002&amp;start=api\", \"author\" : \"J.K. 롤링 지음, 강동혁 옮김\", \"pubDate\" : \"2024-11-27\", \"description\" : \"이번에 출간하는 《해리 포터》 시리즈는 지난 2019년에 새로운 번역을 선보인 버전이다. 7권 《해리 포터와 죽음의 성물》로 완간된 기존의 《해리 포터》 시리즈는 빈틈없는 소설적 구성과 생생한 캐릭터 그리고 마법 세계를 정교하게 묘사하며 풍부한 상상력이 돋보이면서도 정밀한 세계관을 구축했다.\", \"creator\" : \"aladin\", \"isbn\" : \"K822934145\", \"isbn13\" : \"9791193790618\", \"itemId\" : 352558625, \"priceSales\" : 11700, \"priceStandard\" : 13000, \"stockStatus\" : \"\", \"mileage\" : 650, \"cover\":\"https:\\/\\/image.aladin.co.kr\\/product\\/35255\\/86\\/cover150\\/k822934145_1.jpg\", \"categoryId\" : 51120, \"categoryName\" : \"국내도서>소설/시/희곡>판타지/환상문학>외국판타지/환상소설\", \"publisher\":\"문학수첩\", \"customerReviewRank\":0  } , { \"title\" : \"해리 포터와 죽음의 성물 1 (무선)\", \"link\" : \"http:\\/\\/www.aladin.co.kr\\/shop\\/wproduct.aspx?ItemId=352558507&amp;copyPaper=1&amp;ttbkey=ttbbuzz033122112002&amp;start=api\", \"author\" : \"J.K. 롤링 지음, 강동혁 옮김\", \"pubDate\" : \"2024-11-27\", \"description\" : \"이번에 출간하는 《해리 포터》 시리즈는 지난 2019년에 새로운 번역을 선보인 버전이다. 7권 《해리 포터와 죽음의 성물》로 완간된 기존의 《해리 포터》 시리즈는 빈틈없는 소설적 구성과 생생한 캐릭터 그리고 마법 세계를 정교하게 묘사하며 풍부한 상상력이 돋보이면서도 정밀한 세계관을 구축했다.\", \"creator\" : \"aladin\", \"isbn\" : \"K732934145\", \"isbn13\" : \"9791193790601\", \"itemId\" : 352558507, \"priceSales\" : 11700, \"priceStandard\" : 13000, \"stockStatus\" : \"\", \"mileage\" : 650, \"cover\":\"https:\\/\\/image.aladin.co.kr\\/product\\/35255\\/85\\/cover150\\/k732934145_1.jpg\", \"categoryId\" : 51120, \"categoryName\" : \"국내도서>소설/시/희곡>판타지/환상문학>외국판타지/환상소설\", \"publisher\":\"문학수첩\", \"customerReviewRank\":0  } , { \"title\" : \"해리 포터와 혼혈 왕자 4 (무선)\", \"link\" : \"http:\\/\\/www.aladin.co.kr\\/shop\\/wproduct.aspx?ItemId=352558345&amp;copyPaper=1&amp;ttbkey=ttbbuzz033122112002&amp;start=api\", \"author\" : \"J.K. 롤링 지음, 강동혁 옮김\", \"pubDate\" : \"2024-11-27\", \"description\" : \"1997년 영국에서 출간된 이래 《해리 포터》 시리즈는 지금까지 200개국 이상 80개의 언어로 번역되고 출간되어 5억 부 이상을 판매했다. 국내에서도 1999년 《해리 포터와 마법사의 돌》의 출간을 필두로 지금까지 약 1,500만 부가 판매되었으며, 현재에도 독자들에게 변함없는 사랑을 받고 있다.\", \"creator\" : \"aladin\", \"isbn\" : \"K602934145\", \"isbn13\" : \"9791193790595\", \"itemId\" : 352558345, \"priceSales\" : 11700, \"priceStandard\" : 13000, \"stockStatus\" : \"\", \"mileage\" : 650, \"cover\":\"https:\\/\\/image.aladin.co.kr\\/product\\/35255\\/83\\/cover150\\/k602934145_1.jpg\", \"categoryId\" : 51120, \"categoryName\" : \"국내도서>소설/시/희곡>판타지/환상문학>외국판타지/환상소설\", \"publisher\":\"문학수첩\", \"customerReviewRank\":0  } ] };";

        // when
        when(bookCacheService.getCurrentPage(request)).thenReturn(currentResponse);
        when(bookCacheService.cachingBookInfo(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(new GetBookInfoResponse("소설/시/희곡", "https://image.url", "해리 포터와 불의 잔 2 (양장)", "J.K. 롤링", "문학수첩", "2025-02-26", "9791193790694", "설명"));

        GetBookListResponse response = bookService.searchBook(request, userId);

        // then
        assertThat(response.getBookList()).isNotNull();
        assertThat(response.getBookList().size()).isEqualTo(10);
        assertThat(response.getBookList().get(0).getBookTitle()).isEqualTo("해리 포터와 불의 잔 2 (양장)");

        // verify
        verify(recentSearchService, times(1)).addRecentSearch(userId, request.getSearchTerm());
        verify(bookCacheService, times(2)).getCurrentPage(request); // 1번 동기적으로 호출(현재 페이지), 1번 비동기적으로 호출(다음 페이지)
        verify(bookCacheService, times(10)).cachingBookInfo(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
    }


    @Test
    @DisplayName("가장 인기 있는 책이 없을 때 null로 반환 테스트")
    void returnNullIfNotExistPopularBook() {
        // given
        when(bookRepository.findBookWithMostRooms()).thenReturn(Collections.emptyList());

        // when & then
        assertThat(bookService.showPopularBook()).isNull();
    }

}