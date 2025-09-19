package com.example.dorm.service;

import com.example.dorm.model.Violation;
import com.example.dorm.repository.ViolationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ViolationService {

    private static final List<String> SEVERITY_LEVELS = List.of("LOW", "MEDIUM", "HIGH");

    private final ViolationRepository violationRepository;

    public ViolationService(ViolationRepository violationRepository) {
        this.violationRepository = violationRepository;
    }

    public Violation createViolation(Violation violation) {
        if (violation == null) {
            throw new IllegalArgumentException("Violation must not be null");
        }

        if (violation.getStudent() == null) {
            throw new IllegalArgumentException("Vi phạm phải gắn với sinh viên");
        }

        if (violation.getDescription() == null || violation.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Mô tả vi phạm không được để trống");
        }
        violation.setDescription(violation.getDescription().trim());

        if (violation.getSeverity() != null && !violation.getSeverity().isBlank()) {
            String normalizedSeverity = violation.getSeverity().trim().toUpperCase();
            if (!SEVERITY_LEVELS.contains(normalizedSeverity)) {
                throw new IllegalArgumentException("Mức độ vi phạm không hợp lệ");
            }
            violation.setSeverity(normalizedSeverity);
        } else {
            violation.setSeverity("LOW");
        }

        if (violation.getType() != null && !violation.getType().isBlank()) {
            violation.setType(violation.getType().trim().toUpperCase());
        } else {
            violation.setType("OTHER");
        }

        if (violation.getDate() == null) {
            violation.setDate(LocalDate.now());
        }

        return violationRepository.save(violation);
    }

    /**
     * @deprecated use {@link #createViolation(Violation)} instead.
     */
    @Deprecated
    public Violation recordViolation(Violation violation) {
        return createViolation(violation);
    }

    public List<Violation> getViolationsByStudent(Long studentId) {
        return violationRepository.findByStudent_IdOrderByDateDesc(studentId);
    }

    public List<Violation> getViolationsByRoom(Long roomId) {
        return violationRepository.findByRoom_IdOrderByDateDesc(roomId);
    }

    public List<Violation> getAllViolations() {
        return violationRepository.findAllByOrderByDateDesc();
    }

    public Map<String, Long> countByRoom() {
        return violationRepository.countByRoom().stream()
                .collect(LinkedHashMap::new,
                        (map, arr) -> map.put((String) arr[0], (Long) arr[1]),
                        Map::putAll);
    }

    public Map<String, Long> countByStudent() {
        return violationRepository.countByStudent().stream()
                .collect(LinkedHashMap::new,
                        (map, arr) -> map.put((String) arr[0], (Long) arr[1]),
                        Map::putAll);
    }

    public Map<String, Long> countBySeverity() {
        return getAllViolations().stream()
                .collect(Collectors.groupingBy(Violation::getSeverity, LinkedHashMap::new, Collectors.counting()));
    }

    public Map<String, Long> countByType() {
        return violationRepository.countByType().stream()
                .collect(LinkedHashMap::new,
                        (map, arr) -> map.put((String) arr[0], (Long) arr[1]),
                        Map::putAll);
    }

    public long countViolations() {
        return violationRepository.count();
    }

    public long countViolationsBySeverity(String severity) {
        if (severity == null || severity.isBlank()) {
            return countViolations();
        }
        return violationRepository.countBySeverity(severity.trim().toUpperCase());
    }
}
