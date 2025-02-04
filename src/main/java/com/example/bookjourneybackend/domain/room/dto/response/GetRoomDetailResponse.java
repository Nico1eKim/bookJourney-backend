package com.example.bookjourneybackend.domain.room.dto.response;

import com.example.bookjourneybackend.domain.book.domain.Book;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.global.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetRoomDetailResponse {
    private String roomName;
    private boolean isPublic;
    private String lastActivityTime;
    private double roomPercentage;
    private String progressStartDate;
    private String progressEndDate;
    private String recruitDday;
    private String recruitEndDate;
    private int recruitCount;
    private boolean isMember;
    private String genre;
    private String imageUrl;
    private String bookTitle;
    private String authorName;
    private boolean favorite;
    private String publisher;
    private String publishedDate;
    private String isbn;
    private String description;
    private List<RoomMemberInfo> memberList;

    public static GetRoomDetailResponse of(Room room, boolean isMember, boolean isFavorite,
                                           List<RoomMemberInfo> memberList, DateUtil dateUtil) {
        Book book = room.getBook();
        return new GetRoomDetailResponse(
                room.getRoomName(),
                room.isPublic(),
                dateUtil.calculateLastActivityTime(room.getRecords()),
                room.getRoomPercentage().intValue(),
                dateUtil.formatDate(room.getStartDate()),
                dateUtil.formatDate(room.getProgressEndDate()),
                dateUtil.calculateDday(room.getRecruitEndDate()), // D-day 계산
                dateUtil.formatDate(room.getRecruitEndDate()),
                room.getRecruitCount(),
                isMember,
                book.getGenre().getGenreType(),
                book.getImageUrl(),
                book.getBookTitle(),
                book.getAuthorName(),
                isFavorite,
                book.getPublisher(),
                dateUtil.formatDate(book.getPublishedDate()),
                book.getIsbn(),
                book.getDescription(),
                memberList // DELETED가 아닌 유저들만 포함
        );
    }
}