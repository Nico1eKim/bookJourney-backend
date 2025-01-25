package com.example.bookjourneybackend.domain.room.controller;

import com.example.bookjourneybackend.domain.room.dto.response.GetRoomDetailResponse;
import com.example.bookjourneybackend.domain.room.dto.response.GetRoomInfoResponse;
import com.example.bookjourneybackend.domain.room.dto.response.GetRoomSearchResponse;
import com.example.bookjourneybackend.domain.room.service.RoomService;
import com.example.bookjourneybackend.global.response.BaseResponse;
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
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String recruitStartDate,
            @RequestParam(required = false) String recruitEndDate,
            @RequestParam(required = false) String roomStartDate,
            @RequestParam(required = false) String roomEndDate,
            @RequestParam(required = false) Integer recordCount,
            @RequestParam(required = false) Integer page
    ) {
        return BaseResponse.ok(
                roomService.searchRooms(searchTerm, genre, recruitStartDate, recruitEndDate, roomStartDate, roomEndDate, recordCount, page)
        );
    }
}
