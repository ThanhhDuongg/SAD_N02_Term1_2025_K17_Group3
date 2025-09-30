package com.example.dorm.repository;

import com.example.dorm.model.DormRegistrationPeriod;
import com.example.dorm.model.DormRegistrationPeriodStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DormRegistrationPeriodRepository extends JpaRepository<DormRegistrationPeriod, Long> {
    Optional<DormRegistrationPeriod> findFirstByStatusOrderByStartTimeDesc(DormRegistrationPeriodStatus status);
    boolean existsByStatus(DormRegistrationPeriodStatus status);
    List<DormRegistrationPeriod> findAllByOrderByStartTimeDesc();
}
