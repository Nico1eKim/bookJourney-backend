package com.example.bookjourneybackend.domain.record.domain.repository;

import com.example.bookjourneybackend.domain.record.domain.Record;
import com.example.bookjourneybackend.domain.record.domain.RecordLike;
import com.example.bookjourneybackend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordLikeRepository extends JpaRepository<RecordLike, Long> {

    @Query("SELECT COUNT(rl) > 0 FROM RecordLike rl " +
            "WHERE rl.record = :record AND rl.user = :user AND rl.record.status = 'ACTIVE'")
    boolean existsByRecordAndUser(@Param("record") Record record, @Param("user") User user);
}
