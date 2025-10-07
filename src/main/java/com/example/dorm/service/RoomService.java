package com.example.dorm.service;

import com.example.dorm.model.Building;
import com.example.dorm.model.Room;
import com.example.dorm.repository.BuildingRepository;
import com.example.dorm.repository.RoomRepository;
import com.example.dorm.repository.StudentRepository;
import com.example.dorm.repository.ContractRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final StudentRepository studentRepository;
    private final BuildingRepository buildingRepository;
    private final ContractRepository contractRepository;

    public RoomService(RoomRepository roomRepository,
                       StudentRepository studentRepository,
                       BuildingRepository buildingRepository,
                       ContractRepository contractRepository) {
        this.roomRepository = roomRepository;
        this.studentRepository = studentRepository;
        this.buildingRepository = buildingRepository;
        this.contractRepository = contractRepository;
    }

    public Page<Room> getAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> getRoom(Long id) {
        return roomRepository.findById(id);
    }

    public Room getRequiredRoom(Long id) {
        return getRoom(id).orElseThrow(() -> new IllegalArgumentException("Room not found"));
    }

    public Room createRoom(Room room, Long buildingId) {
        String normalizedNumber = normalizeNumber(room.getNumber());
        ensureUniqueNumber(normalizedNumber, null);

        room.setNumber(normalizedNumber);
        room.setType(normalizeType(room.getType()));
        room.setCapacity(normalizeCapacity(room.getCapacity()));
        room.setPrice(normalizePrice(room));
        room.setBuilding(getRequiredBuilding(buildingId));
        return roomRepository.save(room);
    }

    @Transactional(readOnly = true)
    public Room getRoomWithStudents(Long id) {
        return roomRepository.findByIdWithStudents(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phòng: " + id));
    }

    public Room updateRoom(Long id, Room room, Long buildingId) {
        Room existing = getRequiredRoom(id);
        String normalizedNumber = normalizeNumber(room.getNumber());
        ensureUniqueNumber(normalizedNumber, id);

        existing.setNumber(normalizedNumber);
        existing.setType(normalizeType(room.getType()));
        existing.setCapacity(normalizeCapacity(room.getCapacity()));
        existing.setPrice(normalizePrice(room));
        existing.setBuilding(getRequiredBuilding(buildingId));
        return roomRepository.save(existing);
    }

    public void deleteRoom(Long id) {
        long occupantCount = studentRepository.countByRoom_Id(id);
        if (occupantCount > 0) {
            throw new IllegalStateException("Không thể xóa phòng khi vẫn còn sinh viên cư trú");
        }

        long contractCount = contractRepository.countByRoom_Id(id);
        if (contractCount > 0) {
            throw new IllegalStateException("Không thể xóa phòng khi vẫn còn hợp đồng liên kết");
        }

        roomRepository.deleteById(id);
    }

    public long getCurrentOccupancy(Long roomId) {
        return studentRepository.countByRoom_Id(roomId);
    }

    public Page<Room> searchRooms(String search, Long buildingId, Pageable pageable) {
        String normalizedSearch = (search == null || search.isBlank()) ? null : search.trim();
        return roomRepository.searchByKeywordAndBuilding(normalizedSearch, buildingId, pageable);
    }

    public List<Room> getRoomsByBuilding(Long buildingId) {
        return roomRepository.findByBuilding_IdOrderByNumberAsc(buildingId);
    }

    public long countRooms() {
        return roomRepository.count();
    }

    public long sumCapacity() {
        return roomRepository.sumCapacityByBuilding(null);
    }

    public long countOccupiedBeds() {
        return studentRepository.countByRoomIsNotNull();
    }

    private int normalizeCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Sức chứa phải lớn hơn 0");
        }
        return capacity;
    }

    private String normalizeType(String type) {
        return type == null ? null : type.trim();
    }

    private int normalizePrice(Room room) {
        int price = room.getPrice();
        if (price > 0) {
            return price;
        }
        String type = room.getType();
        if (type == null) {
            throw new IllegalArgumentException("Giá phòng phải lớn hơn 0");
        }
        return switch (type) {
            case "Phòng bốn" -> 2_000_000;
            case "Phòng tám" -> 1_200_000;
            default -> throw new IllegalArgumentException("Giá phòng phải lớn hơn 0");
        };
    }

    private String normalizeNumber(String number) {
        return number == null ? null : number.trim();
    }

    private void ensureUniqueNumber(String number, Long currentRoomId) {
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("Tên phòng không được để trống");
        }

        roomRepository.findByNumberIgnoreCase(number)
                .filter(existing -> currentRoomId == null || !existing.getId().equals(currentRoomId))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Phòng \"" + existing.getNumber() + "\" đã tồn tại, vui lòng nhập tên phòng khác.");
                });
    }

    private Building getRequiredBuilding(Long buildingId) {
        if (buildingId == null) {
            throw new IllegalArgumentException("Vui lòng chọn tòa nhà");
        }
        return buildingRepository.findById(buildingId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tòa nhà với ID: " + buildingId));
    }
}
