package com.example.bookjourneybackend.domain.user.service;

import com.example.bookjourneybackend.domain.auth.service.TokenService;
import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import com.example.bookjourneybackend.domain.user.domain.FavoriteGenre;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.UserImage;
import com.example.bookjourneybackend.domain.user.domain.dto.request.PostUsersNicknameValidationRequest;
import com.example.bookjourneybackend.domain.user.domain.dto.request.PostUsersSignUpRequest;
import com.example.bookjourneybackend.domain.user.domain.dto.response.PostUsersNicknameValidationResponse;
import com.example.bookjourneybackend.domain.user.domain.dto.response.PostUsersSignUpResponse;
import com.example.bookjourneybackend.domain.user.domain.repository.FavoriteGenreRepository;
import com.example.bookjourneybackend.domain.user.domain.repository.UserImageRepository;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.example.bookjourneybackend.global.util.JwtAuthenticationFilter;
import com.example.bookjourneybackend.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.bookjourneybackend.global.entity.EntityStatus.ACTIVE;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.ALREADY_EXIST_USER;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_BESTSELLER;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserImageRepository userImageRepository;
    private final BookRepository bookRepository;
    private final FavoriteGenreRepository favoriteGenreRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

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
        tokenService.storeRefreshToken(refreshToken, newUser.getUserId());

        jwtAuthenticationFilter.setAuthentication(request,newUser.getUserId());

        return PostUsersSignUpResponse.of(newUser.getUserId(),accessToken,refreshToken);

    }

    /**
     * db에서 존재하는 유저중에 해당닉네임이있다면 false반환 없다면 true반환
     * @param NicknameValidationRequest
     * @return PostUsersSignUpResponse
     */
    public PostUsersNicknameValidationResponse validateNickname(PostUsersNicknameValidationRequest NicknameValidationRequest) {
        log.info("[UserService.validateNickname]");
        return PostUsersNicknameValidationResponse.of(
                !userRepository.existsByNicknameAndStatus(NicknameValidationRequest.getNickName(), ACTIVE));
    }
}
