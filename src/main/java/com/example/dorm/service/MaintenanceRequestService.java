package com.example.dorm.service;

import com.example.dorm.model.MaintenanceRequest;
import com.example.dorm.repository.MaintenanceRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaintenanceRequestService {

    @Autowired
    private MaintenanceRequestRepository maintenanceRequestRepository;

    public MaintenanceRequest createRequest(MaintenanceRequest request) {
        return maintenanceRequestRepository.save(request);
    }

    public List<MaintenanceRequest> getRequestsByStudent(Long studentId) {
        return maintenanceRequestRepository.findByStudent_Id(studentId);
    }
}
