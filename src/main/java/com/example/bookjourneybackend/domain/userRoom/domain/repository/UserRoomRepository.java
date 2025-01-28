package com.example.bookjourneybackend.domain.userRoom.domain.repository;

import com.example.bookjourneybackend.domain.userRoom.domain.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {
    //JOIN FETCH를 쓴 이유? -> JPA의 N+1 문제를 해결하기 위함..
    @Query("SELECT ur FROM UserRoom ur " +
            "JOIN FETCH ur.room r " +
            "WHERE ur.user.userId = :userId AND r.status = 'ACTIVE' " +
            "ORDER BY ur.userPercentage DESC")
    List<UserRoom> findUserRoomsByUserIdAndActiveRoomsOrderByUserPercentage(@Param("userId") Long userId);

    @Query("SELECT ur FROM UserRoom ur " +
            "JOIN FETCH ur.room r " +
            "WHERE ur.user.userId = :userId AND r.status = 'ACTIVE' " +
            "ORDER BY (SELECT MAX(rec.modifiedAt) FROM Record rec WHERE rec.room = r) DESC ")
    List<UserRoom> findUserRoomsByUserIdAndActiveRoomsOrderByRecordModifiedAt(@Param("userId") Long userId);
}
