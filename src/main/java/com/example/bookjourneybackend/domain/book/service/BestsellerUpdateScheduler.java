package com.example.bookjourneybackend.domain.book.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Order(2)  // DataLoader보다 나중에 실행되도록 설정
@RequiredArgsConstructor
@Slf4j
public class BestsellerUpdateScheduler implements ApplicationRunner {

    private final BestSellerService bestSellerService;

    //매주 월요일 00시에 베스트셀러 리스트 업데이트
    //TODO 베스트셀러 한주기준 월요일인지 일요일인지 제대로 픽스 --> 일요일 00시 아님/ 월요일 00시인지 확인
    @Scheduled(cron = "0 0 0 ? * MON")
    public void updateBestsellers() {
        log.info("[BestsellerUpdateScheduler.updateBestsellers]");
        bestSellerService.updateBestsellers();
        log.info("completed [BestsellerUpdateScheduler.updateBestsellers]");
    }

    // 애플리케이션 시작 시 한 번 실행
    @Override
    public void run(ApplicationArguments args) {
        log.info("[BestsellerUpdateScheduler.run] 최초 실행 - 베스트셀러 업데이트");
        updateBestsellers();
    }
}