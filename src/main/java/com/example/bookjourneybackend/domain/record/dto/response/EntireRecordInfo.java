package com.example.bookjourneybackend.domain.record.dto.response;

import com.example.bookjourneybackend.domain.record.domain.Record;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.UserImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class EntireRecordInfo {
    private Long userId;
    private Long recordId;
    private String imageUrl;
    private String nickName;
    private String recordName;
    private Integer bookPage;
    private String createdAt;
    private String content;
    private Integer commentCount;
    private Integer recordLikeCount;
    private boolean isLike;
}
