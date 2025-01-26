package com.example.bookjourneybackend.domain.record.domain;

import com.example.bookjourneybackend.global.exception.GlobalException;
import lombok.Getter;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.INVALID_RECORD_TYPE;

@Getter
public enum RecordType {
    PAGE("페이지 기록"), ENTIRE("전체 기록");

    private final String recordType;

    RecordType(String recordType) {
        this.recordType = recordType;
    }

    public static RecordType from(String recordType) throws GlobalException {
        for (RecordType type : RecordType.values()) {
            if (type.getRecordType().equals(recordType)) {
                return type;
            }
        }
        throw new GlobalException(INVALID_RECORD_TYPE);
    }


}
