package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.room.domain.repository.RoomRepository;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.domain.userRoom.domain.UserRole;
import com.example.bookjourneybackend.domain.userRoom.domain.UserRoom;
import com.example.bookjourneybackend.domain.userRoom.domain.repository.UserRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserRoomInitializer {

    private final UserRoomRepository userRoomRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

    @Transactional // 트랜잭션 추가
    public void initializeUserRooms() {
        List<User> users = userRepository.findAll(); // User 리스트 로드
        List<Room> rooms = roomRepository.findAll(); // Room 리스트 로드

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            Room room = rooms.get(i % rooms.size());

            UserRoom userRoom = UserRoom.builder()
                    .user(user)
                    .room(room)
                    .userRole(i % 2 == 0 ? UserRole.HOST : UserRole.MEMBER)
                    .userPercentage(0.0)
                    .currentPage(1)
                    .build();

            // 연관관계 설정
            room.addUserRoom(userRoom); // Room과 UserRoom의 연관관계 설정
            user.addUserRoom(userRoom); // User와 UserRoom의 연관관계 설정

            // UserRoom 저장
            userRoomRepository.save(userRoom);
        }
    }
}
