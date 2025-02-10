package com.example.bookjourneybackend.global.scheduler;

import com.example.bookjourneybackend.domain.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckExpiredScheduler implements ApplicationRunner {

    private final RoomService roomService;

    //매일 자정마다 만료된 방 체크
    @Scheduled(cron = "0 0 0 * * *")
    public void checkExpiredRooms() {
        roomService.checkExpiredRooms();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        checkExpiredRooms();
    }
}
