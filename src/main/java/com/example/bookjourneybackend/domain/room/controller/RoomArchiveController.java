package com.example.bookjourneybackend.domain.room.controller;

import com.example.bookjourneybackend.domain.room.dto.response.GetRoomArchiveResponse;
import com.example.bookjourneybackend.domain.room.service.RoomArchiveService;
import com.example.bookjourneybackend.global.annotation.LoginUserId;
import com.example.bookjourneybackend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms/archive")
public class RoomArchiveController {

    private final RoomArchiveService roomArchiveService;

    @GetMapping
    public BaseResponse<GetRoomArchiveResponse> viewCompletedRooms(@LoginUserId final Long userId,
                                                                   @RequestParam(required = false) final String date
    ) {
        return BaseResponse.ok(roomArchiveService.viewInCompletedRooms(userId, date));
    }
}
