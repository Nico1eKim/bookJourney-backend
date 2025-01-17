package com.example.bookjourneybackend.domain.record.domain;

import lombok.Getter;

@Getter
public enum RecordType {
    PAGE("페이지 기록"), ENTIRE("전체 기록");

    private String type;

    RecordType(String type) {
        this.type = type;
    }

    public static RecordType from(String type) {
        for (RecordType recordType : RecordType.values()) {
            if (recordType.getType().equals(recordType)) {
                return recordType;
            }
        }
        //TODO 예외 엔티티 작성
        //throw new CustomException(ErrorCode.NO_SUCH_TYPE);
        return null;
    }


}
