package com.example.bookjourneybackend.domain.room.domain.repository;

import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.global.entity.EntityStatus;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r " +
            "WHERE (:genre IS NULL OR r.book.genre = :genre) " +
            "AND (:recruitStartDate IS NULL OR r.startDate >= :recruitStartDate) " +
            "AND (:recruitEndDate IS NULL OR r.recruitEndDate <= :recruitEndDate) " +
            "AND (:roomStartDate IS NULL OR r.startDate >= :roomStartDate) " +
            "AND (:roomEndDate IS NULL OR r.progressEndDate <= :roomEndDate) " +
            "AND (:recordCount IS NULL OR SIZE(r.records) >= :recordCount) " +
            "AND (LOWER(r.roomName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(r.book.bookTitle) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(r.book.authorName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " + // 검색어 필터링
            "ORDER BY r.recruitEndDate ASC, r.progressEndDate DESC, SIZE(r.records) DESC")
    Slice<Room> findRoomsByFilters(
            @Param("genre") GenreType genre,
            @Param("recruitStartDate") LocalDate recruitStartDate,
            @Param("recruitEndDate") LocalDate recruitEndDate,
            @Param("roomStartDate") LocalDate roomStartDate,
            @Param("roomEndDate") LocalDate roomEndDate,
            @Param("recordCount") Integer recordCount,
            @Param("searchTerm") String searchTerm, // 검색어 추가
            Pageable pageable);

    /**
     * 정렬 기준 순서
     * 공개방
     * 댓글+답글 수가 많은 순
     * 모집마감일까지 많이 남은 순
     * 최근에 방 생성된 순
     */
    @Query("SELECT r FROM Room r " +
            "LEFT JOIN r.records rec " +
            "LEFT JOIN rec.comments com " +
            "WHERE r.recruitEndDate >= :lastDayOfWeek " +
            "AND r.roomType = 'TOGETHER' " +
            "AND r.isPublic = true " +
            "AND r.status != 'EXPIRED' " +
            "GROUP BY r " +
            "ORDER BY (COUNT(rec) + COUNT(com)) DESC, r.recruitEndDate DESC, r.startDate ASC")
    List<Room> findRecruitmentRooms(LocalDate firstDayOfWeek, LocalDate lastDayOfWeek, PageRequest of);

    List<Room> findByProgressEndDateBefore(LocalDate localDate);
}