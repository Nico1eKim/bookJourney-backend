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
                    .build();
            userRepository.save(user);
        }
    }
}
