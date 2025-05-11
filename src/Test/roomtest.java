package test.model;

import model.Room;
import model.Student;
import org.junit.Test;
import static org.junit.Assert.*;

public class RoomTest {

    @Test
    public void testAddStudentWithinLimit() {
        Room room = new Room("101", "A", "Standard", 2);
        Student s1 = new Student("SV001", "Nguyen Van A");
        Student s2 = new Student("SV002", "Le Thi B");

        assertTrue(room.addStudent(s1));
        assertTrue(room.addStudent(s2));
        assertEquals(2, room.getCurrentOccupancy());
        assertEquals(0, room.getAvailableSlots());
    }

    @Test
    public void testAddStudentOverLimit() {
        Room room = new Room("102", "B", "Standard", 1);
        Student s1 = new Student("SV003", "Tran Van C");
        Student s2 = new Student("SV004", "Pham Thi D");

        assertTrue(room.addStudent(s1));
        assertFalse(room.addStudent(s2));  // Đã vượt quá số lượng
        assertEquals(1, room.getCurrentOccupancy());
        assertEquals(0, room.getAvailableSlots());
    }
}
