package com.example.dorm.service;

import com.example.dorm.model.Violation;
import com.example.dorm.repository.ViolationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViolationService {

    @Autowired
    private ViolationRepository violationRepository;

    public Violation recordViolation(Violation violation) {
        return violationRepository.save(violation);
    }

    public List<Violation> getViolationsByStudent(Long studentId) {
        return violationRepository.findByStudent_Id(studentId);
    }

    public List<Violation> getViolationsByRoom(Long roomId) {
        return violationRepository.findByRoom_Id(roomId);
    }

    public List<Violation> getAllViolations() {
        return violationRepository.findAll();
    }
}
