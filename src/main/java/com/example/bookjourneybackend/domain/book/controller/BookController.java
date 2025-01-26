package com.example.bookjourneybackend.domain.book.controller;

import com.example.bookjourneybackend.domain.book.dto.request.GetBookListRequest;
import com.example.bookjourneybackend.domain.book.dto.response.GetBookInfoResponse;
import com.example.bookjourneybackend.domain.book.dto.response.GetBookListResponse;
import com.example.bookjourneybackend.domain.book.dto.response.GetBookPopularResponse;
import com.example.bookjourneybackend.domain.book.service.BookService;
import com.example.bookjourneybackend.global.annotation.LoginUserId;
import com.example.bookjourneybackend.global.response.BaseResponse;
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
    public BaseResponse<GetBookListResponse> viewBookList(final GetBookListRequest getBookListRequest) {
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

}
