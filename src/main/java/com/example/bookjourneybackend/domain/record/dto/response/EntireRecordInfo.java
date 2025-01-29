package com.example.bookjourneybackend.domain.record.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EntireRecordInfo {
    private Long userId;
    private Long recordId;
    private String imageUrl;
    private String nickName;
    private String recordTitle;
    private Integer bookPage;
    private String createdAt;
    private String content;
    private Integer commentCount;
    private Integer recordLikeCount;
    private boolean isLike;
}
