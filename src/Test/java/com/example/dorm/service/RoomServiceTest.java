package com.example.dorm.service;

import com.example.dorm.model.Room;
import com.example.dorm.repository.RoomRepository;
import com.example.dorm.repository.StudentRepository;
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
public class RoomServiceTest {
    @Mock RoomRepository roomRepository;
    @Mock StudentRepository studentRepository;
    @InjectMocks RoomService roomService;

    @Test
    void testGetAllRoomsPageable() {
        Page<Room> page = new PageImpl<>(List.of(new Room()));
        when(roomRepository.findAll(any(Pageable.class))).thenReturn(page);
        assertEquals(1, roomService.getAllRooms(Pageable.unpaged()).getTotalElements());
    }
    @Test
    void testGetAllRoomsList() {
        when(roomRepository.findAll()).thenReturn(List.of(new Room()));
        assertEquals(1, roomService.getAllRooms().size());
    }
    @Test
    void testGetRoomFound() {
        Room r = new Room(); r.setId(1L);
        when(roomRepository.findById(1L)).thenReturn(Optional.of(r));
        assertTrue(roomService.getRoom(1L).isPresent());
    }
    @Test
    void testGetRoomNotFound() {
        when(roomRepository.findById(2L)).thenReturn(Optional.empty());
        assertFalse(roomService.getRoom(2L).isPresent());
    }
    @Test
    void testCreateRoomTypeBon() {
        Room r = new Room(); r.setType("Phòng bốn");
        when(roomRepository.save(any())).thenReturn(r);
        Room result = roomService.createRoom(r);
        assertEquals(2000000, result.getPrice());
    }
    @Test
    void testCreateRoomTypeTam() {
        Room r = new Room(); r.setType("Phòng tám");
        when(roomRepository.save(any())).thenReturn(r);
        Room result = roomService.createRoom(r);
        assertEquals(1200000, result.getPrice());
    }
    @Test
    void testUpdateRoomNotFound() {
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> roomService.updateRoom(1L, new Room()));
    }
    @Test
    void testDeleteRoom() {
        doNothing().when(roomRepository).deleteById(1L);
        roomService.deleteRoom(1L);
        verify(roomRepository, times(1)).deleteById(1L);
    }
    @Test
    void testGetCurrentOccupancy() {
        when(studentRepository.countByRoom_Id(1L)).thenReturn(3L);
        assertEquals(3L, roomService.getCurrentOccupancy(1L));
    }
    @Test
    void testSearchRooms() {
        Page<Room> page = new PageImpl<>(List.of(new Room()));
        when(roomRepository.findAll(any(Pageable.class))).thenReturn(page);
        assertEquals(1, roomService.searchRooms(null, Pageable.unpaged()).getTotalElements());
    }
} 