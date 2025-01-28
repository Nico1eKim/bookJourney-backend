package com.example.bookjourneybackend.domain.room.domain;

import com.example.bookjourneybackend.global.exception.GlobalException;
import lombok.Getter;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.INVALID_SORT_TYPE;

@Getter
public enum SortType {
    LASTEST("최신순"),
    PROGRESS("진행도순");

    private final String sortType;

    SortType(String sortType) {
        this.sortType = sortType;
    }

    public static SortType from(String sortType) {
        for (SortType type : SortType.values()) {
            if (type.getSortType().equals(sortType)) {
                return type;
            }
        }
        throw new GlobalException(INVALID_SORT_TYPE);
    }
}
