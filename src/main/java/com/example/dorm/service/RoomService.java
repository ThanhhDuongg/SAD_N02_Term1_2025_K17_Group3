package com.example.dorm.service;

import com.example.dorm.dto.RoomAutoCreateRequest;
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
    public BulkCreateResult autoCreateRooms(RoomAutoCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Yêu cầu tạo phòng không được để trống");
        }

        Long buildingId = request.buildingId();
        Long roomTypeId = request.roomTypeId();
        Integer capacity = request.capacity();
        Integer quantity = request.quantity();
        Integer startNumber = request.startNumber();

        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phòng phải lớn hơn 0");
        }
        if (startNumber == null) {
            throw new IllegalArgumentException("Vui lòng nhập số bắt đầu cho mã phòng");
        }

        Building building = getRequiredBuilding(buildingId);
        RoomType roomType = getRequiredRoomType(roomTypeId);

        int normalizedCapacity = normalizeCapacity(capacity != null ? capacity : 0);
        Integer normalizedFloor = normalizeFloor(request.floor());
        RoomOccupancyStatus occupancyStatus = normalizeStatus(request.occupancyStatus());

        String prefix = request.prefix() != null ? request.prefix().trim() : "";
        int padding = request.padding() != null && request.padding() >= 0 ? request.padding() : 0;

        List<Room> roomsToSave = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            int sequenceNumber = startNumber + i;
            if (sequenceNumber < 0) {
                errors.add("Không thể tạo phòng với số âm: " + sequenceNumber);
                continue;
            }

            String generatedNumber = buildRoomNumber(prefix, sequenceNumber, padding);
            String normalizedNumber = normalizeNumber(generatedNumber);

            try {
                ensureUniqueNumber(normalizedNumber, null);
                Room room = new Room();
                room.setNumber(normalizedNumber);
                room.setBuilding(building);
                room.setRoomType(roomType);
                room.setCapacity(normalizedCapacity);
                room.setFloor(normalizedFloor);
                room.setOccupancyStatus(occupancyStatus);
                roomsToSave.add(room);
            } catch (Exception ex) {
                errors.add("Không thể tạo phòng " + normalizedNumber + ": " + ex.getMessage());
            }
        }

        if (!roomsToSave.isEmpty()) {
            roomRepository.saveAll(roomsToSave);
        }

        return new BulkCreateResult(roomsToSave.size(), errors);
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

    private String buildRoomNumber(String prefix, int sequenceNumber, int padding) {
        String numberPart = padding > 0
                ? String.format("%0" + padding + "d", sequenceNumber)
                : Integer.toString(sequenceNumber);
        return prefix + numberPart;
    }

    public static record BulkCreateResult(int createdCount, List<String> errors) {
        public BulkCreateResult {
            errors = errors == null ? List.of() : List.copyOf(errors);
        }

        public boolean hasErrors() {
            return !errors.isEmpty();
        }
    }
}
