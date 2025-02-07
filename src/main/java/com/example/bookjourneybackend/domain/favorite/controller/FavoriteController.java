package com.example.bookjourneybackend.domain.favorite.controller;

import com.example.bookjourneybackend.domain.favorite.domain.dto.request.DeleteFavoriteSelectedRequest;
import com.example.bookjourneybackend.domain.favorite.domain.dto.response.GetFavoriteListResponse;
import com.example.bookjourneybackend.domain.favorite.domain.dto.response.PostFavoriteAddResponse;
import com.example.bookjourneybackend.domain.favorite.service.FavoriteService;
import com.example.bookjourneybackend.global.annotation.LoginUserId;
import com.example.bookjourneybackend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{isbn}")
    public BaseResponse<PostFavoriteAddResponse> addFavorite(@PathVariable("isbn") final String isbn, @LoginUserId final Long userId) {
        return BaseResponse.ok(favoriteService.addFavorite(isbn,userId));
    }

    @GetMapping
    public BaseResponse<GetFavoriteListResponse> viewFavoriteList(@LoginUserId final Long userId) {
        return BaseResponse.ok(favoriteService.showFavoriteList(userId));
    }

    @DeleteMapping
    public BaseResponse<Void> deleteSelectedFavorite(@RequestBody final DeleteFavoriteSelectedRequest deleteFavoriteSelectedRequest,
                                                     @LoginUserId final Long userId) {
        return BaseResponse.ok(favoriteService.deleteSelectedFavorite(deleteFavoriteSelectedRequest,userId));
    }

    @DeleteMapping("/{isbn}")
    public BaseResponse<PostFavoriteAddResponse> deleteFavorite(@PathVariable("isbn") final String isbn, @LoginUserId final Long userId) {
        return BaseResponse.ok(favoriteService.deleteFavorite(isbn,userId));
    }
}
