package com.example.bookjourneybackend.domain.room.domain;


import com.example.bookjourneybackend.global.exception.GlobalException;
import lombok.Getter;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.INVALID_SEARCH_TYPE;

@Getter
public enum SearchType {

    ROOM_NAME("방 이름"),
    BOOK_TITLE("책 제목"),
    AUTHOR_NAME("작가 이름");

    private final String description;

    SearchType(String description) {
        this.description = description;
    }

    public static SearchType from(String description) throws GlobalException {
        for (SearchType type : SearchType.values()) {
            if (type.description.equals(description)) {
                return type;
            }
        }
        throw new GlobalException(INVALID_SEARCH_TYPE);
    }

    public String toDescription() {
        return this.description;
    }
}