package com.example.bookjourneybackend.domain.user.service;

import com.example.bookjourneybackend.domain.user.domain.FavoriteGenre;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.dto.request.PostUsersSignUpRequest;
import com.example.bookjourneybackend.domain.user.domain.dto.response.PostUsersSignUpResponse;
import com.example.bookjourneybackend.domain.user.domain.repository.FavoriteGenreRepository;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.global.exception.GlobalException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.bookjourneybackend.global.entity.EntityStatus.ACTIVE;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.ALREADY_EXIST_USER;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final FavoriteGenreRepository favoriteGenreRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public PostUsersSignUpResponse signup(PostUsersSignUpRequest userSignUpRequest,
                                          HttpServletRequest request, HttpServletResponse response) {
        log.info("[UserService.signUp]");
        //이미 등록된 유저인지 찾기
        if(userRepository.findByEmailAndStatus(userSignUpRequest.getEmail(),ACTIVE).isPresent())
            throw new GlobalException(ALREADY_EXIST_USER);

        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userSignUpRequest.getPassword());

        User newUser = User.builder()
                .email(userSignUpRequest.getEmail())
                .password(encodedPassword)
                .nickname(userSignUpRequest.getNickName())
                .build();

        for(FavoriteGenre favoriteGenre : favoriteGenreRepository.findByGenre())
        {

        }

        favoriteGenreR

        favoriteGenreRepository

        userRepository.save(newUser);
        //회원가입할떄 관심장르 테이블 저장
        //db에 저장할때 비밀번호 암호화

        //엑세스,리프레쉬 토큰 발급 및 헤더저장(엑세스토큰),db저장(리프레쉬토큰)


        return PostUsersSignUpResponse.of();

    }
}
