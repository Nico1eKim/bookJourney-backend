package com.example.bookjourneybackend.global.config.initData;

import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.UserImage;
import com.example.bookjourneybackend.domain.user.domain.repository.UserImageRepository;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserImageInitializer {

    private final UserImageRepository userImageRepository;
    private final UserRepository userRepository;

    public void initializeUserImages() {
        List<User> users = userRepository.findAll();

        for (int i = 0; i < users.size(); i++) {
            UserImage userImage = UserImage.builder()
                    .user(users.get(i))
                    .imageUrl("http://example.com/userImage" + i)
                    .path("/images/user" + i)
                    .size(1024 + i)
                    .build();
            userImageRepository.save(userImage);
        }
    }
}

