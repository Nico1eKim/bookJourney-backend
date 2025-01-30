package com.example.bookjourneybackend.domain.record.domain;

import com.example.bookjourneybackend.global.exception.GlobalException;
import lombok.Getter;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.INVALID_RECORD_SORT_TYPE;

@Getter
public enum RecordSortType {

    LATEST("최신 등록순"), MOST_COMMENTS("답글 많은 순"), PAGE_ORDER("페이지순");

    private final String recordSortType;

    RecordSortType(String recordSortType) {
        this.recordSortType = recordSortType;
    }

    public static RecordSortType from(String recordSortType) throws GlobalException {
        for (RecordSortType type : RecordSortType.values()) {
            if (type.getRecordSortType().equals(recordSortType)) {
                return type;
            }
        }
        throw new GlobalException(INVALID_RECORD_SORT_TYPE);
    }
}
