package com.example.bookjourneybackend.global.scheduler;

import com.example.bookjourneybackend.domain.book.service.BestSellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Order(2)  // DataLoader보다 나중에 실행되도록 설정
@RequiredArgsConstructor
public class BestsellerUpdateScheduler implements ApplicationRunner {

    private final BestSellerService bestSellerService;

    //매주 월요일 오전 5시에 베스트셀러 리스트 업데이트
    @Scheduled(cron = "0 0 5 ? * MON")
    public void updateBestsellers() {
        bestSellerService.updateBestsellers();
    }

    // 애플리케이션 시작 시 한 번 실행
    @Override
    public void run(ApplicationArguments args) {
//        updateBestsellers();
    }
}
