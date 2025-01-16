package com.example.bookjourneybackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BookJourneyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookJourneyBackendApplication.class, args);
    }

}
