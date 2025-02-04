package com.example.bookjourneybackend.domain.comment.domain.repository;

import com.example.bookjourneybackend.domain.comment.domain.Comment;
import com.example.bookjourneybackend.domain.record.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
        List<Comment> findByRecord(Record record);
//    List<Comment> findByRecord_RecordId(Long recordId);
}
