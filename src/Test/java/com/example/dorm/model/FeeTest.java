package com.example.dorm.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FeeTest {
    @Test
    void testFeeConstructorAndGettersSetters() {
        Fee fee = new Fee();
        fee.setId(1L);
        assertEquals(1L, fee.getId());
        // Thêm các test cho các thuộc tính khác nếu có
    }
    @Test
    void testToString() {
        Fee fee = new Fee();
        assertNotNull(fee.toString());
    }
} 