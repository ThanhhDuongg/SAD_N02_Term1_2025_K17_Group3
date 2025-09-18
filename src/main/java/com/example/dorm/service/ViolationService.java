package com.example.dorm.service;

import com.example.dorm.model.Violation;
import com.example.dorm.repository.ViolationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ViolationService {

    @Autowired
    private ViolationRepository violationRepository;

    public Violation recordViolation(Violation violation) {
        if (violation.getSeverity() != null) {
            violation.setSeverity(violation.getSeverity().trim().toUpperCase());
        }
        return violationRepository.save(violation);
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
