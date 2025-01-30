package com.example.bookjourneybackend.domain.record.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecordInfo {
    private Long userId;
    private Long recordId;
    private String imageUrl;
    private String nickName;
    private String recordTitle;  // 전체 기록에서 사용
    private Integer recordPage;     // 페이지 기록에서 사용
    private String createdAt;
    private String content;
    private Integer commentCount;
    private Integer recordLikeCount;
    private boolean isLike;

    public static RecordInfo fromPageRecord(Long userId, Long recordId, String imageUrl, String nickName,
                                            Integer recordPage, String createdAt, String content,
                                            Integer commentCount, Integer recordLikeCount, boolean isLike) {
        return RecordInfo.builder()
                .userId(userId)
                .recordId(recordId)
                .imageUrl(imageUrl)
                .nickName(nickName)
                .recordPage(recordPage)
                .createdAt(createdAt)
                .content(content)
                .commentCount(commentCount)
                .recordLikeCount(recordLikeCount)
                .isLike(isLike)
                .build();
    }

    public static RecordInfo fromEntireRecord(Long userId, Long recordId, String imageUrl, String nickName,
                                              String recordTitle, String createdAt, String content,
                                              Integer commentCount, Integer recordLikeCount, boolean isLike) {
        return RecordInfo.builder()
                .userId(userId)
                .recordId(recordId)
                .imageUrl(imageUrl)
                .nickName(nickName)
                .recordTitle(recordTitle)
                .createdAt(createdAt)
                .content(content)
                .commentCount(commentCount)
                .recordLikeCount(recordLikeCount)
                .isLike(isLike)
                .build();
    }
}
