package com.example.dorm.repository;

import com.example.dorm.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByNumberContainingIgnoreCaseOrTypeContainingIgnoreCase(String number, String type);

    Page<Room> findByNumberContainingIgnoreCaseOrTypeContainingIgnoreCase(String number, String type, Pageable pageable);

    Optional<Room> findByNumber(String number);

    Optional<Room> findByNumberIgnoreCase(String number);

    boolean existsByBuilding_Id(Long buildingId);

    int countByBuilding_Id(Long buildingId);

    @Query("""
            select coalesce(sum(r.capacity), 0)
            from Room r
            where (:buildingId is null or r.building.id = :buildingId)
            """)
    long sumCapacityByBuilding(@Param("buildingId") Long buildingId);

    @Query("""
            select r
            from Room r
            left join fetch r.students
            left join fetch r.building
            where r.id = :id
            """)
    Optional<Room> findByIdWithStudents(@Param("id") Long id);

    List<Room> findByBuilding_IdOrderByNumberAsc(Long buildingId);

    @Query("""
            select r
            from Room r
            where (:buildingId is null or r.building.id = :buildingId)
              and (
                    :search is null
                 or lower(r.number) like lower(concat('%', :search, '%'))
                 or lower(r.type) like lower(concat('%', :search, '%'))
                 or lower(r.building.name) like lower(concat('%', :search, '%'))
              )
            """)
    Page<Room> searchByKeywordAndBuilding(@Param("search") String search,
                                          @Param("buildingId") Long buildingId,
                                          Pageable pageable);
}
