package com.example.dorm.repository;

import com.example.dorm.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByNumberContainingIgnoreCaseOrTypeContainingIgnoreCase(String number, String type);
    Page<Room> findByNumberContainingIgnoreCaseOrTypeContainingIgnoreCase(String number, String type, Pageable pageable);
    Optional<Room> findByNumber(String number);
}