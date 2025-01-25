package com.example.bookjourneybackend.domain.room.controller;

import com.example.bookjourneybackend.domain.room.dto.request.PostRoomCreateRequest;
import com.example.bookjourneybackend.domain.room.dto.response.GetRoomDetailResponse;
import com.example.bookjourneybackend.domain.room.dto.response.GetRoomInfoResponse;
import com.example.bookjourneybackend.domain.room.dto.response.PostRoomCreateResponse;
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

    @PostMapping
    public BaseResponse<PostRoomCreateResponse> createRoom(@RequestBody @Valid final PostRoomCreateRequest postRoomCreateRequest,
                                                           @LoginUserId final Long userId) {
        return BaseResponse.ok(roomService.createRoom(postRoomCreateRequest, userId));
    }
}
