package com.example.bookjourneybackend.domain.record.dto.request;

import com.example.bookjourneybackend.domain.record.domain.RecordType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRecordRequest {

    @NotNull
    private RecordType recordType;

    private String recordTitle; // 전체 기록일 때 필수
    private Integer recordPage; // 페이지 기록일 때 필수

    @NotNull
    private String content;


}
