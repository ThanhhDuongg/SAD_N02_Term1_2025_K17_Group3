package com.example.dorm.service;

import com.example.dorm.model.Room;
import com.example.dorm.repository.RoomRepository;
import com.example.dorm.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private StudentRepository studentRepository;


    public Page<Room> getAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> getRoom(Long id) {
        return roomRepository.findById(id);
    }

    public Room createRoom(Room room) {
        if ("Phòng bốn".equals(room.getType())) {
            room.setPrice(2000000);
        } else if ("Phòng tám".equals(room.getType())) {
            room.setPrice(1200000);
        }
        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, Room room) {
        Room existing = roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        room.setId(id);
        room.setType(existing.getType());
        room.setPrice(existing.getPrice());
        return roomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

     public long getCurrentOccupancy(Long roomId) {
        return studentRepository.countByRoom_Id(roomId);
    }


    public Page<Room> searchRooms(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return roomRepository.findAll(pageable);
        }
        return roomRepository.findByNumberContainingIgnoreCaseOrTypeContainingIgnoreCase(search, search, pageable);
    }
}
