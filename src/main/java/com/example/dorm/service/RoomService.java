package com.example.dorm.service;

import com.example.dorm.model.Room;
import com.example.dorm.repository.RoomRepository;
import com.example.dorm.repository.StudentRepository;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final StudentRepository studentRepository;

    public RoomService(RoomRepository roomRepository, StudentRepository studentRepository) {
        this.roomRepository = roomRepository;
        this.studentRepository = studentRepository;
    }


    public Page<Room> getAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> getRoom(Long id) {
        return roomRepository.findById(id);
    }

    public Room getRequiredRoom(Long id) {
        return getRoom(id).orElseThrow(() -> new IllegalArgumentException("Room not found"));
    }

    public Room createRoom(Room room) {
        room.setPrice(resolvePrice(room));
        return roomRepository.save(room);
    }

    public Room updateRoom(Long id, Room room) {
        Room existing = getRequiredRoom(id);
        existing.setNumber(room.getNumber());
        existing.setType(room.getType());
        existing.setCapacity(room.getCapacity());
        existing.setPrice(resolvePrice(room));
        return roomRepository.save(existing);
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

    public long countRooms() {
        return roomRepository.count();
    }

    private int resolvePrice(Room room) {
        String type = room.getType();
        if (type == null) {
            return room.getPrice();
        }
        return switch (type) {
            case "Phòng bốn" -> 2_000_000;
            case "Phòng tám" -> 1_200_000;
            default -> room.getPrice();
        };
    }
}
