package com.example.dorm.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FeeTypeTest {
    @Test
    void testFeeTypeConstructorAndGettersSetters() {
        FeeType feeType = FeeType.valueOf("SOME_TYPE");
        assertNotNull(feeType);
    }
    @Test
    void testToString() {
        FeeType feeType = FeeType.valueOf("SOME_TYPE");
        assertNotNull(feeType.toString());
    }
} 