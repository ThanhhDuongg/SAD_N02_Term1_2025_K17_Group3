package com.example.dorm.repository;

import com.example.dorm.model.RoomTypePriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomTypePriceHistoryRepository extends JpaRepository<RoomTypePriceHistory, Long> {

    List<RoomTypePriceHistory> findByRoomType_IdOrderByChangedAtDesc(Long roomTypeId);

    List<RoomTypePriceHistory> findByRoomType_IdOrderByEffectiveFromAsc(Long roomTypeId);
}
