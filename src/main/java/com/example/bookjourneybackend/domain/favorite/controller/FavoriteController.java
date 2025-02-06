package com.example.bookjourneybackend.domain.favorite.controller;

import com.example.bookjourneybackend.domain.favorite.domain.dto.request.DeleteFavoriteSelectedRequest;
import com.example.bookjourneybackend.domain.favorite.domain.dto.response.GetFavoriteListResponse;
import com.example.bookjourneybackend.domain.favorite.domain.dto.response.PostFavoriteAddResponse;
import com.example.bookjourneybackend.domain.favorite.service.FavoriteService;
import com.example.bookjourneybackend.global.annotation.LoginUserId;
import com.example.bookjourneybackend.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{isbn}")
    public BaseResponse<PostFavoriteAddResponse> addFavorite(@PathVariable("isbn") final String isbn, @LoginUserId final Long userId) {
        log.info("[FavoriteController.addFavorite]");
        return BaseResponse.ok(favoriteService.addFavorite(isbn,userId));
    }

    @GetMapping
    public BaseResponse<GetFavoriteListResponse> viewFavoriteList(@LoginUserId final Long userId) {
        log.info("[FavoriteController.viewFavoriteList]");
        return BaseResponse.ok(favoriteService.showFavoriteList(userId));
    }

    @DeleteMapping
    public BaseResponse<Void> deleteSelectedFavorite(@RequestBody final DeleteFavoriteSelectedRequest deleteFavoriteSelectedRequest,
                                                     @LoginUserId final Long userId) {
        log.info("[FavoriteController.deleteSelectedFavorite]");
        return BaseResponse.ok(favoriteService.deleteSelectedFavorite(deleteFavoriteSelectedRequest,userId));
    }

    @DeleteMapping("/{isbn}")
    public BaseResponse<PostFavoriteAddResponse> deleteFavorite(@PathVariable("isbn") final String isbn, @LoginUserId final Long userId) {
        return BaseResponse.ok(favoriteService.deleteFavorite(isbn,userId));
    }
}
