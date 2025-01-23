package com.example.bookjourneybackend.domain.room.service;

import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.room.domain.repository.RoomRepository;
import com.example.bookjourneybackend.domain.room.dto.response.GetRoomInfoResponse;
import com.example.bookjourneybackend.domain.room.dto.response.RoomMemberInfo;
import com.example.bookjourneybackend.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public GetRoomInfoResponse getRoomInfo(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 방이 존재하지 않습ㄴ다."));

        // 방(Room)에 소속된 사용자-방(UserRoom) 객체의 리스트를 가져옴
        List<RoomMemberInfo> members = room.getUserRooms().stream().map(userRoom -> {
            User user = userRoom.getUser();

            return new RoomMemberInfo(
                    userRoom.getUserRole(),
                    user.getUserImage() != null ? user.getUserImage().getImageUrl() : null,
                    user.getNickname(),
                    userRoom.getUserPercentage().intValue()
            );
        }).toList();

        return new GetRoomInfoResponse(
                room.getRoomId(),
                room.getUserRooms().stream()
                        .filter(userRoom -> "HOST".equals(userRoom.getUserRole().name())) // 호스트의 책 제목을 가져옴
                        .findFirst()
                        .map(userRoom -> userRoom.getBook().getBookTitle())
                        .orElse("책 이름 없음"),
                room.getRoomName(),
                room.isPublic(),
                room.getRoomPercentage().intValue(),
                "D-" + calculateDaysLeft(room.getProgressEndDate()),
                room.getRecruitCount(),
                room.getRecordCount(),
                members
        );
    }

    private long calculateDaysLeft(LocalDateTime endDate) {
        return ChronoUnit.DAYS.between(LocalDateTime.now(), endDate);
    }
}