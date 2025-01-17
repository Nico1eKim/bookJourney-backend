package com.example.bookjourneybackend.domain.book.domain;

import lombok.Getter;

@Getter
public enum GenreType {

    NOVEL_POETRY_DRAMA("소설/시/희곡"),
    SOCIAL_SCIENCE("사회과학"),
    BUSINESS_MANAGEMENT("경제경영"),
    HISTORY("역사"),
    ART_POP_CULTURE("예술/대중문화"),
    RELIGION_ASTROLOGY("종교/역학"),
    HUMANITIES("인문학"),
    ESSAY("에세이"),
    GOOD_PARENTING("좋은부모"),
    COMICS("만화"),
    SELF_DEVELOPMENT("자기계발"),
    HEALTH_HOBBY("건강/취미"),
    SCIENCE("과학"),
    TEENAGERS("청소년"),
    CHILDREN_INFANTS("어린이/유아"),
    ETC("기타");

    private String genreType;

    GenreType(String genreType) {
        this.genreType = genreType;
    }

    public static GenreType getGenreType(String genreType) {
        for (GenreType genre : GenreType.values()) {
            if (genre.getGenreType().equals(genreType)) {
                return genre;
            }
        }
        return null;
    }
}
