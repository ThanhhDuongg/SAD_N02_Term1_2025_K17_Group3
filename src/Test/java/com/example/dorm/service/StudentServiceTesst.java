package com.example.dorm.service;

import com.example.dorm.model.Student;
import com.example.dorm.repository.RoomRepository;
import com.example.dorm.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    @Mock
    private RoomRepository roomRepository;
    @InjectMocks
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void searchStudentsEmptyReturnsAll() {
        List<Student> all = Collections.singletonList(new Student());
        Page<Student> page = new PageImpl<>(all);
        when(studentRepository.findAll(any(Pageable.class))).thenReturn(page);
        Page<Student> result = studentService.searchStudents("", Pageable.unpaged());
        assertEquals(1, result.getTotalElements());
        verify(studentRepository).findAll(any(Pageable.class));
    }

    @Test
    void searchStudentsByName() {
        Student match1 = new Student();
        match1.setName("test");
        Student match2 = new Student();
        match2.setName("Test Nguyen");
        List<Student> list = Arrays.asList(match1, match2);
        Page<Student> page = new PageImpl<>(list);
        when(studentRepository.searchByCodeOrNameWord(eq("test"), any(Pageable.class)))
                .thenReturn(page);
        Page<Student> result = studentService.searchStudents("test", Pageable.unpaged());
        assertEquals(2, result.getContent().size());
        verify(studentRepository).searchByCodeOrNameWord(eq("test"), any(Pageable.class));
    }

    @Test
    void saveStudentThrowsWhenRoomCapacityExceeded() {
        com.example.dorm.model.Room room = new com.example.dorm.model.Room();
        room.setId(5L);
        room.setCapacity(1);
        com.example.dorm.model.Student existing = new com.example.dorm.model.Student();
        existing.setId(2L);
        existing.setRoom(room);

        when(roomRepository.findById(5L)).thenReturn(java.util.Optional.of(room));
        when(studentRepository.countByRoom_Id(5L)).thenReturn(1L);

        com.example.dorm.model.Student newStudent = new com.example.dorm.model.Student();
        newStudent.setRoom(room);

        assertThrows(IllegalStateException.class, () -> studentService.saveStudent(newStudent));
    }
}