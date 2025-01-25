package com.example.bookjourneybackend.domain.room.service;

import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.room.domain.repository.RoomRepository;
import com.example.bookjourneybackend.domain.room.dto.response.*;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.UserImage;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.example.bookjourneybackend.domain.record.domain.Record;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.CANNOT_FIND_ROOM;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public GetRoomDetailResponse showRoomDetails(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new GlobalException(CANNOT_FIND_ROOM));
        List<RoomMemberInfo> members = getRoomMemberInfoList(room);

        LocalDateTime recruitEndDate = room.getRecruitEndDate(); // recruitEndDate를 Room 객체에서 직접 가져옴
        String recruitDday = calculateDday(recruitEndDate); // D-day 계산

        return GetRoomDetailResponse.of(
                room.getRoomName(),
                room.isPublic(),
                calculateLastActivityTime(room.getRecords()),
                room.getRoomPercentage().intValue(),
                formatDate(room.getProgressStartDate()),
                formatDate(room.getProgressEndDate()),
                recruitDday,
                formatDate(recruitEndDate),
                room.getRecruitCount(),
                members
        );

    }

    public GetRoomInfoResponse showRoomInfo(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new GlobalException(CANNOT_FIND_ROOM));
        List<RoomMemberInfo> members = getRoomMemberInfoList(room);

        return GetRoomInfoResponse.of(
                room.getBook().getBookTitle(),
                room.getRoomName(),
                room.isPublic(),
                room.getRoomPercentage().intValue(),
                calculateDday(room.getProgressEndDate()),
                members
        );
    }

    public GetRoomSearchResponse searchRooms(
            String searchTerm,
            String genre,
            String recruitStartDate,
            String recruitEndDate,
            String roomStartDate,
            String roomEndDate,
            Integer recordCount,
            Integer page
    ) {
        // page가 null이면 기본값 0 설정
        int pageNumber = (page != null) ? page : 0;

        GenreType genreType = null;
        if (genre != null && !genre.isEmpty()) {
            genreType = GenreType.getGenreType(genre);
        }

        Slice<Room> rooms = roomRepository.findRoomsByFilters(
                searchTerm,
                genreType,
                recruitStartDate != null ? LocalDate.parse(recruitStartDate) : null,
                recruitEndDate != null ? LocalDate.parse(recruitEndDate) : null,
                roomStartDate != null ? LocalDate.parse(roomStartDate) : null,
                roomEndDate != null ? LocalDate.parse(roomEndDate) : null,
                recordCount,
                PageRequest.of(pageNumber, 10) // 한 페이지당 10개
        );

        List<RoomInfo> roomInfos = rooms.stream().map(room ->
                new RoomInfo(
                        room.getRoomId(),
                        room.isPublic(),
                        room.getBook().getBookTitle(),
                        room.getBook().getAuthorName(),
                        room.getRoomName(),
                        room.getUserRooms().size(),
                        room.getRecruitCount(),
                        room.getRoomPercentage().intValue(),
                        formatDate(room.getProgressStartDate()),
                        formatDate(room.getProgressEndDate())
                )
        ).collect(Collectors.toList());

        return GetRoomSearchResponse.of(roomInfos);
    }

    private List<RoomMemberInfo> getRoomMemberInfoList(Room room) {
        return room.getUserRooms().stream().map(userRoom -> {
            User user = userRoom.getUser();
            return new RoomMemberInfo(
                    userRoom.getUserRole(),
                    Optional.ofNullable(user.getUserImage())
                            .map(UserImage::getImageUrl)
                            .orElse(null),
                    user.getNickname(),
                    userRoom.getUserPercentage().intValue()
            );
        }).collect(Collectors.toList());
    }

    private String calculateLastActivityTime(List<Record> records) {
        Optional<LocalDateTime> lastModifiedAtOpt = records.stream()
                .map(Record::getModifiedAt)
                .max(LocalDateTime::compareTo);

        // 수정 기록이 없는 경우
        if (lastModifiedAtOpt.isEmpty()) {
            return "기록 없음";
        }

        LocalDateTime lastModifiedAt = lastModifiedAtOpt.get();
        long minutes = Duration.between(lastModifiedAt, LocalDateTime.now()).toMinutes();

        if (minutes < 1) {
            return "방금 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else {
            long hours = minutes / 60;
            return hours + "시간 전";
        }
    }

    private String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return date.format(formatter);
    }

    private String calculateDday(LocalDateTime endDate) {
        long days = ChronoUnit.DAYS.between(LocalDateTime.now(), endDate);
        if (days < 0) {
            return "D+" + Math.abs(days);
        }
        return "D-" + days;
    }
}
