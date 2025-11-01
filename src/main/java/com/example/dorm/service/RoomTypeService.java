package com.example.dorm.service;

import com.example.dorm.model.Room;
import com.example.dorm.model.RoomType;
import com.example.dorm.model.RoomTypePriceHistory;
import com.example.dorm.repository.ContractRepository;
import com.example.dorm.repository.RoomRepository;
import com.example.dorm.repository.RoomTypePriceHistoryRepository;
import com.example.dorm.repository.RoomTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoomTypeService {

    private static final String ACTIVE_CONTRACT_STATUS = "ACTIVE";

    private final RoomTypeRepository roomTypeRepository;
    private final RoomTypePriceHistoryRepository roomTypePriceHistoryRepository;
    private final RoomRepository roomRepository;
    private final ContractRepository contractRepository;
    private final StudentNotificationService studentNotificationService;

    public RoomTypeService(RoomTypeRepository roomTypeRepository,
                           RoomTypePriceHistoryRepository roomTypePriceHistoryRepository,
                           RoomRepository roomRepository,
                           ContractRepository contractRepository,
                           StudentNotificationService studentNotificationService) {
        this.roomTypeRepository = roomTypeRepository;
        this.roomTypePriceHistoryRepository = roomTypePriceHistoryRepository;
        this.roomRepository = roomRepository;
        this.contractRepository = contractRepository;
        this.studentNotificationService = studentNotificationService;
    }

    @Transactional(readOnly = true)
    public List<RoomType> getAllRoomTypes() {
        return roomTypeRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public RoomType getRoomType(Long id) {
        return roomTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy loại phòng với ID: " + id));
    }

    @Transactional
    public RoomType createRoomType(RoomType roomType) {
        String name = normalizeName(roomType.getName());
        ensureUniqueName(name, null);

        roomType.setName(name);
        roomType.setCapacity(normalizeCapacity(roomType.getCapacity()));
        roomType.setCurrentPrice(normalizePrice(roomType.getCurrentPrice()));
        roomType.setDescription(normalizeDescription(roomType.getDescription()));
        RoomType saved = roomTypeRepository.save(roomType);
        recordInitialPriceHistory(saved);
        return saved;
    }

    @Transactional
    public RoomTypeUpdateResult updateRoomType(Long id,
                                               RoomType updates,
                                               String priceChangeNote,
                                               LocalDate priceEffectiveDate) {
        RoomType existing = getRoomType(id);
        int previousPrice = existing.getCurrentPrice();
        int previousCapacity = existing.getCapacity();
        String previousName = existing.getName();

        String name = normalizeName(updates.getName());
        ensureUniqueName(name, id);
        existing.setName(name);

        existing.setCapacity(normalizeCapacity(updates.getCapacity()));
        existing.setDescription(normalizeDescription(updates.getDescription()));

        int newPrice = normalizePrice(updates.getCurrentPrice());
        boolean priceChanged = previousPrice != newPrice;
        boolean requiresRoomSync = priceChanged || previousCapacity != existing.getCapacity() || !previousName.equals(existing.getName());
        long affectedActiveContracts = 0L;
        if (priceChanged) {
            LocalDate effectiveDate = normalizeEffectiveDate(priceEffectiveDate);
            affectedActiveContracts = contractRepository.countByRoom_RoomType_IdAndStatus(existing.getId(), ACTIVE_CONTRACT_STATUS);
            recordPriceHistory(existing, newPrice, priceChangeNote, effectiveDate);
            existing.setCurrentPrice(newPrice);
            if (affectedActiveContracts > 0) {
                studentNotificationService.notifyRoomTypePriceChange(existing, previousPrice, newPrice, effectiveDate);
            }
        } else {
            existing.setCurrentPrice(newPrice);
        }

        RoomType saved = roomTypeRepository.save(existing);

        if (requiresRoomSync) {
            List<Room> rooms = roomRepository.findByRoomType_Id(saved.getId());
            for (Room room : rooms) {
                room.syncFromRoomType();
            }
            roomRepository.saveAll(rooms);
        }

        return new RoomTypeUpdateResult(saved, priceChanged, affectedActiveContracts);
    }

    @Transactional
    public void deleteRoomType(Long id) {
        RoomType roomType = getRoomType(id);
        if (roomRepository.existsByRoomType_Id(id)) {
            throw new IllegalStateException("Không thể xóa loại phòng khi vẫn còn phòng đang sử dụng loại này");
        }
        roomTypeRepository.delete(roomType);
    }

    @Transactional(readOnly = true)
    public List<RoomTypePriceHistory> getPriceHistory(Long roomTypeId) {
        return roomTypePriceHistoryRepository.findByRoomType_IdOrderByChangedAtDesc(roomTypeId);
    }

    @Transactional(readOnly = true)
    public Map<Long, Long> getRoomCounts() {
        Map<Long, Long> counts = new HashMap<>();
        roomTypeRepository.findAll().forEach(roomType ->
                counts.put(roomType.getId(), roomRepository.countByRoomType_Id(roomType.getId())));
        return counts;
    }

    @Transactional(readOnly = true)
    public long countActiveContracts(Long roomTypeId) {
        return contractRepository.countByRoom_RoomType_IdAndStatus(roomTypeId, ACTIVE_CONTRACT_STATUS);
    }

    private void recordPriceHistory(RoomType roomType, int newPrice, String note, LocalDate effectiveDate) {
        RoomTypePriceHistory history = new RoomTypePriceHistory();
        history.setRoomType(roomType);
        history.setOldPrice(roomType.getCurrentPrice());
        history.setNewPrice(newPrice);
        history.setNote(normalizeDescription(note));
        LocalDateTime now = LocalDateTime.now();
        history.setChangedAt(now);
        history.setEffectiveFrom(effectiveDate != null ? effectiveDate : now.toLocalDate());
        roomTypePriceHistoryRepository.save(history);
    }

    private void recordInitialPriceHistory(RoomType roomType) {
        if (roomType == null || roomType.getId() == null) {
            return;
        }
        RoomTypePriceHistory history = new RoomTypePriceHistory();
        history.setRoomType(roomType);
        history.setOldPrice(roomType.getCurrentPrice());
        history.setNewPrice(roomType.getCurrentPrice());
        history.setNote("Khởi tạo loại phòng");
        LocalDateTime createdAt = roomType.getCreatedAt();
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        history.setChangedAt(createdAt);
        history.setEffectiveFrom(createdAt.toLocalDate());
        roomTypePriceHistoryRepository.save(history);
    }

    private String normalizeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên loại phòng không được để trống");
        }
        return name.trim();
    }

    private void ensureUniqueName(String name, Long currentId) {
        roomTypeRepository.findByNameIgnoreCase(name)
                .ifPresent(existing -> {
                    if (currentId == null || !existing.getId().equals(currentId)) {
                        throw new IllegalArgumentException("Loại phòng \"" + name + "\" đã tồn tại");
                    }
                });
    }

    private int normalizeCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Sức chứa phải lớn hơn 0");
        }
        return capacity;
    }

    private int normalizePrice(int price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Giá phòng phải lớn hơn 0");
        }
        return price;
    }

    private LocalDate normalizeEffectiveDate(LocalDate effectiveDate) {
        LocalDate today = LocalDate.now();
        LocalDate minimumDate = today.plusMonths(1);
        LocalDate target = effectiveDate != null ? effectiveDate : minimumDate;
        if (target.isBefore(minimumDate)) {
            throw new IllegalArgumentException("Ngày áp dụng giá mới phải cách hiện tại tối thiểu 1 tháng.");
        }
        return target;
    }

    private String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }
        String trimmed = description.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public record RoomTypeUpdateResult(RoomType roomType, boolean priceChanged, long affectedActiveContracts) {
    }
}
