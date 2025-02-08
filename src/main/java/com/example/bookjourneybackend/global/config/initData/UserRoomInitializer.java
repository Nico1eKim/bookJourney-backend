package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.favorite.domain.Favorite;
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

import static com.example.bookjourneybackend.global.entity.EntityStatus.EXPIRED;
import static com.example.bookjourneybackend.global.entity.EntityStatus.INACTIVE;

@Component
@RequiredArgsConstructor
public class UserRoomInitializer {

    private final UserRoomRepository userRoomRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final Random random = new Random(); // 랜덤 객체 생성

    //같이읽기 1~20 ACTIVE/INACTIVE 21~31 EXPIRED
    //혼자읽기 32~39 ACTIVE 40~47 INACTIVE 48~55 EXPIRED
    @Transactional
    public void initializeUserRooms() {
        List<User> users = userRepository.findAll();
        List<Room> rooms = roomRepository.findAll();

        int index = 0;
        int flag =0;

        //같이읽기 1~20 ACTIVE/INACTIVE 21~31 EXPIRED
        for (index = 0; index < 31; index++) {

            Room room = rooms.get(index);
            int maxUsers = index < 6 ? room.getRecruitCount() : 7;  //방인원 무조건 7명 방1,2,3,4,5는 각각 2,3,4,5,6명

            for (int j = 0; j < maxUsers; j++) {

                User user = users.get(flag % users.size()); // 사용자들이 돌아가면서 순차적으로 선택
                flag++;

                UserRoom userRoom = UserRoom.builder()
                        .user(user)
                        .room(room)
                        .userRole((j == 0) ? UserRole.HOST : UserRole.MEMBER)
                        .currentPage(0)
                        .userPercentage(0.0)
                        .build();


                if((index < 10 && user.getUserId()%2 ==0) || ((index >= 10  && index < 20) && user.getUserId()%2 !=0)) {
                    userRoom.setStatus(INACTIVE); //1~10번방은 짝수 유저가 INACTIVE 11~20번방은 홀수 유저가 INACTIVE
                    userRoom.setInActivatedAt(LocalDateTime.now());
                }
                if(index>=20)
                    userRoom.setStatus(EXPIRED); //21~31 EXPIRED

                room.addUserRoom(userRoom);
                user.addUserRoom(userRoom);

                userRoomRepository.save(userRoom);
            }

        }

        flag= -1;
        //혼자읽기 32~39 ACTIVE 40~47 INACTIVE 48~55 EXPIRED
        for (index = 31; index < 55; index++) {

            Room room = rooms.get(index);
            flag = (flag + 1) % 8;
            User user = users.get(flag);
                UserRoom userRoom = UserRoom.builder()
                        .user(user)
                        .userRole(UserRole.HOST)
                        .currentPage(0)
                        .userPercentage(0.0)
                        .build();

                if(index >= 39 && index <= 46) {
                    userRoom.setStatus(INACTIVE); //40~47 INACTIVE
                    userRoom.setInActivatedAt(LocalDateTime.now());
                }
                if(index>=47)
                    userRoom.setStatus(EXPIRED); //21~31 EXPIRED

            room.addUserRoom(userRoom);
            user.addUserRoom(userRoom);

            userRoomRepository.save(userRoom);


        }

        }

    }



