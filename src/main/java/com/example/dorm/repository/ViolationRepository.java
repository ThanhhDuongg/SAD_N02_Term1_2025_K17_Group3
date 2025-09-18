package com.example.dorm.repository;

import com.example.dorm.model.Violation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ViolationRepository extends JpaRepository<Violation, Long> {
    List<Violation> findByStudent_IdOrderByDateDesc(Long studentId);

    List<Violation> findByRoom_IdOrderByDateDesc(Long roomId);

    List<Violation> findAllByOrderByDateDesc();

    @org.springframework.data.jpa.repository.Query("SELECT v.room.number, COUNT(v) FROM Violation v WHERE v.room IS NOT NULL GROUP BY v.room.number")
    List<Object[]> countByRoom();

    @org.springframework.data.jpa.repository.Query("SELECT v.student.code, COUNT(v) FROM Violation v WHERE v.student IS NOT NULL GROUP BY v.student.code")
    List<Object[]> countByStudent();

    Optional<Violation> findByStudent_IdAndDescription(Long studentId, String description);

    long countBySeverity(String severity);

    @org.springframework.data.jpa.repository.Query("SELECT v.type, COUNT(v) FROM Violation v WHERE v.type IS NOT NULL GROUP BY v.type ORDER BY COUNT(v) DESC")
    List<Object[]> countByType();
}
