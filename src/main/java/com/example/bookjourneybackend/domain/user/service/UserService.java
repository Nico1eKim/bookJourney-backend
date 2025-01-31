package com.example.bookjourneybackend.domain.user.service;

import com.example.bookjourneybackend.domain.auth.service.RedisService;
import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import com.example.bookjourneybackend.domain.user.domain.EmailContentTemplate;
import com.example.bookjourneybackend.domain.user.domain.FavoriteGenre;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.UserImage;
import com.example.bookjourneybackend.domain.user.domain.dto.request.PostUsersEmailRequest;
import com.example.bookjourneybackend.domain.user.domain.dto.request.PostUsersNicknameValidationRequest;
import com.example.bookjourneybackend.domain.user.domain.dto.request.PostUsersSignUpRequest;
import com.example.bookjourneybackend.domain.user.domain.dto.request.PostUsersVerificationEmailRequest;
import com.example.bookjourneybackend.domain.user.domain.dto.response.PostUsersSignUpResponse;
import com.example.bookjourneybackend.domain.user.domain.dto.response.PostUsersValidationResponse;
import com.example.bookjourneybackend.domain.user.domain.repository.UserImageRepository;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.example.bookjourneybackend.global.util.DateUtil;
import com.example.bookjourneybackend.global.util.JwtAuthenticationFilter;
import com.example.bookjourneybackend.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

import static com.example.bookjourneybackend.domain.user.domain.EmailContentTemplate.AUTH_CODE_EMAIL;
import static com.example.bookjourneybackend.global.entity.EntityStatus.ACTIVE;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtUtil jwtUtil;
    private final DateUtil dateUtil;
    private final RedisService redisService;
    private final MailService mailService;

    private static final int AUTH_CODE_LENGTH = 6;  // 인증번호 길이
    private static final int RANDOM_BOUND = 10;     // 0부터 9까지의 범위

    /**
     * 1. 회원가입 하려는 유저의 비밀번호는 암호화하여 db에 저장
     * 2. 회원가입 할때 선택한 관심장르의 베스트셀러 bookId를 관심장르 테이블에 저장
     * 3. 회원가입 한 유저가 바로 서비스 이용을 할 수 있도록 로그인과 동일하게 토큰 발급 및 인증된 사용자 권한 설정
     * @param userSignUpRequest,request,response
     * @return PostUsersSignUpResponse
     */
    @Transactional
    public PostUsersSignUpResponse signup(PostUsersSignUpRequest userSignUpRequest,
                                          HttpServletRequest request, HttpServletResponse response) {
        log.info("[UserService.signUp]");

        //비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userSignUpRequest.getPassword());

        User newUser = User.builder()
                .email(userSignUpRequest.getEmail())
                .password(encodedPassword)
                .nickname(userSignUpRequest.getNickName())
                .build();

        //TODO s3 연동하고 수정
        UserImage userImage = UserImage.builder()
                .imageUrl(userSignUpRequest.getImageUrl())
                .user(newUser)
                .path("///")
                .size(11)
                .build();

        // 관심 장르 매핑
        userSignUpRequest.getFavoriteGenres().forEach(genre -> {
            FavoriteGenre favoriteGenre = FavoriteGenre.builder()
                    .genre(GenreType.fromGenreType(genre.getGenreName())) //장르 정보 매핑
                    .book(bookRepository.findByBestSellerTrueAndGenre(GenreType.fromGenreType(genre.getGenreName()))
                            .orElseThrow(() -> new GlobalException(CANNOT_FOUND_BESTSELLER))) //책 정보 매핑
                    .build();
            newUser.addFavoriteGenres(favoriteGenre); // 유저 정보 매핑
        });

        userRepository.save(newUser);
        userImageRepository.save(userImage);

        //토큰 발급
        String accessToken = jwtUtil.createAccessToken(newUser.getUserId());
        String refreshToken = jwtUtil.createRefreshToken(newUser.getUserId());

        jwtUtil.setHeaderAccessToken(response,accessToken);
        redisService.storeRefreshToken(refreshToken, newUser.getUserId());

        jwtAuthenticationFilter.setAuthentication(request,newUser.getUserId());

        return PostUsersSignUpResponse.of(newUser.getUserId(),accessToken,refreshToken);

    }

    /**
     * db에서 존재하는 유저중에 해당닉네임이있다면 false반환 없다면 true반환
     * @param NicknameValidationRequest
     * @return PostUsersSignUpResponse
     */
    public PostUsersValidationResponse validateNickname(PostUsersNicknameValidationRequest NicknameValidationRequest) {
        log.info("[UserService.validateNickname]");
        return PostUsersValidationResponse.of(
                !userRepository.existsByNicknameAndStatus(NicknameValidationRequest.getNickName(), ACTIVE));
    }

    /**
     * 1. 이미 가입된 회원인지 검사
     * 2. 가입되지않은 이메일이라면 인증코드 전송
     * @param postUsersEmailRequest
     */
    public Void sendCodeToEmail(PostUsersEmailRequest postUsersEmailRequest) {
        log.info("[UserService.sendCodeToEmail]");

        //이미 가입된 유저인지 검증
        checkDuplicatedEmail(postUsersEmailRequest.getEmail());

        // 인증 코드 생성
        String authCode = createCode();

        // 현재 시간에서 메일 인증유효기간 계산
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(redisService.getAuthCodeExpirationMinutes());
        String formattedExpirationTime = dateUtil.formatDateTimeKorean(expirationTime);

        // 이메일 제목과 본문 생성
        EmailContentTemplate template = AUTH_CODE_EMAIL;

        //이메일 전송
        mailService.sendEmail(postUsersEmailRequest.getEmail(),template.getTitle(),
                template.getContent(authCode, formattedExpirationTime));

        //인증 번호 Redis에 저장
        redisService.storeAuthCode(postUsersEmailRequest.getEmail(), authCode);

        return null;
    }

    private void checkDuplicatedEmail(String email) {
        if(userRepository.existsByEmailAndStatus(email,ACTIVE))
            throw new GlobalException(ALREADY_EXIST_USER);
    }

    private String createCode() {
        int lenth = AUTH_CODE_LENGTH;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < lenth; i++) {
                builder.append(random.nextInt(RANDOM_BOUND));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new GlobalException(NO_SUCH_ALGORITHM);
        }
    }

    /**
     * 1. 레디스에서 이메일로 인증 요청 여부를 찾음
     * 2. 인증 요청 false: 인증 요청 자체를 안한 유저 -> 예외처리
     * 3. 인증 요청 true : 인증번호 검증 로직
     * 4. 인증번호가 만료되었을 경우 예외처리
     * 5. 인증번호 일치하면 true, 일치하지않으면 false 반환
     * @param UsersVerificationEmailRequest
     * @return PostUsersValidationResponse
     */
    @Transactional
    public PostUsersValidationResponse verifiedCode(PostUsersVerificationEmailRequest UsersVerificationEmailRequest) {

        // 이메일로 저장된 인증 코드 조회
        if (!redisService.hasRequestedAuthCode(UsersVerificationEmailRequest.getEmail())) {
            throw new GlobalException(CANNOT_CREATE_EMAIL_AUTH_CODE);  // 인증 요청 자체를 안한 유저
        }

        // 인증 코드 조회 (없으면 만료된 것)
        String storedAuthCode = redisService.getAuthCode(UsersVerificationEmailRequest.getEmail())
                .orElseThrow(() -> new GlobalException(EMAIL_AUTH_CODE_EXPIRED));

        //인증코드 일치 여부 반환
        if (storedAuthCode.equals(UsersVerificationEmailRequest.getCode())) {
            // 인증 성공 후 인증 코드 삭제
            redisService.deleteAuthCode(UsersVerificationEmailRequest.getEmail());
            return PostUsersValidationResponse.of(true);
        }
        return PostUsersValidationResponse.of(false);

    }


}
