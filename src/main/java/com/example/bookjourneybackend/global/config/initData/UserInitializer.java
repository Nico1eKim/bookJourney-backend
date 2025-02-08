package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void initializeUsers() {
        for (int i = 1; i <= 50; i++) {
            User user = User.builder()
                    .email("user" + i + "@example.com")
                    .password(passwordEncoder.encode("password" + i))
                    .nickname("User" + i)
                    .imageUrl("https://book-journey-bucket.s3.eu-north-1.amazonaws.com/ae6d85be-4%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202024-12-30%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%206.31.45.png")
                    .build();
            userRepository.save(user);
        }
    }
}
