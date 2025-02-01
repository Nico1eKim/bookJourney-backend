package com.example.bookjourneybackend.domain.book.service;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import com.example.bookjourneybackend.domain.user.domain.FavoriteGenre;
import com.example.bookjourneybackend.domain.user.domain.repository.FavoriteGenreRepository;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.example.bookjourneybackend.global.util.AladinApiUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BestSellerService {

    private final AladinApiUtil aladinApiUtil;
    private final RestTemplate restTemplate;
    private final BookRepository bookRepository;
    private final FavoriteGenreRepository favoriteGenreRepository;

    @Transactional
    @Async
    public void updateBestsellers() {

        log.info("[BestSellerService.updateBestsellers]");

        //장르별로 베스트셀러 정보 얻어오기
        for (GenreType genre : GenreType.values()) {
            if (genre != GenreType.UNKNOWN) { // UNKNOWN 장르 제외

                // 기존 베스트셀러 중 현재 장르와 일치하는 책 찾기
                Book oldBestSeller = bookRepository.findByBestSellerTrueAndGenre(genre)
                        .orElseThrow(() -> new GlobalException(CANNOT_FOUND_BESTSELLER));

                int categoryId = genre.getCategoryId();
                //베스트셀러 정보 알아오기
                String currentResponse = getBestsellers(categoryId);

                int nthItem = 0; // 첫 번째 책부터 시작

                while (true) {

                    String isbn = aladinApiUtil.getIsbnFromBestSellerResponse(currentResponse, nthItem);

                    // 기존 DB에서 책 찾기
                    Optional<Book> existingBookOptional = bookRepository.findByIsbn(isbn);

                    // 기존 DB에 없으면 저장
                    if (existingBookOptional.isEmpty()) {
                        break;
                    }

                    // 이미 존재하는 책이면 N번째로 이동
                    nthItem++;

                    // N번째 아이템이 없을 경우 종료
                    if (nthItem >= aladinApiUtil.getBESTSELLER_MAX_RESULTS()) {
                        throw new GlobalException(NO_AVAILABLE_BESTSELLER);
                    }
                }

                // 새로운 베스트셀러 책 파싱
                Book newBestSeller = aladinApiUtil.parseAladinApiResponseToBook(currentResponse, true, nthItem);
                bookRepository.save(newBestSeller);

                // 베스트셀러 정보 업데이트
                updateFavoriteGenres(oldBestSeller, newBestSeller);
                deleteOldBestsellers(oldBestSeller);

                }
            }

    }

    /**
     * 기존 관심 장르의 BookId를 새로운 베스트셀러 BookId로 업데이트
     */
    private void updateFavoriteGenres(Book oldBestSeller, Book newBestSeller) {

        // 해당하는 장르에대한 관심 장르 목록 조회
        List<FavoriteGenre> favoriteGenres = favoriteGenreRepository.findByGenre(oldBestSeller.getGenre()).
                orElse(Collections.emptyList());

        // 관심 장르 목록이 없다면 업데이트할 데이터가없으므로 바로 반환(아무 유저도 해당장르를 관심장르로 등록하지않음)
        if (favoriteGenres.isEmpty()) {
            return;
        }

        for (FavoriteGenre favoriteGenre : favoriteGenres) {
            // 기존 베스트셀러를 참조하고 있는 경우만 업데이트
            if (favoriteGenre.getBook().equals(oldBestSeller)) {
                favoriteGenre.setBook(newBestSeller); // 새로운 베스트셀러로 업데이트
            }
        }

        // 변경된 관심 장르 저장
        favoriteGenreRepository.saveAll(favoriteGenres);
    }

    /**
     * 기존 베스트셀러 중 관심 장르에서 더 이상 연결되지 않은 책 삭제
     */
    private void deleteOldBestsellers(Book oldBestSeller) {

        //기존 베스트 셀러 여부 바꾸기
        oldBestSeller.setBestSeller(false);
        bookRepository.save(oldBestSeller);
        //이 책에해당하는 방이 없거나, 이책을 즐겨찾기 한사람이 없으면 db에서 삭제
        if(oldBestSeller.getRooms().isEmpty() && oldBestSeller.getFavorites().isEmpty()) {
            bookRepository.delete(oldBestSeller);
        }
    }


    /**
     * 베스트셀러 정보 얻어오기
     */
    private String getBestsellers(int categoryId) {

        String requestUrl = aladinApiUtil.buildSearchListApiUrl(categoryId);
        String currentResponse;

        try {
            currentResponse = restTemplate.getForEntity(requestUrl, String.class).getBody();
            aladinApiUtil.checkValidatedResponse(currentResponse);
            log.info("알라딘 API 응답 Body: {}", currentResponse);

        } catch (RestClientException e) {
            throw new GlobalException(ALADIN_API_ERROR);
        }

        return currentResponse;
    }




}
