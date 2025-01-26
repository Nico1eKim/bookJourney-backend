package com.example.bookjourneybackend.domain.record.domain.repository;

import com.example.bookjourneybackend.domain.record.domain.RecordLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecordLikeRepository extends JpaRepository<RecordLike, Long> {
}
