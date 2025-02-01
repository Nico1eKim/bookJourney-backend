package com.example.bookjourneybackend.global.util;

import com.example.bookjourneybackend.domain.record.domain.Record;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
public class DateUtil {

    //문자열을 LocalDate로 변환
    public LocalDate parseDate(String date) {
        return date != null ? LocalDate.parse(date) : null;
    }

    //기록들 중 마지막 활동 시간 계산 -> 가장 최근에 수정된 기록의 시간을 반환 (ex. 1분 전, 1시간 전, 1일 전)
    public String calculateLastActivityTime(List<Record> records) {
        return records.stream()
                .map(Record::getModifiedAt)
                .max(LocalDateTime::compareTo)
                .map(this::formatLastActivityTime)
                .orElse("기록 없음");
    }

    //마지막 활동 시간 포맷팅 -> ex. 1분 전, 1시간 전, 1일 전
    public String formatLastActivityTime(LocalDateTime lastModifiedAt) {
        long minutes = Duration.between(lastModifiedAt, LocalDateTime.now()).toMinutes();
        if (minutes < 1) return "방금 전";
        if (minutes < 60) return minutes + "분 전";
        long hours = minutes / 60;
        if (hours < 24) return hours + "시간 전";
        return (hours / 24) + "일 전";
    }

    //날짜 포맷팅 -> ex. 2021.01.01 을 String으로 변환
    public String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return date.format(formatter);
    }

    //D-day 계산
    public String calculateDday(LocalDate endDate) {
        long days = ChronoUnit.DAYS.between(LocalDate.now(), endDate);
        if (days < 0) {
            return "D+" + Math.abs(days);
        }
        return "D-" + days;
    }

    //방의 모집종료 기간 계산
    //방의 모집종료 기간 = {(방의 종료기간 - 방의 시작기간)/2} + 방의 시작기간
    public LocalDate calculateRecruitEndDate(LocalDate startDate, LocalDate progressEndDate) {
        long totalDays = ChronoUnit.DAYS.between(startDate, progressEndDate);
        long halfDays = Math.round(totalDays / 2.0);

        return startDate.plusDays(halfDays);
    }

    //문자열을 LocalDate(2024.11.14 형태)로 변환
    public LocalDate parseDateToLocalDateString(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
    }

    // LocalDateTime을 문자열(2024.11.14 23:04:28 형태)로 변환
    public String formatLocalDateTime(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    //문자열을 LocalDate(2024-11-14 형태)로 변환
    public LocalDate parseDateToLocalDateFromPublishedDateString(String publishedDate) {
        return  LocalDate.parse(publishedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    // LocalDateTime을 문자열(2024년 11월 14일 23시 04분 28초 형태)로 변환
    public String formatLocalDateTimeKorean(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초"));
    }

}
