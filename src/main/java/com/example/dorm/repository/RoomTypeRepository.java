package com.example.dorm.repository;

import com.example.dorm.model.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {

    Optional<RoomType> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    @Query("""
            select rt
            from RoomType rt
            left join fetch rt.rooms
            where rt.id = :id
            """)
    Optional<RoomType> findByIdWithRooms(@Param("id") Long id);

    List<RoomType> findAllByOrderByNameAsc();
}
