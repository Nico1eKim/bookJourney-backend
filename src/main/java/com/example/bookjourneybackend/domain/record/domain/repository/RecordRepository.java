package com.example.bookjourneybackend.domain.record.domain.repository;

import com.example.bookjourneybackend.domain.record.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {
}
