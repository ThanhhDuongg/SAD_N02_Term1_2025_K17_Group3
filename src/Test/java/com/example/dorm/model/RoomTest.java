package com.example.dorm.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RoomTest {
    @Test
    void testRoomConstructorAndGettersSetters() {
        Room room = new Room();
        room.setId(1L);
        assertEquals(1L, room.getId());
        // Thêm các test cho các thuộc tính khác nếu có
    }
    @Test
    void testToString() {
        Room room = new Room();
        assertNotNull(room.toString());
    }
} 