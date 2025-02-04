package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.room.domain.repository.RoomRepository;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.domain.userRoom.domain.UserRole;
import com.example.bookjourneybackend.domain.userRoom.domain.UserRoom;
import com.example.bookjourneybackend.domain.userRoom.domain.repository.UserRoomRepository;
import com.example.bookjourneybackend.global.entity.EntityStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class UserRoomInitializer {

    private final UserRoomRepository userRoomRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final Random random = new Random(); // 랜덤 객체 생성

    @Transactional
    public void initializeUserRooms() {
        List<User> users = userRepository.findAll();
        List<Room> rooms = roomRepository.findAll();

        if (users.isEmpty() || rooms.isEmpty()) {
            throw new IllegalStateException("Users 또는 Rooms가 존재하지 않습니다. 초기 데이터를 확인하세요.");
        }

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(random.nextInt(users.size())); // 랜덤한 User 선택
            Room room = rooms.get(i % rooms.size()); // 방 선택 (순환)

            double randomPercentage = Math.round((Math.random() * 100.0) * 10) / 10.0; // 0~100 사이 소수점 첫째 자리까지

            UserRoom userRoom = UserRoom.builder()
                    .user(user)
                    .room(room)
                    .userRole(random.nextBoolean() ? UserRole.HOST : UserRole.MEMBER) // 랜덤한 역할
                    .userPercentage(randomPercentage)
                    .currentPage(1)
                    .build();

            EntityStatus[] statuses = EntityStatus.values();
            userRoom.setStatus(statuses[random.nextInt(statuses.length)]); // 랜덤한 상태
            userRoom.setInActivatedAt(LocalDateTime.now());

            room.addUserRoom(userRoom);
            user.addUserRoom(userRoom);

            userRoomRepository.save(userRoom);
        }
    }
}
