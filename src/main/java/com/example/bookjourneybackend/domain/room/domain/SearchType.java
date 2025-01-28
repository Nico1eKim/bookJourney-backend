package com.example.bookjourneybackend.domain.room.domain;


import com.example.bookjourneybackend.global.exception.GlobalException;
import lombok.Getter;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.INVALID_SEARCH_TYPE;

@Getter
public enum SearchType {

    ROOM_NAME("방 이름", ""),
    BOOK_TITLE("책 제목", "Title"),
    AUTHOR_NAME("작가 이름", "Author"),;

    private final String description;
    private final String queryType;     //알라딘 api용

    SearchType(String description, String queryType) {
        this.description = description;
        this.queryType = queryType;
    }

    public static SearchType from(String description) {
        for (SearchType type : SearchType.values()) {
            if (type.description.equals(description)) {
                return type;
            }
        }
        throw new GlobalException(INVALID_SEARCH_TYPE);
    }
}