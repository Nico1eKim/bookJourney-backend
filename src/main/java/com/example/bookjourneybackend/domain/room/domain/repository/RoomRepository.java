package com.example.bookjourneybackend.domain.room.domain.repository;

import com.example.bookjourneybackend.domain.room.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
}
