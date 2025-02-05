package com.example.bookjourneybackend.domain.comment.domain.dto.response;

import com.example.bookjourneybackend.domain.record.dto.response.RecordInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class GetCommentListResponse {

    private List<CommentInfo> comments;
    private RecordInfo recordInfo;

    public GetCommentListResponse(List<CommentInfo> comments, RecordInfo recordInfo) {
        this.comments = comments;
        this.recordInfo = recordInfo;
    }

    public static GetCommentListResponse of(List<CommentInfo> comments, RecordInfo recordInfo) {
        return new GetCommentListResponse(comments, recordInfo);
    }
}
