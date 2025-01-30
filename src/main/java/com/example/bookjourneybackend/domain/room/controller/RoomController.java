package com.example.bookjourneybackend.domain.room.controller;

import com.example.bookjourneybackend.domain.room.dto.request.PostJoinRoomRequest;
import com.example.bookjourneybackend.domain.room.dto.request.PostRoomCreateRequest;
import com.example.bookjourneybackend.domain.room.dto.response.*;
import com.example.bookjourneybackend.domain.room.service.RoomService;
import com.example.bookjourneybackend.global.annotation.LoginUserId;
import com.example.bookjourneybackend.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/{roomId}")
    public BaseResponse<GetRoomDetailResponse> getRoomDetail(@PathVariable("roomId") final Long roomId) {

        return BaseResponse.ok(roomService.showRoomDetails(roomId));
    }

    @GetMapping("/{roomId}/info")
    public BaseResponse<GetRoomInfoResponse> getRoomInfo(@PathVariable("roomId") final Long roomId) {

        return BaseResponse.ok(roomService.showRoomInfo(roomId));
    }

    @GetMapping("/search")
    public BaseResponse<GetRoomSearchResponse> searchRooms(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String recruitStartDate,
            @RequestParam(required = false) String recruitEndDate,
            @RequestParam(required = false) String roomStartDate,
            @RequestParam(required = false) String roomEndDate,
            @RequestParam(required = false) Integer recordCount,
            @RequestParam(required = false) Integer page
    ) {
        return BaseResponse.ok(
                roomService.searchRooms(searchTerm, searchType, genre, recruitStartDate, recruitEndDate, roomStartDate, roomEndDate, recordCount, page)
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
            @RequestParam(value = "password", required = false) Integer password,
            @LoginUserId final Long userId
    ) {
        return BaseResponse.ok(roomService.joinRoom(roomId, userId, password));
    }
}
