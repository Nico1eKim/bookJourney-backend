package com.example.bookjourneybackend.domain.record.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostRecordLikeResponse {
    private boolean isLiked;

    public PostRecordLikeResponse(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public static PostRecordLikeResponse of(boolean isLiked) {
        return new PostRecordLikeResponse(isLiked);
    }
}
