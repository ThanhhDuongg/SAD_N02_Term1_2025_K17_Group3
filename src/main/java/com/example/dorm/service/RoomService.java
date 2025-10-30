package com.example.dorm.service;

import com.example.dorm.dto.RoomImportRequest;
import com.example.dorm.model.Building;
import com.example.dorm.model.Room;
import com.example.dorm.model.RoomOccupancyStatus;
import com.example.dorm.model.RoomType;
import com.example.dorm.repository.BuildingRepository;
import com.example.dorm.repository.RoomRepository;
import com.example.dorm.repository.RoomTypeRepository;
import com.example.dorm.repository.StudentRepository;
import com.example.dorm.repository.ContractRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final StudentRepository studentRepository;
    private final BuildingRepository buildingRepository;
    private final ContractRepository contractRepository;
    private final RoomTypeRepository roomTypeRepository;

    public RoomService(RoomRepository roomRepository,
                       StudentRepository studentRepository,
                       BuildingRepository buildingRepository,
                       ContractRepository contractRepository,
                       RoomTypeRepository roomTypeRepository) {
        this.roomRepository = roomRepository;
        this.studentRepository = studentRepository;
        this.buildingRepository = buildingRepository;
        this.contractRepository = contractRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    public Page<Room> getAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> getRoom(Long id) {
        return roomRepository.findByIdWithTypeAndBuilding(id);
    }

    public Room getRequiredRoom(Long id) {
        return getRoom(id).orElseThrow(() -> new IllegalArgumentException("Room not found"));
    }

    public Room createRoom(Room room, Long buildingId, Long roomTypeId) {
        String normalizedNumber = normalizeNumber(room.getNumber());
        ensureUniqueNumber(normalizedNumber, null);

        int requestedPrice = room.getPrice();
        room.setNumber(normalizedNumber);
        room.setBuilding(getRequiredBuilding(buildingId));
        RoomType roomType = getRequiredRoomType(roomTypeId);
        room.setRoomType(roomType);
        room.setPrice(normalizePrice(requestedPrice, roomType));
        room.setCapacity(normalizeCapacity(room.getCapacity()));
        room.setFloor(normalizeFloor(room.getFloor()));
        room.setOccupancyStatus(normalizeStatus(room.getOccupancyStatus()));
        return roomRepository.save(room);
    }

    @Transactional(readOnly = true)
    public Room getRoomWithStudents(Long id) {
        return roomRepository.findByIdWithStudents(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy phòng: " + id));
    }

    public Room updateRoom(Long id, Room room, Long buildingId, Long roomTypeId) {
        Room existing = getRequiredRoom(id);
        String normalizedNumber = normalizeNumber(room.getNumber());
        ensureUniqueNumber(normalizedNumber, id);

        int requestedPrice = room.getPrice();
        int normalizedCapacity = normalizeCapacity(room.getCapacity());
        ensureCapacityFits(id, normalizedCapacity);
        existing.setNumber(normalizedNumber);
        existing.setBuilding(getRequiredBuilding(buildingId));
        RoomType roomType = getRequiredRoomType(roomTypeId);
        existing.setRoomType(roomType);
        existing.setPrice(normalizePrice(requestedPrice, roomType));
        existing.setCapacity(normalizedCapacity);
        existing.setFloor(normalizeFloor(room.getFloor()));
        existing.setOccupancyStatus(normalizeStatus(room.getOccupancyStatus()));
        return roomRepository.save(existing);
    }

    @Transactional
    public ImportResult importRooms(List<RoomImportRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Danh sách phòng import không được để trống");
        }

        List<Room> roomsToSave = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        Set<String> batchNumbers = new HashSet<>();
        Map<Long, Building> buildingCache = new HashMap<>();
        Map<Long, RoomType> roomTypeCache = new HashMap<>();

        for (int index = 0; index < requests.size(); index++) {
            RoomImportRequest request = requests.get(index);
            try {
                String normalizedNumber = normalizeNumber(request.number());
                if (normalizedNumber == null || normalizedNumber.isBlank()) {
                    throw new IllegalArgumentException("Mã phòng không được để trống");
                }
                String batchKey = normalizedNumber.toLowerCase();
                if (!batchNumbers.add(batchKey)) {
                    throw new IllegalArgumentException("Trùng mã phòng trong dữ liệu import: " + normalizedNumber);
                }
                ensureUniqueNumber(normalizedNumber, null);

                Long buildingId = request.buildingId();
                if (buildingId == null) {
                    throw new IllegalArgumentException("Vui lòng cung cấp tòa nhà cho phòng " + normalizedNumber);
                }
                Building building = buildingCache.computeIfAbsent(buildingId, this::getRequiredBuilding);

                Long roomTypeId = request.roomTypeId();
                if (roomTypeId == null) {
                    throw new IllegalArgumentException("Vui lòng chọn loại phòng cho phòng " + normalizedNumber);
                }
                RoomType roomType = roomTypeCache.computeIfAbsent(roomTypeId, this::getRequiredRoomType);

                int normalizedCapacity = normalizeCapacity(request.capacity() != null ? request.capacity() : 0);
                Room room = new Room();
                room.setNumber(normalizedNumber);
                room.setBuilding(building);
                room.setRoomType(roomType);
                room.setCapacity(normalizedCapacity);
                room.setFloor(normalizeFloor(request.floor()));
                room.setOccupancyStatus(normalizeStatus(request.occupancyStatus()));
                roomsToSave.add(room);
            } catch (Exception ex) {
                errors.add("Dòng " + (index + 1) + ": " + ex.getMessage());
            }
        }

        if (!roomsToSave.isEmpty()) {
            roomRepository.saveAll(roomsToSave);
        }

        return new ImportResult(roomsToSave.size(), errors);
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

    private String normalizeNumber(String number) {
        return number == null ? null : number.trim();
    }

    private int normalizeCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Sức chứa phòng phải lớn hơn 0");
        }
        return capacity;
    }

    private Integer normalizeFloor(Integer floor) {
        if (floor == null) {
            return null;
        }
        if (floor < 0) {
            throw new IllegalArgumentException("Tầng không được âm");
        }
        return floor;
    }

    private RoomOccupancyStatus normalizeStatus(RoomOccupancyStatus status) {
        return status == null ? RoomOccupancyStatus.AVAILABLE : status;
    }

    private int normalizePrice(int price, RoomType roomType) {
        if (price <= 0) {
            if (roomType == null) {
                throw new IllegalArgumentException("Giá phòng phải lớn hơn 0");
            }
            return roomType.getCurrentPrice();
        }
        return price;
    }

    private void ensureCapacityFits(Long roomId, int capacity) {
        long currentOccupancy = studentRepository.countByRoom_Id(roomId);
        if (currentOccupancy > capacity) {
            throw new IllegalArgumentException("Sức chứa mới nhỏ hơn số sinh viên hiện tại trong phòng");
        }
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

    private RoomType getRequiredRoomType(Long roomTypeId) {
        if (roomTypeId == null) {
            throw new IllegalArgumentException("Vui lòng chọn loại phòng");
        }
        return roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy loại phòng với ID: " + roomTypeId));
    }

    private Building getRequiredBuilding(Long buildingId) {
        if (buildingId == null) {
            throw new IllegalArgumentException("Vui lòng chọn tòa nhà");
        }
        return buildingRepository.findById(buildingId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tòa nhà với ID: " + buildingId));
    }

    public static record ImportResult(int createdCount, List<String> errors) {
        public ImportResult {
            errors = errors == null ? List.of() : List.copyOf(errors);
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }
    }
}
