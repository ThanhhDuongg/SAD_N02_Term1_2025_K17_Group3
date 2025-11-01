package com.example.dorm.service;

import com.example.dorm.model.Contract;
import com.example.dorm.model.RoomType;
import com.example.dorm.model.Student;
import com.example.dorm.model.StudentNotification;
import com.example.dorm.repository.ContractRepository;
import com.example.dorm.repository.StudentNotificationRepository;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class StudentNotificationService {

    private static final Set<String> INACTIVE_STATUSES = Set.of("CANCELLED", "TERMINATED", "ENDED", "EXPIRED");

    private final StudentNotificationRepository notificationRepository;
    private final ContractRepository contractRepository;

    public StudentNotificationService(StudentNotificationRepository notificationRepository,
                                      ContractRepository contractRepository) {
        this.notificationRepository = notificationRepository;
        this.contractRepository = contractRepository;
    }

    public List<StudentNotification> consumeUnread(Long studentId) {
        if (studentId == null) {
            return List.of();
        }
        List<StudentNotification> notifications = notificationRepository
                .findByStudent_IdAndReadFalseOrderByCreatedAtAsc(studentId);
        if (notifications.isEmpty()) {
            return List.of();
        }
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(notifications);
        return List.copyOf(notifications);
    }

    public void notifyRoomTypePriceChange(RoomType roomType,
                                          int oldPrice,
                                          int newPrice,
                                          LocalDate effectiveDate) {
        if (roomType == null || roomType.getId() == null) {
            return;
        }
        List<Contract> contracts = contractRepository.findByRoom_RoomType_Id(roomType.getId());
        if (contracts.isEmpty()) {
            return;
        }
        LocalDate referenceDate = effectiveDate != null ? effectiveDate : LocalDate.now();
        List<StudentNotification> notifications = new ArrayList<>();
        Set<Long> processedStudentIds = new HashSet<>();
        for (Contract contract : contracts) {
            if (!isEligible(contract, referenceDate)) {
                continue;
            }
            Student student = contract.getStudent();
            if (student == null) {
                continue;
            }
            Long studentId = student.getId();
            if (studentId != null && !processedStudentIds.add(studentId)) {
                continue;
            }
            StudentNotification notification = new StudentNotification();
            notification.setStudent(student);
            notification.setTitle("Điều chỉnh giá phòng");
            notification.setMessage(buildPriceChangeMessage(roomType, oldPrice, newPrice, effectiveDate));
            notification.setReferenceType("ROOM_TYPE_PRICE");
            notification.setReferenceId(roomType.getId());
            notification.setEffectiveDate(effectiveDate);
            notification.setRead(false);
            notifications.add(notification);
        }
        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);
        }
    }

    private boolean isEligible(Contract contract, LocalDate referenceDate) {
        if (contract == null || contract.getStudent() == null) {
            return false;
        }
        String status = contract.getStatus();
        if (status != null && INACTIVE_STATUSES.contains(status.trim().toUpperCase())) {
            return false;
        }
        LocalDate start = contract.getStartDate();
        LocalDate end = contract.getEndDate();
        LocalDate date = referenceDate != null ? referenceDate : LocalDate.now();
        if (start != null && start.isAfter(date)) {
            return false;
        }
        if (end != null && end.isBefore(date)) {
            return false;
        }
        return true;
    }

    private String buildPriceChangeMessage(RoomType roomType,
                                           int oldPrice,
                                           int newPrice,
                                           LocalDate effectiveDate) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String roomTypeName = roomType.getName() != null ? roomType.getName() : "Loại phòng";
        String oldPriceText = formatter.format(Math.max(oldPrice, 0));
        String newPriceText = formatter.format(Math.max(newPrice, 0));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String effectiveText = effectiveDate != null ? effectiveDate.format(dateFormatter) : "ngay khi có thông báo";
        return String.format("Giá phòng %s sẽ thay đổi từ %s₫ lên %s₫ kể từ ngày %s.",
                roomTypeName,
                oldPriceText,
                newPriceText,
                effectiveText);
    }
}
