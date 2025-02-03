package com.example.bookjourneybackend.domain.room.service;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.room.domain.repository.RoomRepository;
import com.example.bookjourneybackend.domain.room.dto.response.GetRoomArchiveResponse;
import com.example.bookjourneybackend.domain.room.dto.response.RecordInfo;
import com.example.bookjourneybackend.domain.userRoom.domain.UserRoom;
import com.example.bookjourneybackend.domain.userRoom.domain.repository.UserRoomRepository;
import com.example.bookjourneybackend.global.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomArchiveService {

    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;
    private final DateUtil dateUtil;

    /**
     * <독서기록장 다 안읽었어요>
     * 해당 사용자의 status가 'INACTIVE'인 UserRoom 중에서 queryParam으로 넘어온 날짜에 겹치는 날을 필터링하여 조회
     * 정렬은 inActivatedAt 내림차순으로 정렬
     */
    public GetRoomArchiveResponse viewInCompletedRooms(Long userId, String date) {
        log.info("------------------------[RoomArchiveService.viewCompletedRooms]------------------------");

        LocalDate localDate = dateUtil.parseDate(date);

        List<UserRoom> togetherArchiveList = userRoomRepository.findInActiveTogetherRoomsByUserIdAndDate(userId, localDate);
        List<UserRoom> aloneArchiveList = userRoomRepository.findInActiveAloneRoomsByUserIdAndDate(userId, localDate);

        return new GetRoomArchiveResponse(combineAndParseToRecordInfo(togetherArchiveList, aloneArchiveList));
    }

    //DB에서 찾은 같이읽기와 혼자읽기 방을 InActivatedAt 내림차순으로 정렬하여 RecordInfo의 리스트로 파싱
    private List<RecordInfo> combineAndParseToRecordInfo(List<UserRoom> togetherArchiveList, List<UserRoom> aloneArchiveList) {
        List<UserRoom> combinedList = new ArrayList<>();
        combinedList.addAll(togetherArchiveList);
        combinedList.addAll(aloneArchiveList);

        combinedList.sort(Comparator.comparing(UserRoom::getInActivatedAt).reversed());

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
