package com.example.bookjourneybackend.domain.comment.domain.repository;

import com.example.bookjourneybackend.domain.comment.domain.Comment;
import com.example.bookjourneybackend.domain.comment.domain.CommentLike;
import com.example.bookjourneybackend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
        boolean existsByCommentAndUser(Comment comment, User user);

        void deleteByCommentAndUser(Comment comment, User user);
}
