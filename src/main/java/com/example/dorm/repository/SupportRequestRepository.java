package com.example.dorm.repository;

import com.example.dorm.model.SupportRequest;
import com.example.dorm.model.SupportRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SupportRequestRepository extends JpaRepository<SupportRequest, Long> {

    Page<SupportRequest> findByStatus(SupportRequestStatus status, Pageable pageable);

    Page<SupportRequest> findByViolationFlagTrue(Pageable pageable);

    Page<SupportRequest> findByStatusAndViolationFlagTrue(SupportRequestStatus status, Pageable pageable);

    Page<SupportRequest> findByStudent_User_Username(String username, Pageable pageable);

    Page<SupportRequest> findByStudent_User_UsernameAndStatus(String username, SupportRequestStatus status, Pageable pageable);

    Optional<SupportRequest> findByStudent_IdAndTitleIgnoreCase(Long studentId, String title);
}
