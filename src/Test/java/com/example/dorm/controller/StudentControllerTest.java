package com.example.dorm.controller;

import com.example.dorm.model.Student;
import com.example.dorm.model.Room;
import com.example.dorm.model.Contract;
import com.example.dorm.service.StudentService;
import com.example.dorm.service.RoomService;
import com.example.dorm.service.ContractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {
    @Mock
    private StudentService studentService;
    @Mock
    private RoomService roomService;
    @Mock
    private ContractService contractService;
    @Mock
    private Model model;
    @Mock
    private RedirectAttributes redirectAttributes;
    @InjectMocks
    private StudentController studentController;

    private Student testStudent;
    private Room testRoom;
    private Contract testContract;
    private List<Student> studentList;
    private List<Room> roomList;

    @BeforeEach
    void setUp() {
        testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setCode("SV001");
        testStudent.setName("Nguyen Van A");
        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setNumber("101");
        testStudent.setRoom(testRoom);
        testContract = new Contract();
        testContract.setId(1L);
        testContract.setStudent(testStudent);
        testContract.setRoom(testRoom);
        testContract.setStartDate(LocalDate.now());
        testContract.setEndDate(LocalDate.now().plusMonths(6));
        studentList = Arrays.asList(testStudent);
        roomList = Arrays.asList(testRoom);
    }

    @Test
    void testListStudents_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Student> studentsPage = new PageImpl<>(studentList, pageable, 1);
        when(studentService.searchStudents(anyString(), any(Pageable.class))).thenReturn(studentsPage);
        String result = studentController.listStudents("test", 0, 10, model);
        assertEquals("students/list", result);
        verify(model).addAttribute("studentsPage", studentsPage);
        verify(model).addAttribute("search", "test");
    }

    @Test
    void testListStudents_Exception() {
        when(studentService.searchStudents(anyString(), any(Pageable.class))).thenThrow(new RuntimeException("Database error"));
        String result = studentController.listStudents("test", 0, 10, model);
        assertEquals("error", result);
        verify(model).addAttribute(eq("errorMessage"), contains("Lỗi khi tải danh sách sinh viên"));
    }

    @Test
    void testShowCreateForm_Success() {
        when(roomService.getAllRooms()).thenReturn(roomList);
        String result = studentController.showCreateForm(model);
        assertEquals("students/form", result);
        verify(model).addAttribute(eq("student"), any(Student.class));
        verify(model).addAttribute("rooms", roomList);
    }

    @Test
    void testShowCreateForm_Exception() {
        when(roomService.getAllRooms()).thenThrow(new RuntimeException("Service error"));
        String result = studentController.showCreateForm(model);
        assertEquals("error", result);
        verify(model).addAttribute(eq("errorMessage"), contains("Lỗi khi hiển thị form tạo sinh viên"));
    }

    @Test
    void testViewStudent_Success() {
        when(studentService.getStudent(1L)).thenReturn(Optional.of(testStudent));
        String result = studentController.viewStudent(1L, model);
        assertEquals("students/detail", result);
        verify(model).addAttribute("student", testStudent);
    }

    @Test
    void testViewStudent_NotFound() {
        when(studentService.getStudent(1L)).thenReturn(Optional.empty());
        String result = studentController.viewStudent(1L, model);
        assertEquals("error", result);
        verify(model).addAttribute(eq("errorMessage"), contains("Không tìm thấy sinh viên"));
    }

    @Test
    void testViewStudent_Exception() {
        when(studentService.getStudent(1L)).thenThrow(new RuntimeException("Database error"));
        String result = studentController.viewStudent(1L, model);
        assertEquals("error", result);
        verify(model).addAttribute(eq("errorMessage"), contains("Lỗi khi xem thông tin sinh viên"));
    }

    @Test
    void testCreateStudent_Success() {
        when(studentService.saveStudent(any(Student.class))).thenReturn(testStudent);
        when(contractService.createContract(any(Contract.class))).thenReturn(testContract);
        testStudent.setRoom(testRoom);
        String result = studentController.createStudent(
                testStudent,
                LocalDate.now(),
                LocalDate.now().plusMonths(6),
                "ACTIVE",
                redirectAttributes,
                model
        );
        assertEquals("redirect:/students", result);
        verify(studentService).saveStudent(any(Student.class));
        verify(contractService).createContract(any(Contract.class));
        verify(redirectAttributes).addFlashAttribute("message", contains("Thêm sinh viên thành công"));
    }

    @Test
    void testCreateStudent_WithoutContract() {
        when(studentService.saveStudent(any(Student.class))).thenReturn(testStudent);
        // Không có room hoặc ngày hợp đồng
        testStudent.setRoom(null);
        String result = studentController.createStudent(
                testStudent,
                null,
                null,
                null,
                redirectAttributes,
                model
        );
        assertEquals("redirect:/students", result);
        verify(studentService).saveStudent(any(Student.class));
        verify(contractService, never()).createContract(any(Contract.class));
        verify(redirectAttributes).addFlashAttribute("message", contains("Thêm sinh viên thành công"));
    }

    @Test
    void testCreateStudent_Exception() {
        when(studentService.saveStudent(any(Student.class))).thenThrow(new RuntimeException("DB error"));
        String result = studentController.createStudent(
                testStudent,
                LocalDate.now(),
                LocalDate.now().plusMonths(6),
                "ACTIVE",
                redirectAttributes,
                model
        );
        assertEquals("redirect:/students", result);
        verify(redirectAttributes).addFlashAttribute("message", contains("Thêm sinh viên thất bại"));
    }

    @Test
    void testShowUpdateForm_Success() {
        when(studentService.getStudent(1L)).thenReturn(Optional.of(testStudent));
        when(roomService.getAllRooms()).thenReturn(roomList);
        when(contractService.findLatestContractByStudentId(1L)).thenReturn(testContract);
        String result = studentController.showUpdateForm(1L, model);
        assertEquals("students/form", result);
        verify(model).addAttribute("student", testStudent);
        verify(model).addAttribute("rooms", roomList);
        verify(model).addAttribute("contractStartDate", testContract.getStartDate());
        verify(model).addAttribute("contractEndDate", testContract.getEndDate());
    }

    @Test
    void testShowUpdateForm_NotFound() {
        when(studentService.getStudent(1L)).thenReturn(Optional.empty());
        String result = studentController.showUpdateForm(1L, model);
        assertEquals("error", result);
        verify(model).addAttribute(eq("errorMessage"), contains("Không tìm thấy sinh viên"));
    }

    @Test
    void testShowUpdateForm_Exception() {
        when(studentService.getStudent(1L)).thenThrow(new RuntimeException("DB error"));
        String result = studentController.showUpdateForm(1L, model);
        assertEquals("error", result);
        verify(model).addAttribute(eq("errorMessage"), contains("Lỗi khi hiển thị form cập nhật"));
    }

    @Test
    void testUpdateStudent_Success() {
        when(studentService.getStudent(1L)).thenReturn(Optional.of(testStudent));
        when(studentService.saveStudent(any(Student.class))).thenReturn(testStudent);
        String result = studentController.updateStudent(1L, testStudent, redirectAttributes, model);
        assertEquals("redirect:/students", result);
        verify(studentService).saveStudent(any(Student.class));
        verify(redirectAttributes).addFlashAttribute("message", contains("Cập nhật sinh viên thành công"));
    }

    @Test
    void testUpdateStudent_NotFound() {
        when(studentService.getStudent(1L)).thenReturn(Optional.empty());
        String result = studentController.updateStudent(1L, testStudent, redirectAttributes, model);
        assertEquals("redirect:/students", result);
        verify(redirectAttributes).addFlashAttribute("message", contains("Không tìm thấy sinh viên"));
    }

    @Test
    void testUpdateStudent_Exception() {
        when(studentService.getStudent(1L)).thenThrow(new RuntimeException("DB error"));
        String result = studentController.updateStudent(1L, testStudent, redirectAttributes, model);
        assertEquals("redirect:/students", result);
        verify(redirectAttributes).addFlashAttribute("message", contains("Cập nhật sinh viên thất bại"));
    }

    @Test
    void testDeleteStudent_Success() {
        when(studentService.getStudent(1L)).thenReturn(Optional.of(testStudent));
        doNothing().when(studentService).deleteStudent(1L);
        String result = studentController.deleteStudent(1L, redirectAttributes, model);
        assertEquals("redirect:/students", result);
        verify(studentService).deleteStudent(1L);
        verify(redirectAttributes).addFlashAttribute("message", contains("Xoá sinh viên thành công"));
    }

    @Test
    void testDeleteStudent_NotFound() {
        when(studentService.getStudent(1L)).thenReturn(Optional.empty());
        String result = studentController.deleteStudent(1L, redirectAttributes, model);
        assertEquals("redirect:/students", result);
        verify(redirectAttributes).addFlashAttribute("message", contains("Không tìm thấy sinh viên"));
    }

    @Test
    void testDeleteStudent_Exception() {
        when(studentService.getStudent(1L)).thenThrow(new RuntimeException("DB error"));
        String result = studentController.deleteStudent(1L, redirectAttributes, model);
        assertEquals("redirect:/students", result);
        verify(redirectAttributes).addFlashAttribute("message", contains("Xoá sinh viên thất bại"));
    }

    @Test
    void testAutocomplete() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Student> studentsPage = new PageImpl<>(studentList, pageable, 1);
        when(studentService.searchStudents(eq("test"), any(Pageable.class))).thenReturn(studentsPage);
        List<Map<String, Object>> result = studentController.autocomplete("test");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testStudent.getId(), result.get(0).get("id"));
        assertTrue(result.get(0).get("label").toString().contains(testStudent.getCode()));
    }
}