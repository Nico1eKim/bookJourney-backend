package com.example.bookjourneybackend.domain.book.controller;

import com.example.bookjourneybackend.domain.book.dto.request.GetBookListRequest;
import com.example.bookjourneybackend.domain.book.dto.response.GetBookBestSellersResponse;
import com.example.bookjourneybackend.domain.book.dto.response.GetBookInfoResponse;
import com.example.bookjourneybackend.domain.book.dto.response.GetBookListResponse;
import com.example.bookjourneybackend.domain.book.dto.response.GetBookPopularResponse;
import com.example.bookjourneybackend.domain.book.service.BestSellerService;
import com.example.bookjourneybackend.domain.book.service.BookService;
import com.example.bookjourneybackend.global.annotation.LoginUserId;
import com.example.bookjourneybackend.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping("/search")
    public BaseResponse<GetBookListResponse> viewBookList(@Valid final GetBookListRequest getBookListRequest) {
        return BaseResponse.ok(bookService.searchBook(getBookListRequest));
    }

    @GetMapping("/info/{isbn}")
    public BaseResponse<GetBookInfoResponse> viewBookInfo(@PathVariable("isbn") final String isbn,
                                                          @LoginUserId final Long userId) {
        return BaseResponse.ok(bookService.showBookInfo(isbn, userId));
    }

    @GetMapping("/popular")
    public BaseResponse<GetBookPopularResponse> viewPopularBook() {
        return BaseResponse.ok(bookService.showPopularBook());
    }

    //사용자별베스트 셀러 조회
    @GetMapping("/best-sellers")
    public BaseResponse<GetBookBestSellersResponse> viewBestSellers(@LoginUserId final Long userId) {
        log.info("[BookController.viewBestSellers]");
        return BaseResponse.ok(bookService.showBestSellers(userId));
    }

}
