package com.example.bookjourneybackend.domain.book.domain;

import com.example.bookjourneybackend.global.exception.GlobalException;
import lombok.Getter;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.*;

@Getter
public enum GenreType {

    NOVEL_POETRY_DRAMA("소설/시/희곡", 1),
    SOCIAL_SCIENCE("사회과학", 798),
    BUSINESS_MANAGEMENT("경제경영", 170),
    HISTORY("역사", 74),
    ART_POP_CULTURE("예술/대중문화", 517),
    RELIGION_ASTROLOGY("종교/역학", 1237),
    HUMANITIES("인문학", 656),
    ESSAY("에세이", 55889),
    GOOD_PARENTING("좋은부모", 2030),
    COMICS("만화", 2551),
    SELF_DEVELOPMENT("자기계발", 336),
    HEALTH_HOBBY("건강/취미/레저", 55890),
    SCIENCE("과학", 987),
    TEENAGERS("청소년", 1137),
    CHILDREN("어린이", 1108),
    INFANTS("유아", 13789);
//    ETC("기타");

    private final String genreType;
    private final int categoryId;

    GenreType(String genreType, int categoryId) {
        this.genreType = genreType;
        this.categoryId = categoryId;
    }

    public static GenreType getGenreType(String genreType) {
        for (GenreType genre : GenreType.values()) {
            if (genre.getGenreType().equals(genreType)) {
                return genre;
            }
        }
        throw new GlobalException(INVALID_GENRE);
    }
}
