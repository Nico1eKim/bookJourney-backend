package com.example.bookjourneybackend.domain.readTogether.service;

import com.example.bookjourneybackend.domain.readTogether.dto.response.GetRoomInfoResponse;
import com.example.bookjourneybackend.domain.readTogether.dto.response.RoomData;
import com.example.bookjourneybackend.domain.readTogether.dto.response.RoomMemberInfo;
import com.example.bookjourneybackend.domain.room.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class ReadTogetherService {

    public GetRoomInfoResponse getRoomInfo(Long roomId) {
        // 테스트용 데이터
        RoomMemberInfo member1 = new RoomMemberInfo(UserRole.HOST, "hostImage.jpg", "호스트 닉네임", 50);
        RoomMemberInfo member2 = new RoomMemberInfo(UserRole.MEMBER, "teamImage1.jpg", "팀원1 닉네임", 40);
        RoomMemberInfo member3 = new RoomMemberInfo(UserRole.MEMBER, "teamImage2.jpg", "팀원2 닉네임", 30);

        RoomData roomData = new RoomData(
                roomId,
                "밤의 여행자들",
                "같이 읽기방 제목",
                true,
                30,
                "D-8",
                4,
                6,
                Arrays.asList(member1, member2, member3)
        );

        return new GetRoomInfoResponse(roomData);
    }

}
