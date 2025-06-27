package com.example.dorm.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentStatusTest {
    @Test
    void testPaymentStatusEnum() {
        PaymentStatus status = PaymentStatus.valueOf("SOME_STATUS");
        assertNotNull(status);
    }
    @Test
    void testToString() {
        PaymentStatus status = PaymentStatus.valueOf("SOME_STATUS");
        assertNotNull(status.toString());
    }
} 