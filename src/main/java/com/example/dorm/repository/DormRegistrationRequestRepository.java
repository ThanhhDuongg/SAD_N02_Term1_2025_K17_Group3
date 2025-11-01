package com.example.dorm.repository;

import com.example.dorm.model.DormRegistrationRequest;
import com.example.dorm.model.DormRegistrationStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DormRegistrationRequestRepository extends JpaRepository<DormRegistrationRequest, Long> {

    @EntityGraph(attributePaths = {"period", "student"})
    List<DormRegistrationRequest> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    @EntityGraph(attributePaths = {"period", "student"})
    List<DormRegistrationRequest> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"period", "student"})
    List<DormRegistrationRequest> findByStatusOrderByCreatedAtDesc(DormRegistrationStatus status);

    @EntityGraph(attributePaths = {"period", "student"})
    List<DormRegistrationRequest> findByPeriodIdOrderByCreatedAtDesc(Long periodId);

    @EntityGraph(attributePaths = {"period", "student"})
    List<DormRegistrationRequest> findByPeriodIdAndStatusOrderByCreatedAtDesc(Long periodId, DormRegistrationStatus status);

    boolean existsByStudentIdAndPeriodId(Long studentId, Long periodId);
    long countByPeriodId(Long periodId);

    @EntityGraph(attributePaths = {"period", "student", "student.room"})
    Optional<DormRegistrationRequest> findWithStudentAndRoomById(Long id);

    Optional<DormRegistrationRequest> findFirstByPeriod_IdAndDesiredRoomTypeIgnoreCaseAndStatusOrderByCreatedAtAsc(
            Long periodId,
            String desiredRoomType,
            DormRegistrationStatus status);
}
