package com.example.dorm.controller;

import com.example.dorm.model.Room;
import com.example.dorm.service.RoomService;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomControllerTest {
    @Mock
    private RoomService roomService;
    @Mock
    private Model model;
    @Mock
    private RedirectAttributes redirectAttributes;
    @InjectMocks
    private RoomController roomController;

    private Room testRoom;
    private List<Room> roomList;

    @BeforeEach
    void setUp() {
        testRoom = new Room();
        testRoom.setId(1L);
        testRoom.setNumber("101");
        roomList = Arrays.asList(testRoom);
    }

    @Test
    void testListRooms_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Room> roomsPage = new PageImpl<>(roomList, pageable, 1);
        when(roomService.searchRooms(anyString(), any(Pageable.class))).thenReturn(roomsPage);
        when(roomService.getCurrentOccupancy(anyLong())).thenReturn(2L);
        String result = roomController.listRooms("test", 0, 10, model);
        assertEquals("rooms/list", result);
        verify(model).addAttribute("roomsPage", roomsPage);
        verify(model).addAttribute("search", "test");
    }

    @Test
    void testListRooms_Exception() {
        when(roomService.searchRooms(anyString(), any(Pageable.class))).thenThrow(new RuntimeException("Database error"));
        String result = roomController.listRooms("test", 0, 10, model);
        assertEquals("error", result);
        verify(model).addAttribute(eq("errorMessage"), contains("Lỗi khi tải danh sách phòng"));
    }

    @Test
    void testShowCreateForm_Success() {
        String result = roomController.showCreateForm(model);
        assertEquals("rooms/form", result);
        verify(model).addAttribute(eq("room"), any(Room.class));
    }

    @Test
    void testShowCreateForm_Exception() {
        doThrow(new RuntimeException("Service error")).when(model).addAttribute(eq("room"), any(Room.class));
        String result = roomController.showCreateForm(model);
        assertEquals("error", result);
        verify(model).addAttribute(eq("errorMessage"), contains("Lỗi khi hiển thị form tạo phòng"));
    }

    @Test
    void testViewRoom_Success() {
        when(roomService.getRoom(1L)).thenReturn(Optional.of(testRoom));
        when(roomService.getCurrentOccupancy(1L)).thenReturn(2L);
        String result = roomController.viewRoom(1L, model);
        assertEquals("rooms/detail", result);
        verify(model).addAttribute("room", testRoom);
        verify(model).addAttribute("occupancy", 2L);
    }

    @Test
    void testViewRoom_NotFound() {
        when(roomService.getRoom(1L)).thenReturn(Optional.empty());
        String result = roomController.viewRoom(1L, model);
        assertEquals("error", result);
        verify(model).addAttribute(eq("errorMessage"), contains("Không tìm thấy phòng"));
    }

    @Test
    void testViewRoom_Exception() {
        when(roomService.getRoom(1L)).thenThrow(new RuntimeException("Database error"));
        String result = roomController.viewRoom(1L, model);
        assertEquals("error", result);
        verify(model).addAttribute(eq("errorMessage"), contains("Lỗi khi xem phòng"));
    }

    @Test
    void testCreateRoom_Success() {
        when(roomService.createRoom(any(Room.class))).thenReturn(testRoom);
        String result = roomController.createRoom(testRoom, redirectAttributes, model);
        assertEquals("redirect:/rooms", result);
        verify(roomService).createRoom(any(Room.class));
        verify(redirectAttributes).addFlashAttribute("message", contains("Thêm phòng thành công"));
    }

    @Test
    void testCreateRoom_Exception() {
        when(roomService.createRoom(any(Room.class))).thenThrow(new RuntimeException("DB error"));
        String result = roomController.createRoom(testRoom, redirectAttributes, model);
        assertEquals("redirect:/rooms", result);
        verify(redirectAttributes).addFlashAttribute("message", contains("Thêm phòng thất bại"));
    }

    @Test
    void testShowUpdateForm_Success() {
        when(roomService.getRoom(1L)).thenReturn(Optional.of(testRoom));
        String result = roomController.showUpdateForm(1L, model);
        assertEquals("rooms/form", result);
        verify(model).addAttribute("room", testRoom);
    }

    @Test
    void testShowUpdateForm_NotFound() {
        when(roomService.getRoom(1L)).thenReturn(Optional.empty());
        String result = roomController.showUpdateForm(1L, model);
        assertEquals("error", result);
        verify(model).addAttribute(eq("errorMessage"), contains("Không tìm thấy phòng"));
    }

    @Test
    void testShowUpdateForm_Exception() {
        when(roomService.getRoom(1L)).thenThrow(new RuntimeException("DB error"));
        String result = roomController.showUpdateForm(1L, model);
        assertEquals("error", result);
        verify(model).addAttribute(eq("errorMessage"), contains("Lỗi khi hiển thị form sửa phòng"));
    }

    @Test
    void testUpdateRoom_Success() {
        when(roomService.getRoom(1L)).thenReturn(Optional.of(testRoom));
        when(roomService.updateRoom(eq(1L), any(Room.class))).thenReturn(testRoom);
        String result = roomController.updateRoom(1L, testRoom, redirectAttributes, model);
        assertEquals("redirect:/rooms", result);
        verify(roomService).updateRoom(eq(1L), any(Room.class));
        verify(redirectAttributes).addFlashAttribute("message", contains("Cập nhật phòng thành công"));
    }

    @Test
    void testUpdateRoom_NotFound() {
        when(roomService.getRoom(1L)).thenReturn(Optional.empty());
        String result = roomController.updateRoom(1L, testRoom, redirectAttributes, model);
        assertEquals("redirect:/rooms", result);
        verify(redirectAttributes).addFlashAttribute("message", contains("Không tìm thấy phòng"));
    }

    @Test
    void testUpdateRoom_Exception() {
        when(roomService.getRoom(1L)).thenThrow(new RuntimeException("DB error"));
        String result = roomController.updateRoom(1L, testRoom, redirectAttributes, model);
        assertEquals("redirect:/rooms", result);
        verify(redirectAttributes).addFlashAttribute("message", contains("Cập nhật phòng thất bại"));
    }

    @Test
    void testDeleteRoom_Success() {
        when(roomService.getRoom(1L)).thenReturn(Optional.of(testRoom));
        doNothing().when(roomService).deleteRoom(1L);
        String result = roomController.deleteRoom(1L, redirectAttributes, model);
        assertEquals("redirect:/rooms", result);
        verify(roomService).deleteRoom(1L);
        verify(redirectAttributes).addFlashAttribute("message", contains("Xoá phòng thành công"));
    }

    @Test
    void testDeleteRoom_NotFound() {
        when(roomService.getRoom(1L)).thenReturn(Optional.empty());
        String result = roomController.deleteRoom(1L, redirectAttributes, model);
        assertEquals("redirect:/rooms", result);
        verify(redirectAttributes).addFlashAttribute("message", contains("Không tìm thấy phòng"));
    }

    @Test
    void testDeleteRoom_Exception() {
        when(roomService.getRoom(1L)).thenThrow(new RuntimeException("DB error"));
        String result = roomController.deleteRoom(1L, redirectAttributes, model);
        assertEquals("redirect:/rooms", result);
        verify(redirectAttributes).addFlashAttribute("message", contains("Xoá phòng thất bại"));
    }
} 