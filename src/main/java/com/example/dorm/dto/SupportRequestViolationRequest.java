package com.example.dorm.dto;

import jakarta.validation.constraints.Size;

/**
 * Payload for flagging or clearing a violation associated with a support request.
 */
public class SupportRequestViolationRequest {

    private boolean violation = true;

    @Size(max = 1000)
    private String note;

    public boolean isViolation() {
        return violation;
    }

    public void setViolation(boolean violation) {
        this.violation = violation;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
