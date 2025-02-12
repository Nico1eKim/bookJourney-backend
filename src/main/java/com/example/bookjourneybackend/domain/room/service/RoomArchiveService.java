package com.example.bookjourneybackend.domain.room.service;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.room.domain.repository.RoomRepository;
import com.example.bookjourneybackend.domain.room.dto.response.GetRoomArchiveResponse;
import com.example.bookjourneybackend.domain.room.dto.response.RecordInfo;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.user.domain.repository.UserRepository;
import com.example.bookjourneybackend.domain.userRoom.domain.UserRoom;
import com.example.bookjourneybackend.domain.userRoom.domain.repository.UserRoomRepository;
import com.example.bookjourneybackend.global.entity.EntityStatus;
import com.example.bookjourneybackend.global.exception.GlobalException;
import com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus;
import com.example.bookjourneybackend.global.util.DateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.bookjourneybackend.global.entity.EntityStatus.EXPIRED;
import static com.example.bookjourneybackend.global.entity.EntityStatus.INACTIVE;
import static com.example.bookjourneybackend.global.response.status.BaseExceptionResponseStatus.CANNOT_FOUND_USER;

@Service
@RequiredArgsConstructor
public class RoomArchiveService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;
    private final DateUtil dateUtil;

    /**
     * status=INACTIVE => <독서기록장 다 안읽었어요>
     * status=EXPIRED => <독서기록장 다 읽었어요>
     * 해당 사용자의 status에 따른 UserRoom 중에서 queryParam으로 넘어온 날짜에 겹치는 날을 필터링하여 조회
     * 정렬은 inActivatedAt 내림차순으로 정렬
     */
    public GetRoomArchiveResponse viewArchiveRooms(Long userId, Integer month, Integer year, EntityStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(CANNOT_FOUND_USER));

        if (year == null) {
            // year가 null이면 이번달과 겹치는 모든 방을 조회
            year = LocalDate.now().getYear();
            month = LocalDate.now().getMonthValue();
        }

        //month가 null일 경우에는 해당 년도의 모든 방을 조회
        List<UserRoom> togetherArchiveList = userRoomRepository.findInActiveTogetherRoomsByUserIdAndDate(userId, year, month, status);
        List<UserRoom> aloneArchiveList = userRoomRepository.findInActiveAloneRoomsByUserIdAndDate(userId, year, month, status);

        return GetRoomArchiveResponse.of(user.getNickname(), combineAndParseToRecordInfo(togetherArchiveList, aloneArchiveList, status));
    }

    //DB에서 찾은 같이읽기와 혼자읽기 방을 InActivatedAt 내림차순으로 정렬하여 RecordInfo의 리스트로 파싱
    private List<RecordInfo> combineAndParseToRecordInfo(List<UserRoom> togetherArchiveList, List<UserRoom> aloneArchiveList, EntityStatus status) {
        List<UserRoom> combinedList = new ArrayList<>();
        combinedList.addAll(togetherArchiveList);
        combinedList.addAll(aloneArchiveList);

        if (status == INACTIVE) {
            combinedList.sort(Comparator.comparing(UserRoom::getInActivatedAt).reversed());

        }
        if(status == EXPIRED) {
            //혼자읽기책과 같이읽기방을 구분하지 않고, 기간의 마지막일 날짜가 가장 오래 전인 책 및 방이 가장 상단에 위치하도록 정렬
            //기간의 마지막일이 같으면?
            //혼자읽기책과 같이읽기 방 중에서는 혼자읽기 책이 위에 배치되고 -> 제외
            //시작일을 기준으로, 시작일이 더 예전일 때 위에 배치
            //(혼자읽기 책의 경우, 시작일 = 유저가 혼자읽기 버튼을 누른 시점
            //같이읽기 방의 경우, 시작일 = 방기간의 시작일)
            combinedList.sort(Comparator.comparing(UserRoom::getRoom , Comparator.comparing(Room::getProgressEndDate, Comparator.nullsLast(Comparator.reverseOrder())))
                    .thenComparing(UserRoom::getRoom, Comparator.comparing(Room::getStartDate, Comparator.naturalOrder())));

            return combinedList.stream()
                    .map(userRoom -> {
                        Room room = userRoom.getRoom();
                        Book book = room.getBook();
                        return RecordInfo
                                .builder()
                                .roomId(room.getRoomId())
                                .imageUrl(book.getImageUrl())
                                .roomType(room.getRoomType().getRoomType())
                                .bookTitle(book.getBookTitle())
//                                .modifiedAt(dateUtil.calculateLastActivityTime(room.getRecords()))
                                .authorName(book.getAuthorName())
//                                .userPercentage(userRoom.getUserPercentage())
                                .roomDate(dateUtil.formatDateRange(room.getStartDate(), room.getProgressEndDate()))
                                .build();
                    })
                    .collect(Collectors.toList());
        }


        return combinedList.stream()
                .map(userRoom -> {
                    Room room = userRoom.getRoom();
                    Book book = room.getBook();
                    return RecordInfo
                            .builder()
                            .roomId(room.getRoomId())
                            .imageUrl(book.getImageUrl())
                            .roomType(room.getRoomType().getRoomType())
                            .bookTitle(book.getBookTitle())
                            .modifiedAt(dateUtil.calculateLastActivityTime(room.getRecords()))
                            .authorName(book.getAuthorName())
                            .userPercentage(userRoom.getUserPercentage())
                            .build();
                })
                .collect(Collectors.toList());
    }
}
