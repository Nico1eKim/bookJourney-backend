package com.example.bookjourneybackend.domain.comment.domain.repository;

import com.example.bookjourneybackend.domain.comment.domain.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
}
