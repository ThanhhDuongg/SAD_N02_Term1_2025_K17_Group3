package com.example.dorm.controller;

import com.example.dorm.model.Room;
import com.example.dorm.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
@Import(RoomControllerTest.SecurityConfig.class)
class RoomControllerTest {

    @TestConfiguration
    static class SecurityConfig {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(authz -> authz.anyRequest().permitAll())
                    .csrf().disable();
            return http.build();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    private Room sampleRoom;
    private Room secondRoom;
    private List<Room> sampleRooms;

    @BeforeEach
    void setUp() {
        sampleRoom = new Room();
        sampleRoom.setId(1L);
        sampleRoom.setNumber("101");
        sampleRoom.setType("STANDARD");
        sampleRoom.setCapacity(4);
        sampleRoom.setPrice(1000);

        secondRoom = new Room();
        secondRoom.setId(2L);
        secondRoom.setNumber("102");
        secondRoom.setType("DELUXE");
        secondRoom.setCapacity(3);
        secondRoom.setPrice(1200);

        sampleRooms = Arrays.asList(sampleRoom, secondRoom);
    }

    @Test
    void testListRooms_Success() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Room> roomsPage = new PageImpl<>(sampleRooms, pageable, sampleRooms.size());

        when(roomService.searchRooms(isNull(), eq(pageable))).thenReturn(roomsPage);
        when(roomService.getCurrentOccupancy(1L)).thenReturn(2L);
        when(roomService.getCurrentOccupancy(2L)).thenReturn(1L);

        mockMvc.perform(get("/rooms")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("rooms/list"))
                .andExpect(model().attributeExists("roomsPage"))
                .andExpect(model().attributeExists("occupancies"))
                .andExpect(model().attributeExists("pageNumbers"));

        verify(roomService).searchRooms(isNull(), eq(pageable));
        verify(roomService).getCurrentOccupancy(1L);
        verify(roomService).getCurrentOccupancy(2L);
    }

    @Test
    void testListRooms_WithSearchTerm() throws Exception {
        String searchTerm = "101";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Room> roomsPage = new PageImpl<>(List.of(sampleRoom), pageable, 1);

        when(roomService.searchRooms(eq(searchTerm), eq(pageable))).thenReturn(roomsPage);
        when(roomService.getCurrentOccupancy(1L)).thenReturn(2L);

        mockMvc.perform(get("/rooms")
                        .param("search", searchTerm)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("rooms/list"))
                .andExpect(model().attribute("search", searchTerm));

        verify(roomService).searchRooms(eq(searchTerm), eq(pageable));
    }

    @Test
    void testListRooms_Exception() throws Exception {
        when(roomService.searchRooms(any(), any())).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/rooms"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void testShowCreateForm_Success() throws Exception {
        mockMvc.perform(get("/rooms/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("rooms/form"))
                .andExpect(model().attributeExists("room"));
    }

    @Test
    void testCreateRoom_Success() throws Exception {
        when(roomService.createRoom(any(Room.class))).thenReturn(sampleRoom);

        mockMvc.perform(post("/rooms")
                        .param("number", "103")
                        .param("capacity", "4")
                        .param("type", "STANDARD")
                        .param("price", "1200"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rooms"))
                .andExpect(flash().attribute("message", "Thêm phòng thành công!"))
                .andExpect(flash().attribute("alertClass", "alert-success"));

        verify(roomService).createRoom(any(Room.class));
    }

    @Test
    void testCreateRoom_Exception() throws Exception {
        when(roomService.createRoom(any(Room.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/rooms")
                        .param("number", "103")
                        .param("capacity", "4")
                        .param("type", "STANDARD")
                        .param("price", "1200"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rooms"))
                .andExpect(flash().attribute("message", "Thêm phòng thất bại!"))
                .andExpect(flash().attribute("alertClass", "alert-danger"));
    }

    @Test
    void testViewRoom_Success() throws Exception {
        when(roomService.getRoom(1L)).thenReturn(Optional.of(sampleRoom));
        when(roomService.getCurrentOccupancy(1L)).thenReturn(2L);

        mockMvc.perform(get("/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("rooms/detail"))
                .andExpect(model().attributeExists("room"))
                .andExpect(model().attribute("occupancy", 2L));

        verify(roomService).getRoom(1L);
        verify(roomService).getCurrentOccupancy(1L);
    }

    @Test
    void testViewRoom_NotFound() throws Exception {
        when(roomService.getRoom(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void testShowUpdateForm_Success() throws Exception {
        when(roomService.getRoom(1L)).thenReturn(Optional.of(sampleRoom));

        mockMvc.perform(get("/rooms/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("rooms/form"))
                .andExpect(model().attributeExists("room"));

        verify(roomService).getRoom(1L);
    }

    @Test
    void testUpdateRoom_Success() throws Exception {
        when(roomService.getRoom(1L)).thenReturn(Optional.of(sampleRoom));
        when(roomService.updateRoom(eq(1L), any(Room.class))).thenReturn(sampleRoom);

        mockMvc.perform(post("/rooms/1")
                        .param("number", "101")
                        .param("capacity", "4")
                        .param("type", "STANDARD")
                        .param("price", "1000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rooms"))
                .andExpect(flash().attribute("message", "Cập nhật phòng thành công!"))
                .andExpect(flash().attribute("alertClass", "alert-success"));

        verify(roomService).getRoom(1L);
        verify(roomService).updateRoom(eq(1L), any(Room.class));
    }

    @Test
    void testUpdateRoom_Exception() throws Exception {
        when(roomService.getRoom(1L)).thenReturn(Optional.of(sampleRoom));
        when(roomService.updateRoom(eq(1L), any(Room.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/rooms/1")
                        .param("number", "101")
                        .param("capacity", "4")
                        .param("type", "STANDARD")
                        .param("price", "1000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rooms"))
                .andExpect(flash().attribute("message", "Cập nhật phòng thất bại!"))
                .andExpect(flash().attribute("alertClass", "alert-danger"));
    }

    @Test
    void testDeleteRoom_Success() throws Exception {
        when(roomService.getRoom(1L)).thenReturn(Optional.of(sampleRoom));
        doNothing().when(roomService).deleteRoom(1L);

        mockMvc.perform(get("/rooms/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rooms"))
                .andExpect(flash().attribute("message", "Xoá phòng thành công!"))
                .andExpect(flash().attribute("alertClass", "alert-success"));

        verify(roomService).getRoom(1L);
        verify(roomService).deleteRoom(1L);
    }

    @Test
    void testDeleteRoom_NotFound() throws Exception {
        when(roomService.getRoom(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/rooms/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/rooms"))
                .andExpect(flash().attribute("message", "Không tìm thấy phòng với ID: 1"))
                .andExpect(flash().attribute("alertClass", "alert-danger"));

        verify(roomService).getRoom(1L);
        verify(roomService, never()).deleteRoom(any());
    }
}
