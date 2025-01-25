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
    INFANTS("유아", 13789),
    UNKNOWN("알 수 없는 장르", -1);

    private final String genreType;
    private final int categoryId;

    GenreType(String genreType, int categoryId) {
        this.genreType = genreType;
        this.categoryId = categoryId;
    }

    public static GenreType fromGenreType(String genreType) {
        for (GenreType genre : GenreType.values()) {
            if (genre.getGenreType().equals(genreType)) {
                return genre;
            }
        }
        throw new GlobalException(INVALID_GENRE);
    }

    public static GenreType fromCategoryId(int categoryId) {
        for (GenreType genreType : values()) {
            if (genreType.getCategoryId() == categoryId) {
                return genreType;
            }
        }
        return UNKNOWN; // 매칭되지 않는 경우
    }

    /**
     * 알라딘 api에서 넘겨준 categoryName을 GenreType과 매핑하여 알맞은 String 반환
     * 예시) 국내도서>소설/시/희곡>문학의 이해>한국문학론>한국소설론 => 소실/시/희곡
     * @param categoryName
     * @return
     */
    public static GenreType parsingGenreType(String categoryName) {
        for (GenreType genreType : GenreType.values()) {
            if (categoryName.contains(genreType.getGenreType())) {
                return genreType;
            }
        }
        return UNKNOWN;
    }
}
