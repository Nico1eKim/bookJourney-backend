package com.example.bookjourneybackend.domain.record.domain;

import com.example.bookjourneybackend.global.exception.GlobalException;
import lombok.Getter;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.INVALID_RECORD_SORT_TYPE;

@Getter
public enum EntireRecordSortType {

    LATEST("최신 등록 순"), MOST_COMMENTS("답글 많은 순");

    private final String entireRecordSortType;

    EntireRecordSortType(String entireRecordSortType) {
        this.entireRecordSortType = entireRecordSortType;
    }

    public static EntireRecordSortType from(String entireRecordSortType) throws GlobalException {
        for (EntireRecordSortType type : EntireRecordSortType.values()) {
            if (type.getEntireRecordSortType().equals(entireRecordSortType)) {
                return type;
            }
        }
        throw new GlobalException(INVALID_RECORD_SORT_TYPE);
    }
}
