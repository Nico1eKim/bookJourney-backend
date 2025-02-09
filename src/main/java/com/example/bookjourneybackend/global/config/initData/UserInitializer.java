package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final List<String> emails = List.of("kanghuijin12@gmail.com", "mintynicole@naver.com","buzz03312@gmail.com"
    ,"philip02020@naver.com","annie2104@naver.com","sjin7629@naver.com","bkbkkw304@gmail.com","cherish_261@naver.com");

    private final List<String> password = List.of("qwer1234", "asdf1234","verysecret123"
            ,"a12345678","kimjoohye123","sjsj1234","password123","alstj1234");

    private final List<String> nickname = List.of("kanghuijin12", "better","hyunjun123"
            ,"heeedragon","rlawngP91","kimkim","konkuk","minsuh");


    public void initializeUsers() {
        for (int i = 0; i < emails.size(); i++) {
            User user = User.builder()
                    .email(emails.get(i))
                    .password(passwordEncoder.encode(password.get(i)))
                    .nickname(nickname.get(i))
                    .build();
            userRepository.save(user);
        }
    }
    //https://book-journey-bucket.s3.eu-north-1.amazonaws.com/ae6d85be-4%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA%202024-12-30%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%206.31.45.png
}
