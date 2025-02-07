package com.example.bookjourneybackend.domain.room.controller;

import com.example.bookjourneybackend.domain.room.dto.response.GetRoomArchiveResponse;
import com.example.bookjourneybackend.domain.room.service.RoomArchiveService;
import com.example.bookjourneybackend.global.annotation.LoginUserId;
import com.example.bookjourneybackend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.example.bookjourneybackend.global.entity.EntityStatus.EXPIRED;
import static com.example.bookjourneybackend.global.entity.EntityStatus.INACTIVE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms/archive")
public class RoomArchiveController {

    private final RoomArchiveService roomArchiveService;

    @GetMapping
    public BaseResponse<GetRoomArchiveResponse> viewInCompletedRooms(
            @LoginUserId final Long userId,
            @RequestParam(required = false) final Integer month,
            @RequestParam(required = false) final Integer year) {
        return BaseResponse.ok(roomArchiveService.viewArchiveRooms(userId, month, year, INACTIVE));
    }

    @GetMapping("/completed")
    public BaseResponse<GetRoomArchiveResponse> viewCompletedRooms(
            @LoginUserId final Long userId,
            @RequestParam(required = false) final Integer month,
            @RequestParam(required = false) final Integer year) {
        return BaseResponse.ok(roomArchiveService.viewArchiveRooms(userId, month, year, EXPIRED));
    }

}
