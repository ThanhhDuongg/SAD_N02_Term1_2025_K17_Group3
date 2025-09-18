package com.example.dorm.dto;

import com.example.dorm.model.SupportRequestStatus;
import jakarta.validation.constraints.Size;

/**
 * Payload used by staff/admin to update an existing support request.
 */
public class SupportRequestUpdateRequest {

    private SupportRequestStatus status;

    @Size(max = 5000)
    private String responseMessage;

    private Long assignedStaffId;

    private Boolean violationFlag;

    @Size(max = 1000)
    private String violationNote;

    public SupportRequestStatus getStatus() {
        return status;
    }

    public void setStatus(SupportRequestStatus status) {
        this.status = status;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Long getAssignedStaffId() {
        return assignedStaffId;
    }

    public void setAssignedStaffId(Long assignedStaffId) {
        this.assignedStaffId = assignedStaffId;
    }

    public Boolean getViolationFlag() {
        return violationFlag;
    }

    public void setViolationFlag(Boolean violationFlag) {
        this.violationFlag = violationFlag;
    }

    public String getViolationNote() {
        return violationNote;
    }

    public void setViolationNote(String violationNote) {
        this.violationNote = violationNote;
    }
}
