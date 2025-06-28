package com.example.dorm.service;

import com.example.dorm.model.Student;
import com.example.dorm.model.Room;
import com.example.dorm.repository.StudentRepository;
import com.example.dorm.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceTest {
    @Mock StudentRepository studentRepository;
    @Mock RoomRepository roomRepository;
    @InjectMocks StudentService studentService;

    @Test
    void testGetAllStudents() {
        Page<Student> page = new PageImpl<>(List.of(new Student()));
        when(studentRepository.findAll(any(Pageable.class))).thenReturn(page);
        assertEquals(1, studentService.getAllStudents(Pageable.unpaged()).getTotalElements());
    }
    @Test
    void testGetStudentFound() {
        Student s = new Student(); s.setId(1L);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(s));
        assertTrue(studentService.getStudent(1L).isPresent());
    }
    @Test
    void testGetStudentNotFound() {
        when(studentRepository.findById(2L)).thenReturn(Optional.empty());
        assertFalse(studentService.getStudent(2L).isPresent());
    }
    @Test
    void testSaveStudentRoomFull() {
        Room room = new Room(); room.setId(1L); room.setCapacity(1);
        Student student = new Student(); student.setRoom(room);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(studentRepository.countByRoom_Id(1L)).thenReturn(1L);
        assertThrows(IllegalStateException.class, () -> studentService.saveStudent(student));
    }
    @Test
    void testSaveStudentRoomNotFound() {
        Room room = new Room(); room.setId(1L);
        Student student = new Student(); student.setRoom(room);
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> studentService.saveStudent(student));
    }

    @Test
    void testSaveStudentEmailExists() {
        Student student = new Student();
        student.setEmail("test@example.com");
        Student existing = new Student();
        existing.setId(2L);
        existing.setEmail("test@example.com");
        when(studentRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existing));
        assertThrows(IllegalStateException.class, () -> studentService.saveStudent(student));
    }
    @Test
    void testSaveStudentSuccess() {
        Room room = new Room(); room.setId(1L); room.setCapacity(2);
        Student student = new Student(); student.setRoom(room);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(studentRepository.countByRoom_Id(1L)).thenReturn(0L);
        when(studentRepository.save(any())).thenReturn(student);
        Student result = studentService.saveStudent(student);
        assertNotNull(result);
    }
    @Test
    void testDeleteStudent() {
        doNothing().when(studentRepository).deleteById(1L);
        studentService.deleteStudent(1L);
        verify(studentRepository, times(1)).deleteById(1L);
    }
    @Test
    void testSearchStudents() {
        Page<Student> page = new PageImpl<>(List.of(new Student()));
        when(studentRepository.findAll(any(Pageable.class))).thenReturn(page);
        assertEquals(1, studentService.searchStudents(null, Pageable.unpaged()).getTotalElements());
    }
} 