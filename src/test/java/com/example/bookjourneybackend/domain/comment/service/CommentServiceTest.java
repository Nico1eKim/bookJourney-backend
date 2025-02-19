package com.example.bookjourneybackend.domain.comment.service;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.book.domain.repository.BookRepository;
import com.example.bookjourneybackend.domain.comment.domain.dto.request.PostCommentRequest;
import com.example.bookjourneybackend.domain.comment.domain.dto.response.PostCommentResponse;
import com.example.bookjourneybackend.domain.comment.domain.repository.CommentLikeRepository;
import com.example.bookjourneybackend.domain.comment.domain.repository.CommentRepository;
import com.example.bookjourneybackend.domain.record.domain.Record;
import com.example.bookjourneybackend.domain.record.domain.repository.RecordRepository;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.room.domain.repository.RoomRepository;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.domain.userRoom.domain.UserRoom;
import com.example.bookjourneybackend.domain.userRoom.domain.repository.UserRoomRepository;
import com.example.bookjourneybackend.global.exception.GlobalException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.example.bookjourneybackend.domain.book.domain.GenreType.NOVEL_POETRY_DRAMA;
import static com.example.bookjourneybackend.domain.record.domain.RecordType.ENTIRE;
import static com.example.bookjourneybackend.domain.room.domain.RoomType.TOGETHER;
import static com.example.bookjourneybackend.domain.userRoom.domain.UserRole.HOST;
import static com.example.bookjourneybackend.global.entity.EntityStatus.EXPIRED;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.CANNOT_COMMENT_IN_EXPIRED_ROOM;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_USER_ROOM;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRoomRepository userRoomRepository;

    @Autowired
    private EntityManager em;

    private User mockUser;
    private Book mockBook;
    private Room mockRoom;
    private UserRoom mockUserRoom;
    private Record mockRecord;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .email("test@test.com")
                .nickname("test")
                .password("test1234")
                .imageUrl("test.jpg")
                .build();
        userRepository.save(mockUser);

        mockBook = Book.builder()
                .bookTitle("테스트 책")
                .authorName("테스트 작가")
                .isbn("123456789")
                .genre(NOVEL_POETRY_DRAMA)
                .pageCount(300)
                .imageUrl("test.jpg")
                .build();
        bookRepository.save(mockBook);

        mockRoom = Room.builder()
                .roomName("테스트 방")
                .isPublic(true)
                .recruitCount(5)
                .roomType(TOGETHER)
                .roomPercentage(0.0)
                .book(mockBook)
                .build();
        roomRepository.save(mockRoom);

        mockUserRoom = UserRoom.builder()
                .room(mockRoom)
                .user(mockUser)
                .userPercentage(0.0)
                .userRole(HOST)
                .currentPage(0)
                .build();
        userRoomRepository.save(mockUserRoom);

        mockRecord = Record.builder()
                .room(mockRoom)
                .user(mockUser)
                .recordType(ENTIRE)
                .recordTitle("기록 테스트 제목")
                .content("기록 테스트 내용")
                .build();
        recordRepository.save(mockRecord);

        em.flush();
        em.clear();
    }

    @AfterEach
    void tearDown() {
        commentRepository.deleteAll();
        recordRepository.deleteAll();
        bookRepository.deleteAll();
        userRoomRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("댓글을 정상적으로 작성하는 경우")
    void createCommentSuccess() {
        // given
        PostCommentRequest request = new PostCommentRequest("댓글 내용");

        // when
        PostCommentResponse response = commentService.createComment(mockRecord.getRecordId(), mockUser.getUserId(), request);

        // then
        assertNotNull(response);
        assertThat(response.getCommentId()).isNotNull();
        assertThat(commentRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("방에 속해있지 않은 경우 댓글 작성 불가")
    void createCommentFailNotInRoom() {
        // given
        // 방에 속하지 않은 다른 유저 생성
        User otherUser = User.builder()
                .email("other@test.com")
                .nickname("otherUser")
                .password("password")
                .imageUrl("test.jpg")
                .build();
        userRepository.save(otherUser);

        PostCommentRequest request = new PostCommentRequest("방에 속하지 않은 유저가 댓글 작성 시도");

        // when & then
        GlobalException exception = assertThrows(GlobalException.class,
                () -> commentService.createComment(mockRecord.getRecordId(), otherUser.getUserId(), request));
        assertEquals(CANNOT_FOUND_USER_ROOM, exception.getExceptionStatus());
    }

    @Test
    @DisplayName("방이 EXPIRED 상태라서 댓글을 작성할 수 없는 경우")
    void createCommentFailRoomExpired() {
        // given
        mockUserRoom.setStatus(EXPIRED);
        userRoomRepository.save(mockUserRoom);
        PostCommentRequest request = new PostCommentRequest("댓글 내용");

        // when & then
        GlobalException exception = assertThrows(GlobalException.class,
                () -> commentService.createComment(mockRecord.getRecordId(), mockUser.getUserId(), request));
        assertEquals(CANNOT_COMMENT_IN_EXPIRED_ROOM, exception.getExceptionStatus());
    }
}