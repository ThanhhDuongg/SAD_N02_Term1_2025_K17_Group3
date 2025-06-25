package com.example.dorm.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple unit tests for the {@link Student} model.
 */
class StudentTest {

    @Test
    void testGettersAndSetters() {
        Student s = new Student();
        s.setId(1L);
        s.setCode("SV01");
        s.setName("John");
        LocalDate dob = LocalDate.of(2000, 1, 1);
        s.setDob(dob);
        s.setGender("Male");
        s.setPhone("123");
        s.setAddress("Street 1");
        s.setEmail("a@b.com");
        s.setDepartment("CS");
        s.setStudyYear(3);
        Room room = new Room();
        room.setId(10L);
        s.setRoom(room);

        assertEquals(1L, s.getId());
        assertEquals("SV01", s.getCode());
        assertEquals("John", s.getName());
        assertEquals(dob, s.getDob());
        assertEquals("Male", s.getGender());
        assertEquals("123", s.getPhone());
        assertEquals("Street 1", s.getAddress());
        assertEquals("a@b.com", s.getEmail());
        assertEquals("CS", s.getDepartment());
        assertEquals(3, s.getStudyYear());
        assertSame(room, s.getRoom());
        assertNotNull(s.toString());
    }
}