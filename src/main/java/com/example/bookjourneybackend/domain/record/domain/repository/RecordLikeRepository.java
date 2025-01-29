package com.example.bookjourneybackend.domain.record.domain.repository;

import com.example.bookjourneybackend.domain.record.domain.Record;
import com.example.bookjourneybackend.domain.record.domain.RecordLike;
import com.example.bookjourneybackend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordLikeRepository extends JpaRepository<RecordLike, Long> {
    boolean existsByRecordAndUser(Record record, User user);
}
