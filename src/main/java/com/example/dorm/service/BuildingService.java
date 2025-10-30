package com.example.dorm.service;

import com.example.dorm.dto.BuildingDetail;
import com.example.dorm.dto.BuildingDetailRoom;
import com.example.dorm.dto.BuildingSummary;
import com.example.dorm.model.Building;
import com.example.dorm.repository.BuildingRepository;
import com.example.dorm.repository.RoomRepository;
import com.example.dorm.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final RoomRepository roomRepository;
    private final StudentRepository studentRepository;

    public BuildingService(BuildingRepository buildingRepository,
                           RoomRepository roomRepository,
                           StudentRepository studentRepository) {
        this.buildingRepository = buildingRepository;
        this.roomRepository = roomRepository;
        this.studentRepository = studentRepository;
    }

    public List<Building> getAllBuildings() {
        return buildingRepository.findAllByOrderByNameAsc();
    }

    public Building createBuilding(Building building) {
        String normalizedCode = normalizeCode(building.getCode());
        ensureUniqueCode(normalizedCode, null);

        building.setCode(normalizedCode);
        building.setName(normalizeName(building.getName()));
        building.setAddress(normalizeNullable(building.getAddress()));
        building.setDescription(normalizeNullable(building.getDescription()));
        building.setTotalFloors(normalizeFloor(building.getTotalFloors()));
        return buildingRepository.save(building);
    }

    public Building updateBuilding(Long id, Building building) {
        Building existing = getRequiredBuilding(id);

        String normalizedCode = normalizeCode(building.getCode());
        ensureUniqueCode(normalizedCode, id);

        existing.setCode(normalizedCode);
        existing.setName(normalizeName(building.getName()));
        existing.setAddress(normalizeNullable(building.getAddress()));
        existing.setDescription(normalizeNullable(building.getDescription()));
        existing.setTotalFloors(normalizeFloor(building.getTotalFloors()));
        return buildingRepository.save(existing);
    }

    public void deleteBuilding(Long id) {
        if (roomRepository.existsByBuilding_Id(id)) {
            throw new IllegalStateException("Không thể xóa tòa nhà khi vẫn còn phòng trực thuộc");
        }
        buildingRepository.deleteById(id);
    }

    public Building getRequiredBuilding(Long id) {
        return buildingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tòa nhà với ID: " + id));
    }

    public List<BuildingSummary> getBuildingSummaries() {
        return getAllBuildings().stream()
                .map(building -> {
                    int roomCount = roomRepository.countByBuilding_Id(building.getId());
                    long capacity = roomRepository.sumCapacityByBuilding(building.getId());
                    long occupied = studentRepository.countByRoom_Building_Id(building.getId());
                    return new BuildingSummary(building, roomCount, capacity, occupied);
                })
                .sorted(Comparator.comparing(summary -> summary.name().toLowerCase()))
                .toList();
    }

    public BuildingDetail getBuildingDetail(Long id) {
        Building building = getRequiredBuilding(id);

        Map<Long, Long> occupancyMap = roomRepository.countOccupancyByBuilding(id).stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).longValue(),
                        row -> ((Number) row[1]).longValue(),
                        (first, second) -> first
                ));

        List<BuildingDetailRoom> roomDetails = roomRepository.findByBuilding_IdOrderByNumberAsc(id).stream()
                .map(room -> new BuildingDetailRoom(
                        room.getId(),
                        room.getNumber(),
                        room.getFloor(),
                        room.getType(),
                        room.getCapacity(),
                        room.getPrice(),
                        room.getOccupancyStatus(),
                        occupancyMap.getOrDefault(room.getId(), 0L)
                ))
                .sorted(Comparator.comparing(BuildingDetailRoom::number, String.CASE_INSENSITIVE_ORDER))
                .toList();

        long capacity = roomDetails.stream().mapToLong(BuildingDetailRoom::capacity).sum();
        long occupied = roomDetails.stream().mapToLong(BuildingDetailRoom::occupiedBeds).sum();

        return new BuildingDetail(building, roomDetails.size(), capacity, occupied, roomDetails);
    }

    private void ensureUniqueCode(String code, Long currentId) {
        buildingRepository.findByCodeIgnoreCase(code)
                .filter(existing -> currentId == null || !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Mã tòa nhà đã tồn tại");
                });
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Mã tòa nhà không được để trống");
        }
        return code.trim().toUpperCase();
    }

    private String normalizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tên tòa nhà không được để trống");
        }
        return name.trim();
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Integer normalizeFloor(Integer totalFloors) {
        if (totalFloors == null) {
            return null;
        }
        if (totalFloors < 0) {
            throw new IllegalArgumentException("Số tầng phải lớn hơn hoặc bằng 0");
        }
        return totalFloors;
    }
}
