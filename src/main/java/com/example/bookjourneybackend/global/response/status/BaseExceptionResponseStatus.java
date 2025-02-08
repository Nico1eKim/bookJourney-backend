package com.example.bookjourneybackend.global.response.status;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;


@RequiredArgsConstructor
public enum BaseExceptionResponseStatus implements ResponseStatus {

    /**
     * 1000: 요청 성공 (OK)
     */
    SUCCESS(2000, OK, "요청에 성공하였습니다."),
    FAILURE(4000, BAD_REQUEST, "요청에 실패하였습니다."),
    NOT_FOUND_API(4040, NOT_FOUND, "존재하지 않는 API입니다."),

    /**
     * 5000 : user 관련
     */
    NO_SUCH_TYPE_USER(5000, BAD_REQUEST, "알맞은 유저 타입을 찾을 수 없습니다."),
    CANNOT_FOUND_USER(5001, BAD_REQUEST, "유저를 찾을 수 없습니다."),
    ALREADY_EXIST_USER(5002, BAD_REQUEST, "이미 회원가입 된 유저입니다."),
    UNABLE_TO_SEND_EMAIL(5003, BAD_REQUEST, "이메일을 전송할 수 없습니다."),
    CANNOT_CREAT_EMAIL(5004, BAD_REQUEST, "이메일을 생성할 수 없습니다."),
    CANNOT_CREATE_EMAIL_AUTH_CODE(5005, BAD_REQUEST, "인증번호를 발급 받지 않은 이메일입니다. 먼저 인증번호를 발급받아주세요."),
    EMAIL_AUTH_CODE_EXPIRED(5006, BAD_REQUEST, "만료된 인증번호 입니다."),
    NO_SUCH_ALGORITHM(5007, BAD_REQUEST, "지정된 난수 생성 알고리즘을 찾을 수 없습니다."),

    /**
     * 6000 : book 관련
     */
    CANNOT_FOUND_BOOK(6000, BAD_REQUEST, "책을 찾을 수 없습니다."),

    INVALID_GENRE(6001, BAD_REQUEST, "알맞은 장르를 찾을 수 없습니다"),
    EMPTY_SEARCH_TERM(6001, BAD_REQUEST, "검색어는 비워둘 수 없습니다."),
    INVALID_PAGE(6001, BAD_REQUEST, "페이지 번호는 0 이상입니다."),

    ALADIN_API_ERROR(6002, BAD_REQUEST, "알라딘 API 호출에 실패하였습니다."),
    ALADIN_API_PARSING_ERROR(6003, BAD_REQUEST, "알라딘 API 응답 파싱에 실패하였습니다."),

    CANNOT_FOUND_POPULAR_BOOK(6004, BAD_REQUEST, "읽기횟수가 가장 많은 책을 찾을 수 없습니다."),

    CANNOT_FOUND_BESTSELLER(6005, BAD_REQUEST, "베스트셀러 책을 찾을 수 없습니다."),
    NO_AVAILABLE_BESTSELLER(6006, BAD_REQUEST, "해당 장르의 베스트셀러 후보 중 기존에 존재하지 않는 책을 찾을 수 없습니다."),

    /**
     * 7000 : auth 관련
     */
    ALREADY_EXIST_EMAIL(7001, BAD_REQUEST, "이미 존재하는 이메일입니다."),
    CANNOT_FOUND_EMAIL(7002, BAD_REQUEST, "이메일이 존재하지 않습니다."),
    INVALID_PASSWORD(7003, BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    NOT_EXIST_TOKEN(7004, BAD_REQUEST, "토큰이 존재하지 않습니다."),
    INVALID_TOKEN(7005, BAD_REQUEST, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(7006, BAD_REQUEST, "만료된 토큰입니다."),

    /**
     * 8000 : room 관련
     */
    CANNOT_FOUND_ROOM(8001, BAD_REQUEST, "방을 찾을 수 없습니다."),
    INVALID_ROOM_TYPE(8002, BAD_REQUEST, "알맞은 방 타입을 찾을 수 없습니다."),
    INVALID_SEARCH_TYPE(8003, BAD_REQUEST, "알맞은 검색 조건을 찾을 수 없습니다."),
    INVALID_SORT_TYPE(8004, BAD_REQUEST, "알맞은 정렬 조건을 찾을 수 없습니다."),

    CANNOT_FOUND_USER_ROOM(8005, BAD_REQUEST, "사용자가 방에 참여해있지 않습니다."),
    NOT_PARTICIPATING_IN_ROOM(8005, BAD_REQUEST, "방에 참여해있지 않습니다."),

    ROOM_NOT_RECRUITING(8006, BAD_REQUEST, "모집 기간이 지난 방입니다."),
    ROOM_FULL(8006, BAD_REQUEST, "모집 인원이 초과된 방입니다."),
    ALREADY_JOINED_ROOM(8006, BAD_REQUEST, "이미 참여한 방입니다."),
    INVALID_ROOM_PASSWORD(8006, BAD_REQUEST, "비밀번호 인증에 실패했습니다."),

    HOST_CANNOT_LEAVE_ROOM(8007, BAD_REQUEST, "방장은 방에서 나갈 수 없습니다."),

    CANNOT_FOUND_RECRUITMENT_ROOM(8008, BAD_REQUEST, "모집 중인 방을 찾을 수 없습니다."),

    CANNOT_WRITE_IN_EXPIRED_ROOM(8009, BAD_REQUEST, "기간이 지난 방에는 기록을 남길 수 없습니다."),
    CANNOT_LIKE_IN_EXPIRED_ROOM(8009, BAD_REQUEST, "기간이 지난 방에는 좋아요를 남길 수 없습니다."),
    CANNOT_ENTER_PAGE_IN_EXPIRED_ROOM(8009, BAD_REQUEST, "기간이 지난 방에는 페이지를 입력할 수 없습니다."),
    CANNOT_COMMENT_IN_EXPIRED_ROOM(8009, BAD_REQUEST, "기간이 지난 방에는 댓글을 남길 수 없습니다."),

    CANNOT_NULL_DATE(8009, BAD_REQUEST, "같이읽기 방 생성시, 기간은 필수 입력값입니다."),
    CANNOT_NULL_PASSWORD(8010, BAD_REQUEST, "비공개 방 생성시, 비밀번호는 필수 입력값입니다."),
    ALREADY_CREATED_ALONE_ROOM(8011, BAD_REQUEST, "이미 해당 책으로 생성된 혼자읽기 방이 존재합니다."),

    ROOM_IS_PUBLIC(8012, BAD_REQUEST, "공개 방입니다."),
    CANNOT_FIND_HOST(8012, BAD_REQUEST, "호스트를 찾을 수 없습니다."),

    /**
     * 9000 : record 관련
     */
    CANNOT_FOUND_RECORD(9001, BAD_REQUEST, "기록을 찾을 수 없습니다."),

    INVALID_RECORD_TYPE(9002, BAD_REQUEST, "알맞은 기록 타입을 찾을 수 없습니다."),
    INVALID_RECORD_PAGE(9002, BAD_REQUEST, "페이지 번호를 입력해주세요."),
    INVALID_RECORD_TITLE(9002, BAD_REQUEST, "기록 제목을 입력해주세요."),
    INVALID_PAGE_NUMBER(9002, BAD_REQUEST, "유효하지 않은 페이지 범위입니다."),

    INVALID_RECORD_SORT_TYPE(9003, BAD_REQUEST, "알맞은 기록 나열 타입을 찾을 수 없습니다."),

    /**
     * 10000 : recentSearch 관련
     */
    CANNOT_FOUND_RECENT_SEARCH(10000, BAD_REQUEST, "최근검색어를 찾을 수 없습니다."),
    CANNOT_DELETE_RECENT_SEARCH(10001, BAD_REQUEST, "최근검색어를 삭제 할 수 없습니다."),

    /**
     * 11000 : favorite 관련
     */
    CANNOT_FAVORITE(11000, BAD_REQUEST, "이미 즐겨찾기 한 책입니다."),
    CANNOT_FOUND_FAVORITE(11001, BAD_REQUEST, "즐겨찾기를 찾을 수 없습니다."),
    NOT_SELECTED_FAVORITE(11002, BAD_REQUEST, "삭제할 즐겨찾기 ID가 선택되지 않았습니다."),
    CANNOT_DELETE_FAVORITE(11003, BAD_REQUEST, "즐겨찾기를 삭제 할 수 없습니다."),

    /**
     * 12000 : comment 관련
     */
    CANNOT_FOUND_COMMENT(12001, BAD_REQUEST, "댓글을 찾을 수 없습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
