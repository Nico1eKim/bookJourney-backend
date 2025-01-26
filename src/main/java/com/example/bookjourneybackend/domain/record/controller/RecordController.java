package com.example.bookjourneybackend.domain.record.controller;

import com.example.bookjourneybackend.domain.record.dto.request.PostRecordRequest;
import com.example.bookjourneybackend.domain.record.dto.response.PostRecordResponse;
import com.example.bookjourneybackend.domain.record.service.RecordService;
import com.example.bookjourneybackend.global.annotation.LoginUserId;
import com.example.bookjourneybackend.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/records")
public class RecordController {

    private final RecordService recordService;

    @PostMapping("/{roomId}")
    public BaseResponse<PostRecordResponse> createRecord(
            @PathVariable Long roomId,
            @RequestBody @Valid PostRecordRequest postRecordRequest,
            @LoginUserId final Long userId
    ) {
        return BaseResponse.ok(recordService.createRecord(postRecordRequest, roomId, userId));
    }
}
