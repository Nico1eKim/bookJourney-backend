package com.example.bookjourneybackend.domain.readTogether.controller;

import com.example.bookjourneybackend.domain.readTogether.dto.response.GetRoomInfoResponse;
import com.example.bookjourneybackend.domain.readTogether.service.ReadTogetherService;
import com.example.bookjourneybackend.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms-together")
public class ReadTogetherController {

    private final ReadTogetherService readTogetherService;

    @GetMapping("/info/{roomId}")
    public BaseResponse<GetRoomInfoResponse> getReadTogetherInfo(@PathVariable Long roomId) {

        return BaseResponse.ok(readTogetherService.getRoomInfo(roomId));
    }
}
