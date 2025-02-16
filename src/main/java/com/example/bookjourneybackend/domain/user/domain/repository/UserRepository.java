package com.example.bookjourneybackend.domain.user.domain.repository;

import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.global.entity.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAndStatus(String email, EntityStatus status);
    Optional<User> findByUserIdAndStatus(Long userId, EntityStatus status);
    boolean existsByNicknameAndStatus(String nickName, EntityStatus status);
    boolean existsByEmailAndStatus(String nickName, EntityStatus status);
    boolean existsByImageUrlAndUserIdNot(String imageUrl, Long userId);

    @Query("SELECT COUNT(r) FROM Record r WHERE r.user.userId = :userId")
    int countRecordsByUserId(@Param("userId") Long userId);
}
