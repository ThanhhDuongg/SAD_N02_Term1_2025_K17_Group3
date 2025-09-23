package com.example.dorm.repository;

import com.example.dorm.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByNumberContainingIgnoreCaseOrTypeContainingIgnoreCase(String number, String type);

    Page<Room> findByNumberContainingIgnoreCaseOrTypeContainingIgnoreCase(String number, String type, Pageable pageable);

    Optional<Room> findByNumber(String number);

    @Query("""
       select r
       from Room r
       left join fetch r.students
       where r.id = :id
       """)
    Optional<Room> findByIdWithStudents(@Param("id") Long id);
}
