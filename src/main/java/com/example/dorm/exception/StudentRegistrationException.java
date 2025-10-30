package com.example.dorm.exception;

public class StudentRegistrationException extends RuntimeException {
    private final String field;

    public StudentRegistrationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
