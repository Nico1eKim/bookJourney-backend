package com.example.bookjourneybackend.domain.user.domain;

import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;

@Getter
public enum CollectorNicknameType {

    FIRST(1, "책산책 여정 시작"),
    FIFTIETH(50, "기록 한 걸음"),
    HUNDREDTH(100, "한 글자 한 글자"),
    TWO_HUNDREDTH(200, "독서 탐험"),
    THREE_HUNDREDTH(300, "생각 한 줄"),
    FOUR_HUNDREDTH(400, "문장 수집"),
    FIVE_HUNDREDTH(500, "독서 행진"),
    SIX_HUNDREDTH(600, "감상문의 정석"),
    SEVEN_HUNDREDTH(700, "기록의 힘"),
    EIGHT_HUNDREDTH(800, "문장의 완성"),
    NINE_HUNDREDTH(900, "지식의 창고"),
    THOUSANDTH(1000, "독서 음유시인");

    private final int threshold;
    private final String collectorNicknameType;

    CollectorNicknameType(int threshold, String collectorNicknameType) {
        this.threshold = threshold;
        this.collectorNicknameType = collectorNicknameType;
    }

    public static CollectorNicknameType getTitleByRecordCount(int recordCount) {
        return Arrays.stream(values())
                .filter(type -> recordCount >= type.threshold)
                .max(Comparator.comparingInt(type -> type.threshold))
                .orElse(null);
    }
}
