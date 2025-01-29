package com.example.bookjourneybackend.domain.record.domain.repository;

import com.example.bookjourneybackend.domain.record.domain.RecordSortType;
import com.example.bookjourneybackend.domain.record.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {

    @Query("SELECT r FROM Record r WHERE r.room.roomId = :roomId AND r.status = 'ACTIVE' ORDER BY r.createdAt DESC")
    List<Record> findRecordsOrderByLatest(@Param("roomId") Long roomId, @Param("sortType") RecordSortType sortType);

    @Query("SELECT r FROM Record r WHERE r.room.roomId = :roomId AND r.status = 'ACTIVE' ORDER BY SIZE(r.comments) DESC")
    List<Record> findRecordsOrderByMostComments(@Param("roomId") Long roomId, @Param("sortType") RecordSortType sortType);
}
