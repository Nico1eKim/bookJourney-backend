package com.example.bookjourneybackend.global.util;

import com.example.bookjourneybackend.domain.record.domain.Record;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
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

    // 현재 날짜를 기준으로 주차 반환 (ex. ~월 ~주차)
    // 한 주의 시작은 월요일이고, 첫 주에 4일이 포함되어있어야 첫 주 취급 (목/금/토/일) or (월/화/수/목)
    // 마지막 주차의 경우 마지막 날이 월~수 사이이면 다음달 1주차로 계산
    public String getCurrentWeekOfMonth(LocalDate localDate) {
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 4);

        int weekOfMonth = localDate.get(weekFields.weekOfMonth());

        // 첫 주에 해당하지 않는 주의 경우 전 달 마지막 주차로 계산
        if (weekOfMonth == 0) {
            // 전 달의 마지막 날 기준
            LocalDate lastDayOfLastMonth = localDate.with(TemporalAdjusters.firstDayOfMonth()).minusDays(1);
            return getCurrentWeekOfMonth(lastDayOfLastMonth);
        }

        // 이번 달의 마지막 날 기준
        LocalDate lastDayOfMonth = localDate.with(TemporalAdjusters.lastDayOfMonth());
        // 마지막 주차의 경우 마지막 날이 월~수 사이이면 다음달 1주차로 계산
        if (weekOfMonth == lastDayOfMonth.get(weekFields.weekOfMonth()) && lastDayOfMonth.getDayOfWeek().compareTo(DayOfWeek.THURSDAY) < 0) {
            LocalDate firstDayOfNextMonth = lastDayOfMonth.plusDays(1); // 마지막 날 + 1일 => 다음달 1일
            return getCurrentWeekOfMonth(firstDayOfNextMonth);
        }

        return localDate.getMonthValue() + "월 " + weekOfMonth + "주차";
    }

    // 현재 주차를 전달받아 첫째 날과 마지막 날을 반환
    public LocalDate[] getFirstAndLastDayOfWeek(LocalDate now) {
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 4);

        int weekOfMonth = now.get(weekFields.weekOfMonth());

        // 현재 주차를 전달받아 해당 주의 첫째 날과 마지막 날을 반환
        LocalDate firstDayOfWeek = now.with(weekFields.weekOfMonth(), weekOfMonth).with(weekFields.dayOfWeek(), 1);
        LocalDate lastDayOfWeek = now.with(weekFields.weekOfMonth(), weekOfMonth).with(weekFields.dayOfWeek(), 7);

        return new LocalDate[]{firstDayOfWeek, lastDayOfWeek};
    }

}
