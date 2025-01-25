package com.example.bookjourneybackend.domain.room.domain.repository;

import com.example.bookjourneybackend.domain.book.domain.GenreType;
import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.room.domain.SearchType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("SELECT r FROM Room r " +
            "WHERE (:searchTerm IS NULL OR " +
            "       (:searchType = '방 이름' AND r.roomName LIKE CONCAT('%', :searchTerm, '%')) OR " +
            "       (:searchType = '책 제목' AND r.book.bookTitle LIKE CONCAT('%', :searchTerm, '%')) OR " +
            "       (:searchType = '작가 이름' AND r.book.authorName LIKE CONCAT('%', :searchTerm, '%'))) " +
            "AND (:genre IS NULL OR r.book.genre = :genre) " +
            "AND (:recruitStartDate IS NULL OR r.startDate >= :recruitStartDate) " +
            "AND (:recruitEndDate IS NULL OR r.recruitEndDate <= :recruitEndDate) " +
            "AND (:roomStartDate IS NULL OR r.startDate >= :roomStartDate) " +
            "AND (:roomEndDate IS NULL OR r.progressEndDate <= :roomEndDate) " +
            "AND (:recordCount IS NULL OR SIZE(r.records) >= :recordCount) " +
            "ORDER BY r.recruitEndDate ASC, r.progressEndDate DESC, SIZE(r.records) DESC")
        // 정렬 우선순위 모집마감일이 가까운 순 > 방 기간이 많이 남은 순 > 기록 많은 순
    Slice<Room> findRoomsByFilters(@Param("searchTerm") String searchTerm,
                                   @Param("searchType") String searchType,
                                   @Param("genre") GenreType genre,
                                   @Param("recruitStartDate") LocalDate recruitStartDate,
                                   @Param("recruitEndDate") LocalDate recruitEndDate,
                                   @Param("roomStartDate") LocalDate roomStartDate,
                                   @Param("roomEndDate") LocalDate roomEndDate,
                                   @Param("recordCount") Integer recordCount,
                                   Pageable pageable);
}