package com.example.bookjourneybackend.domain.userRoom.domain.repository;

import com.example.bookjourneybackend.domain.room.domain.Room;
import com.example.bookjourneybackend.domain.user.domain.User;
import com.example.bookjourneybackend.domain.userRoom.domain.UserRoom;
import com.example.bookjourneybackend.global.entity.EntityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {
    @Query("SELECT ur FROM UserRoom ur " +
            "JOIN FETCH ur.room r " +
            "WHERE ur.user.userId = :userId AND ur.status = 'ACTIVE'" +
            "ORDER BY (SELECT MAX(rec.modifiedAt) FROM Record rec WHERE rec.room = r) DESC ")
    List<UserRoom> findUserRoomsOrderByModifiedAt(@Param("userId") Long userId);

    @Query("SELECT ur FROM UserRoom ur " +
            "JOIN FETCH ur.room r " +
            "WHERE ur.user.userId = :userId AND ur.status = 'ACTIVE'" +
            "ORDER BY ur.userPercentage DESC")
    List<UserRoom> findUserRoomsOrderByUserPercentage(@Param("userId") Long userId);

    Optional<UserRoom> findUserRoomByRoomAndUser(Room room, User user);

    Optional<UserRoom> findUserRoomByRoomAndUserAndStatus(Room room, User user, EntityStatus status);

    boolean existsByRoomAndUser(Room room, User user);

    List<UserRoom> findAllByRoom(Room room);

    @Query("SELECT ur FROM UserRoom ur " +
            "JOIN FETCH ur.room r " +
            "WHERE ur.user.userId = :userId AND ur.status = 'INACTIVE' " +
            "AND (MONTH(r.startDate) <= MONTH(:localDate) AND YEAR(r.startDate) <= YEAR(:localDate) " +
            "OR MONTH(r.progressEndDate) >= MONTH(:localDate) AND YEAR(r.progressEndDate) >= YEAR(:localDate)) " +
            "AND r.roomType = 'TOGETHER'")
    List<UserRoom> findInActiveTogetherRoomsByUserIdAndDate(@Param("userId") Long userId, @Param("localDate") LocalDate localDate);

    @Query("SELECT ur FROM UserRoom ur " +
            "JOIN FETCH ur.room r " +
            "WHERE ur.user.userId = :userId AND ur.status = 'INACTIVE' " +
            "AND MONTH(r.startDate) <= MONTH(:localDate) " +
            "AND r.roomType = 'ALONE'")
    List<UserRoom> findInActiveAloneRoomsByUserIdAndDate(Long userId, LocalDate localDate);
}
