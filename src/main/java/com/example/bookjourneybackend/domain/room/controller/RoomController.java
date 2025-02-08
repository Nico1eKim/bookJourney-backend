package com.example.bookjourneybackend.domain.room.controller;

import com.example.bookjourneybackend.domain.room.dto.request.PostRoomCreateRequest;
import com.example.bookjourneybackend.domain.room.dto.response.*;
import com.example.bookjourneybackend.domain.room.service.RoomService;
import com.example.bookjourneybackend.global.annotation.LoginUserId;
import com.example.bookjourneybackend.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/{roomId}")
    public BaseResponse<GetRoomDetailResponse> getRoomDetail(@PathVariable("roomId") final Long roomId,
                                                             @LoginUserId final Long userId
    ) {

        return BaseResponse.ok(roomService.showRoomDetails(roomId, userId));
    }

    @GetMapping("/{roomId}/info")
    public BaseResponse<GetRoomInfoResponse> getRoomInfo(@PathVariable("roomId") final Long roomId,
                                                         @LoginUserId final Long userId) {

        return BaseResponse.ok(roomService.showRoomInfo(roomId, userId));
    }

    @GetMapping("/search")
    public BaseResponse<GetRoomSearchResponse> searchRooms(
            @RequestParam(required = true) final String searchTerm,
            @RequestParam(required = true, defaultValue = "책 제목") final String searchType,
            @RequestParam(required = false) final String genre,
            @RequestParam(required = false) final String recruitStartDate,
            @RequestParam(required = false) final String recruitEndDate,
            @RequestParam(required = false) final String roomStartDate,
            @RequestParam(required = false) final String roomEndDate,
            @RequestParam(required = false) final Integer recordCount,
            @RequestParam(required = true, defaultValue = "0") final Integer page,
            @LoginUserId final Long userId
    ) {
        return BaseResponse.ok(
                roomService.searchRooms(searchTerm, searchType, genre, recruitStartDate, recruitEndDate, roomStartDate, roomEndDate, recordCount, page, userId)
        );
    }

    @PostMapping
    public BaseResponse<PostRoomCreateResponse> createRoom(@RequestBody @Valid final PostRoomCreateRequest postRoomCreateRequest,
                                                           @LoginUserId final Long userId) {
        return BaseResponse.ok(roomService.createRoom(postRoomCreateRequest, userId));
    }

    @GetMapping("/records")
    public BaseResponse<GetRoomActiveResponse> viewActiveRooms(@RequestParam(required = false) final String sort,
                                                               @LoginUserId final Long userId) {
        return BaseResponse.ok(roomService.searchActiveRooms(sort, userId));
    }

    @PutMapping("/{roomId}/records")
    public BaseResponse<Void> deleteActiveRooms(@PathVariable("roomId") final Long roomId,
                                                @LoginUserId final Long userId) {
        return BaseResponse.ok(roomService.putRoomsInactive(roomId, userId));
    }

    @PutMapping("/{roomId}/exit")
    public BaseResponse<Void> exitRoom(@PathVariable("roomId") final Long roomId,
                                       @LoginUserId final Long userId) {
        return BaseResponse.ok(roomService.exitRoom(roomId, userId));
    }

    @PostMapping("{roomId}")
    public BaseResponse<PostJoinRoomResponse> joinRoom(
            @PathVariable("roomId") final Long roomId,
            @RequestParam(value = "password", required = false) final Integer password,
            @LoginUserId final Long userId
    ) {
        return BaseResponse.ok(roomService.joinRoom(roomId, userId, password));
    }

    @GetMapping("/recruitments")
    public BaseResponse<GetRoomRecruitmentResponse> viewRecruitmentRooms(
    ) {
        return BaseResponse.ok(roomService.searchRecruitmentRooms());
    }

    @GetMapping("/{roomId}/pages")
    public BaseResponse<GetRoomPagesResponse> viewPages(
            @PathVariable("roomId") final Long roomId,
            @LoginUserId final Long userId
    ) {
        return BaseResponse.ok(roomService.showRoomPages(roomId, userId));
    }

    @GetMapping("/search/{roomId}")
    public BaseResponse<GetSearchPrivateRoomResponse> viewSearchPrivateRooms(
            @PathVariable("roomId") final Long roomId
    ) {
        return BaseResponse.ok(roomService.showSearchPrivateRooms(roomId));
    }
}
