package com.example.bookjourneybackend.domain.room.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    public static GetRoomDetailResponse of(String roomName, boolean isPublic, String lastActivityTime, int roomPercentage,
                                           String progressStartDate, String progressEndDate, String recruitDday,
                                           String recruitEndDate, int recruitCount, boolean isMember, String genre, String imageUrl, String bookTitle, String authorName, boolean favorite,
                                           String publisher, String publishedDate, String isbn, String description, List<RoomMemberInfo> memberList) {
        return new GetRoomDetailResponse(roomName, isPublic, lastActivityTime, roomPercentage, progressStartDate,
                progressEndDate, recruitDday, recruitEndDate, recruitCount, isMember, genre, imageUrl, bookTitle, authorName, favorite, publisher, publishedDate, isbn, description, memberList);
    }
}
