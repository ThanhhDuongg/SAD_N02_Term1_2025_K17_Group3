package com.example.dorm.repository;

import com.example.dorm.model.Fee;
import com.example.dorm.model.FeeType;
import com.example.dorm.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {
    java.util.List<Fee> findByType(FeeType type);

    java.util.List<Fee> findByContract_Student_Id(Long studentId);

    java.util.List<Fee> findByContract_Student_IdAndPaymentStatus(Long studentId, PaymentStatus paymentStatus);

    Page<Fee> findByContract_Student_CodeContainingIgnoreCaseOrContract_Student_NameContainingIgnoreCase(
            String code, String name, Pageable pageable);

    Page<Fee> findByTypeOrContract_Student_CodeContainingIgnoreCaseOrContract_Student_NameContainingIgnoreCase(
            FeeType type, String code, String name, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("""
            SELECT f FROM Fee f
            WHERE lower(f.contract.student.code) LIKE lower(concat('%', :search, '%'))
               OR lower(f.contract.student.name) = lower(:search)
               OR lower(f.contract.student.name) LIKE lower(concat(:search, ' %'))
               OR lower(f.contract.student.name) LIKE lower(concat('% ', :search))
            """)
    Page<Fee> searchByContractStudentWord(@org.springframework.data.repository.query.Param("search") String search, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("""
            SELECT f FROM Fee f
            WHERE f.type = :type
               OR lower(f.contract.student.code) LIKE lower(concat('%', :search, '%'))
               OR lower(f.contract.student.name) = lower(:search)
               OR lower(f.contract.student.name) LIKE lower(concat(:search, ' %'))
               OR lower(f.contract.student.name) LIKE lower(concat('% ', :search))
            """)
    Page<Fee> searchByTypeOrContractStudentWord(@org.springframework.data.repository.query.Param("type") FeeType type,
                                                @org.springframework.data.repository.query.Param("search") String search,
                                                Pageable pageable);

    Optional<Fee> findByContract_IdAndTypeAndDueDate(Long contractId, FeeType type, LocalDate dueDate);

    java.util.List<Fee> findByGroupCode(String groupCode);

    java.util.List<Fee> findByContract_IdAndType(Long contractId, FeeType type);

    java.util.List<Fee> findByContract_Id(Long contractId);
}