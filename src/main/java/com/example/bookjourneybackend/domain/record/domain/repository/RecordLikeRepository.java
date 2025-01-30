package com.example.bookjourneybackend.domain.record.domain.repository;

import com.example.bookjourneybackend.domain.record.domain.Record;
import com.example.bookjourneybackend.domain.record.domain.RecordLike;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.global.entity.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordLikeRepository extends JpaRepository<RecordLike, Long> {

    boolean existsByRecordAndUserAndRecordStatus(Record record, User user, EntityStatus status);
}
