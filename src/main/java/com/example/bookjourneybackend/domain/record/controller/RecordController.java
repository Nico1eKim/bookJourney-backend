package com.example.bookjourneybackend.domain.record.controller;

import com.example.bookjourneybackend.domain.record.dto.request.PostRecordRequest;
import com.example.bookjourneybackend.domain.record.dto.response.GetRecordResponse;
import com.example.bookjourneybackend.domain.record.dto.response.PostRecordPageResponse;
import com.example.bookjourneybackend.domain.record.dto.response.PostRecordLikeResponse;
import com.example.bookjourneybackend.domain.record.dto.response.PostRecordResponse;
import com.example.bookjourneybackend.domain.record.service.RecordService;
import com.example.bookjourneybackend.global.annotation.LoginUserId;
import com.example.bookjourneybackend.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/records")
public class RecordController {

    private final RecordService recordService;

    @PostMapping("/{roomId}")
    public BaseResponse<PostRecordResponse> createRecord(
            @PathVariable("roomId") final Long roomId,
            @RequestBody @Valid final PostRecordRequest postRecordRequest,
            @LoginUserId final Long userId
    ) {
        return BaseResponse.ok(recordService.createRecord(postRecordRequest, roomId, userId));
    }

    @GetMapping("/{roomId}/entire")
    public BaseResponse<GetRecordResponse> getEntireRecords(
            @PathVariable("roomId") final Long roomId,
            @LoginUserId final Long userId,
            @RequestParam(value = "sortingType", required = false, defaultValue = "최신 등록순") final String sortingType) {

        return BaseResponse.ok(recordService.showEntireRecords(roomId, userId, sortingType));
    }

    @GetMapping("/{roomId}/page")
    public BaseResponse<GetRecordResponse> getPageRecords(
            @PathVariable("roomId") final Long roomId,
            @LoginUserId final Long userId,
            @RequestParam(value = "sortingType", required = false, defaultValue = "페이지순") final String sortingType,
            @RequestParam(value = "pageStart", required = false) final Integer pageStart,
            @RequestParam(value = "pageEnd", required = false) final Integer pageEnd
    ) {
        return BaseResponse.ok(recordService.showPageRecords(roomId, userId, sortingType, pageStart, pageEnd));
    }

    @PostMapping("/{recordId}/likes")
    public BaseResponse<PostRecordLikeResponse> likesRecord(
            @PathVariable("recordId") final Long recordId,
            @LoginUserId final Long userId
    ) {
        return BaseResponse.ok(recordService.toggleRecordLike(recordId, userId));
    }

    @PostMapping("/{roomId}/pages")
    public BaseResponse<PostRecordPageResponse> createRecordPage(
            @PathVariable("roomId") final Long roomId,
            @LoginUserId final Long userId,
            @RequestParam("currentPage") final Integer currentPage
    ) {
        return BaseResponse.ok(recordService.enterRecordPage(roomId, userId, currentPage));
    }

    @DeleteMapping("/{recordId}")
    public BaseResponse<Void> deleteRecord(
            @PathVariable("recordId") final Long recordId,
            @LoginUserId final Long userId
    ) {
        recordService.deleteRecord(recordId, userId);
        return BaseResponse.ok();
    }

}

