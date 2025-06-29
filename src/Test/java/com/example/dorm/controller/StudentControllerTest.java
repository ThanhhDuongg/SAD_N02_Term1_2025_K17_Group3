package com.example.dorm.controller;

import com.example.dorm.model.Student;
import com.example.dorm.model.Room;
import com.example.dorm.model.Contract;
import com.example.dorm.service.StudentService;
import com.example.dorm.service.RoomService;
import com.example.dorm.service.ContractService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class StudentControllerTest {

    @Mock
    private StudentService studentService;

    @Mock
    private RoomService roomService;

    @Mock
    private ContractService contractService;

    @InjectMocks
    private StudentController studentController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Student testStudent;
    private Room testRoom;
    private Contract testContract;

    @BeforeEach
    void setUp() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/view/");
        viewResolver.setSuffix(".jsp");

        mockMvc = MockMvcBuilders.standaloneSetup(studentController)
                .setViewResolvers(viewResolver)
                .build();

        objectMapper = new ObjectMapper();

        // Setup test data
        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setNumber("101");

        testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setCode("SV001");
        testStudent.setName("Nguyễn Văn A");
        testStudent.setEmail("a@example.com");
        testStudent.setPhone("0123456789");
        testStudent.setRoom(testRoom);

        testContract = new Contract();
        testContract.setId(1L);
        testContract.setStudent(testStudent);
        testContract.setRoom(testRoom);
        testContract.setStartDate(LocalDate.now());
        testContract.setEndDate(LocalDate.now().plusMonths(6));
        testContract.setStatus("ACTIVE");
    }

    @Test
    void testListStudents_Success() throws Exception {
        // Given
        List<Student> students = Arrays.asList(testStudent);
        Page<Student> studentsPage = new PageImpl<>(students, PageRequest.of(0, 10), 1);

        when(studentService.searchStudents(anyString(), any(Pageable.class)))
                .thenReturn(studentsPage);

        // When & Then
        mockMvc.perform(get("/students")
                        .param("search", "test")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/list"))
                .andExpect(model().attributeExists("studentsPage"))
                .andExpect(model().attributeExists("pageNumbers"))
                .andExpect(model().attribute("search", "test"));

        verify(studentService).searchStudents("test", PageRequest.of(0, 10));
    }

    @Test
    void testListStudents_Exception() throws Exception {
        // Given
        when(studentService.searchStudents(anyString(), any(Pageable.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(studentService).searchStudents(isNull(), any(Pageable.class));
    }

    @Test
    void testShowCreateForm_Success() throws Exception {
        // Given
        List<Room> rooms = Arrays.asList(testRoom);
        when(roomService.getAllRooms()).thenReturn(rooms);

        // When & Then
        mockMvc.perform(get("/students/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/form"))
                .andExpect(model().attributeExists("student"))
                .andExpect(model().attributeExists("rooms"));

        verify(roomService).getAllRooms();
    }

    @Test
    void testShowCreateForm_Exception() throws Exception {
        // Given
        when(roomService.getAllRooms()).thenThrow(new RuntimeException("Service error"));

        // When & Then
        mockMvc.perform(get("/students/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(roomService).getAllRooms();
    }

    @Test
    void testCreateStudent_Success_WithContract() throws Exception {
        // Given
        when(studentService.saveStudent(any(Student.class))).thenReturn(testStudent);
        when(contractService.createContract(any(Contract.class))).thenReturn(testContract);

        // When & Then
        mockMvc.perform(post("/students")
                        .param("code", "SV001")
                        .param("name", "Nguyễn Văn A")
                        .param("email", "a@example.com")
                        .param("phone", "0123456789")
                        .param("room.id", "1")
                        .param("contractStartDate", "2024-01-01")
                        .param("contractEndDate", "2024-06-01")
                        .param("contractStatus", "ACTIVE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"))
                .andExpect(flash().attribute("message", "Thêm sinh viên thành công!"))
                .andExpect(flash().attribute("alertClass", "alert-success"));

        verify(studentService).saveStudent(any(Student.class));
        verify(contractService).createContract(any(Contract.class));
    }

    @Test
    void testCreateStudent_Success_WithoutContract() throws Exception {
        // Given
        when(studentService.saveStudent(any(Student.class))).thenReturn(testStudent);

        // When & Then
        mockMvc.perform(post("/students")
                        .param("code", "SV001")
                        .param("name", "Nguyễn Văn A")
                        .param("email", "a@example.com")
                        .param("phone", "0123456789"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"))
                .andExpect(flash().attribute("message", "Thêm sinh viên thành công!"))
                .andExpect(flash().attribute("alertClass", "alert-success"));

        verify(studentService).saveStudent(any(Student.class));
        verify(contractService, never()).createContract(any(Contract.class));
    }

    @Test
    void testCreateStudent_Exception() throws Exception {
        // Given
        when(studentService.saveStudent(any(Student.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(post("/students")
                        .param("code", "SV001")
                        .param("name", "Nguyễn Văn A"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"))
                .andExpect(flash().attribute("message", org.hamcrest.Matchers.startsWith("Thêm sinh viên thất bại:")))
                .andExpect(flash().attribute("alertClass", "alert-danger"));

        verify(studentService).saveStudent(any(Student.class));
    }

    @Test
    void testViewStudent_Success() throws Exception {
        // Given
        when(studentService.getStudent(1L)).thenReturn(Optional.of(testStudent));

        // When & Then
        mockMvc.perform(get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/detail"))
                .andExpect(model().attribute("student", testStudent));

        verify(studentService).getStudent(1L);
    }

    @Test
    void testViewStudent_NotFound() throws Exception {
        // Given
        when(studentService.getStudent(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(studentService).getStudent(1L);
    }

    @Test
    void testViewStudent_Exception() throws Exception {
        // Given
        when(studentService.getStudent(1L)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/students/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(studentService).getStudent(1L);
    }

    @Test
    void testShowUpdateForm_Success() throws Exception {
        // Given
        List<Room> rooms = Arrays.asList(testRoom);
        when(studentService.getStudent(1L)).thenReturn(Optional.of(testStudent));
        when(roomService.getAllRooms()).thenReturn(rooms);
        when(contractService.findLatestContractByStudentId(1L)).thenReturn(testContract);

        // When & Then
        mockMvc.perform(get("/students/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/form"))
                .andExpect(model().attribute("student", testStudent))
                .andExpect(model().attributeExists("rooms"))
                .andExpect(model().attributeExists("contractStartDate"))
                .andExpect(model().attributeExists("contractEndDate"));

        verify(studentService).getStudent(1L);
        verify(roomService).getAllRooms();
        verify(contractService).findLatestContractByStudentId(1L);
    }

    @Test
    void testShowUpdateForm_StudentNotFound() throws Exception {
        // Given
        when(studentService.getStudent(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/students/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"));

        verify(studentService).getStudent(1L);
        verify(roomService, never()).getAllRooms();
    }

    @Test
    void testUpdateStudent_Success() throws Exception {
        // Given
        when(studentService.getStudent(1L)).thenReturn(Optional.of(testStudent));
        when(studentService.saveStudent(any(Student.class))).thenReturn(testStudent);

        // When & Then
        mockMvc.perform(post("/students/1")
                        .param("code", "SV001")
                        .param("name", "Nguyễn Văn A Updated"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"))
                .andExpect(flash().attribute("message", "Cập nhật sinh viên thành công!"))
                .andExpect(flash().attribute("alertClass", "alert-success"));

        verify(studentService).getStudent(1L);
        verify(studentService).saveStudent(any(Student.class));
    }

    @Test
    void testUpdateStudent_NotFound() throws Exception {
        // Given
        when(studentService.getStudent(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/students/1")
                        .param("code", "SV001")
                        .param("name", "Nguyễn Văn A"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"))
                .andExpect(flash().attribute("message", "Không tìm thấy sinh viên với ID: 1"))
                .andExpect(flash().attribute("alertClass", "alert-danger"));

        verify(studentService).getStudent(1L);
        verify(studentService, never()).saveStudent(any(Student.class));
    }

    @Test
    void testDeleteStudent_Success() throws Exception {
        // Given
        when(studentService.getStudent(1L)).thenReturn(Optional.of(testStudent));
        doNothing().when(studentService).deleteStudent(1L);

        // When & Then
        mockMvc.perform(get("/students/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"))
                .andExpect(flash().attribute("message", "Xoá sinh viên thành công!"))
                .andExpect(flash().attribute("alertClass", "alert-success"));

        verify(studentService).getStudent(1L);
        verify(studentService).deleteStudent(1L);
    }

    @Test
    void testDeleteStudent_NotFound() throws Exception {
        // Given
        when(studentService.getStudent(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/students/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"))
                .andExpect(flash().attribute("message", "Không tìm thấy sinh viên với ID: 1"))
                .andExpect(flash().attribute("alertClass", "alert-danger"));

        verify(studentService).getStudent(1L);
        verify(studentService, never()).deleteStudent(1L);
    }

    @Test
    void testAutocomplete_Success() throws Exception {
        // Given
        List<Student> students = Arrays.asList(testStudent);
        Page<Student> studentsPage = new PageImpl<>(students, PageRequest.of(0, 10), 1);

        when(studentService.searchStudents(eq("test"), any(Pageable.class)))
                .thenReturn(studentsPage);

        // When & Then
        mockMvc.perform(get("/students/search")
                        .param("term", "test")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].label").value("SV001 - Nguyễn Văn A"));

        verify(studentService).searchStudents("test", PageRequest.of(0, 10));
    }

    @Test
    void testListStudents_WithoutSearch() throws Exception {
        // Given
        List<Student> students = Arrays.asList(testStudent);
        Page<Student> studentsPage = new PageImpl<>(students, PageRequest.of(0, 10), 1);

        when(studentService.searchStudents(isNull(), any(Pageable.class)))
                .thenReturn(studentsPage);

        // When & Then
        mockMvc.perform(get("/students"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/list"))
                .andExpect(model().attributeExists("studentsPage"));

        verify(studentService).searchStudents(null, PageRequest.of(0, 10));
    }

    @Test
    void testListStudents_EmptyPage() throws Exception {
        // Given
        List<Student> students = Collections.emptyList();
        Page<Student> studentsPage = new PageImpl<>(students, PageRequest.of(0, 10), 0);

        when(studentService.searchStudents(anyString(), any(Pageable.class)))
                .thenReturn(studentsPage);

        // When & Then
        mockMvc.perform(get("/students")
                        .param("search", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/list"))
                .andExpect(model().attributeExists("studentsPage"))
                .andExpect(model().attributeDoesNotExist("pageNumbers"));

        verify(studentService).searchStudents("nonexistent", PageRequest.of(0, 10));
    }
}