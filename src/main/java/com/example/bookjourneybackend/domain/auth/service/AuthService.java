package com.example.bookjourneybackend.domain.auth.service;

import com.example.bookjourneybackend.domain.auth.domain.dto.request.PostAuthLoginRequest;
import com.example.bookjourneybackend.domain.auth.domain.dto.response.PostAuthLoginResponse;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.example.bookjourneybackend.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.example.bookjourneybackend.global.entity.EntityStatus.ACTIVE;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_EMAIL;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.INVALID_PASSWORD;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public PostAuthLoginResponse login(PostAuthLoginRequest authLoginRequest) {
        log.info("[AuthService.login]");

        String email = authLoginRequest.getEmail();
        String password = authLoginRequest.getPassword();

        User user = userRepository.findByEmailAndStatus(email,ACTIVE)
                .orElseThrow(()-> new GlobalException(CANNOT_FOUND_EMAIL));

        // 암호화된 password를 디코딩한 값과 입력한 패스워드 값이 다르면 null 반환
        // 회원가입시 암호화된 비밀번호 저장하는것으로 리펙토링
        //if(!passwordEncoder.matches(password,user.getPassword()))
        if(!user.getPassword().equals(password)) {
            log.info(password);
            log.info(user.getPassword());
            throw new GlobalException(INVALID_PASSWORD);
        }

        String accessToken = jwtUtil.createAccessToken(user.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());
        tokenService.storeRefreshToken(refreshToken, user.getUserId());

        return  PostAuthLoginResponse.of(user.getUserId(),accessToken,refreshToken);
    }


}
