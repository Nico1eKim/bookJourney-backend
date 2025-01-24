package com.example.bookjourneybackend.domain.room.service;

import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.room.domain.repository.RoomRepository;
import com.example.bookjourneybackend.domain.room.dto.response.GetRoomDetailResponse;
import com.example.bookjourneybackend.domain.room.dto.response.GetRoomInfoResponse;
import com.example.bookjourneybackend.domain.room.dto.response.RoomMemberInfo;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.UserImage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public GetRoomInfoResponse showRoomInfo(Long roomId) {
        Room room = findRoomById(roomId);
        List<RoomMemberInfo> members = getRoomMemberInfoList(room);

        return GetRoomInfoResponse.of(
                room.getUserRooms().stream()
                        .filter(userRoom -> "HOST".equals(userRoom.getUserRole().name()))
                        .findFirst()
                        .map(userRoom -> userRoom.getBook().getBookTitle())
                        .orElse("책 이름 없음"),
                room.getRoomName(),
                room.isPublic(),
                room.getRoomPercentage().intValue(),
                calculateDday(room.getProgressEndDate()),
                room.getRecruitCount(),
                room.getRecordCount(),
                members
        );
    }

    private Room findRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 방이 존재하지 않습니다."));
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

    private String calculateDday(LocalDateTime endDate) {
        long days = java.time.temporal.ChronoUnit.DAYS.between(endDate, LocalDateTime.now());
        return "D-" + days;
    }
}
