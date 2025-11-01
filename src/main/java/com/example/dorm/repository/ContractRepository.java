package com.example.dorm.repository;

import com.example.dorm.model.Contract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    Page<Contract> findByStudent_CodeContainingIgnoreCaseOrStudent_NameContainingIgnoreCaseOrRoom_NumberContainingIgnoreCaseOrStatusContainingIgnoreCase(
            String code, String studentName, String roomNumber, String status, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("""
            SELECT c FROM Contract c
            WHERE lower(c.student.code) LIKE lower(concat('%', :search, '%'))
               OR lower(c.room.number) LIKE lower(concat('%', :search, '%'))
               OR lower(c.status) LIKE lower(concat('%', :search, '%'))
               OR lower(c.student.name) = lower(:search)
               OR lower(c.student.name) LIKE lower(concat(:search, ' %'))
               OR lower(c.student.name) LIKE lower(concat('% ', :search))
            """)
    Page<Contract> searchByStudentWordOrCodeOrRoomOrStatus(@org.springframework.data.repository.query.Param("search") String search, Pageable pageable);

    @org.springframework.data.jpa.repository.Query("""
            SELECT c FROM Contract c
            WHERE str(c.id) LIKE concat('%', :search, '%')
               OR lower(c.student.code) LIKE lower(concat('%', :search, '%'))
               OR lower(c.student.name) = lower(:search)
               OR lower(c.student.name) LIKE lower(concat(:search, ' %'))
               OR lower(c.student.name) LIKE lower(concat('% ', :search))
            """)
    Page<Contract> searchByIdOrStudentWord(@org.springframework.data.repository.query.Param("search") String search, Pageable pageable);

    java.util.List<Contract> findByRoom_Id(Long roomId);

    java.util.List<Contract> findByRoom_RoomType_Id(Long roomTypeId);

    java.util.List<Contract> findByStudent_Id(Long studentId);

    long countByRoom_Id(Long roomId);

    long countByRoom_IdAndStatus(Long roomId, String status);

    long countByRoom_RoomType_IdAndStatus(Long roomTypeId, String status);

    boolean existsByStudent_Id(Long studentId);

    Contract findTopByStudent_IdOrderByEndDateDesc(Long studentId);

    java.util.Optional<Contract> findByStudent_IdAndRoom_IdAndStartDate(Long studentId, Long roomId, java.time.LocalDate startDate);

    @org.springframework.data.jpa.repository.Query("""
            SELECT c FROM Contract c
            WHERE c.student.id = :studentId
              AND (:excludeId IS NULL OR c.id <> :excludeId)
              AND c.startDate <= :endDate
              AND c.endDate >= :startDate
            """)
    java.util.List<Contract> findOverlappingByStudent(@org.springframework.data.repository.query.Param("studentId") Long studentId,
                                                      @org.springframework.data.repository.query.Param("startDate") java.time.LocalDate startDate,
                                                      @org.springframework.data.repository.query.Param("endDate") java.time.LocalDate endDate,
                                                      @org.springframework.data.repository.query.Param("excludeId") Long excludeId);

    @org.springframework.data.jpa.repository.Query("""
            SELECT c FROM Contract c
            WHERE c.room.id = :roomId
              AND (:excludeId IS NULL OR c.id <> :excludeId)
              AND c.startDate <= :endDate
              AND c.endDate >= :startDate
            """)
    java.util.List<Contract> findOverlappingByRoom(@org.springframework.data.repository.query.Param("roomId") Long roomId,
                                                   @org.springframework.data.repository.query.Param("startDate") java.time.LocalDate startDate,
                                                   @org.springframework.data.repository.query.Param("endDate") java.time.LocalDate endDate,
                                                   @org.springframework.data.repository.query.Param("excludeId") Long excludeId);
}