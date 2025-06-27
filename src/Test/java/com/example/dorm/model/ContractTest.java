package com.example.dorm.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ContractTest {
    @Test
    void testContractConstructorAndGettersSetters() {
        Contract contract = new Contract();
        contract.setId(1L);
        assertEquals(1L, contract.getId());
        // Thêm các test cho các thuộc tính khác nếu có
    }
    @Test
    void testToString() {
        Contract contract = new Contract();
        assertNotNull(contract.toString());
    }
} 