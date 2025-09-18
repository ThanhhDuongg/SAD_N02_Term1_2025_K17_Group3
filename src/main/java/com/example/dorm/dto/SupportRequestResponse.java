package com.example.dorm.dto;

import com.example.dorm.model.SupportRequestStatus;

import java.time.LocalDateTime;

/**
 * Serializable view of a support request used by the REST API.
 */
public record SupportRequestResponse(
        Long id,
        String studentCode,
        String studentName,
        String studentUsername,
        String studentEmail,
        String title,
        String description,
        SupportRequestStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime resolvedAt,
        String responseMessage,
        boolean violationFlag,
        String violationNote,
        Long assignedStaffId,
        String assignedStaffUsername,
        String assignedStaffEmail,
        String lastUpdatedByUsername,
        String lastUpdatedByEmail
) {
}
