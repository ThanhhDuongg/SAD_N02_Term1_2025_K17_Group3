package com.example.dorm.service;

import com.example.dorm.model.Student;
import com.example.dorm.repository.StudentRepository;
import com.example.dorm.repository.RoomRepository;
import com.example.dorm.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RoomRepository roomRepository;


    public Page<Student> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    public Optional<Student> getStudent(Long id) {
        return studentRepository.findById(id);
    }

    public Student saveStudent(Student student) {
        if (student.getCode() != null) {
            var existingOpt = studentRepository.findByCode(student.getCode());
            if (existingOpt.isPresent()) {
                if (student.getId() == null || !existingOpt.get().getId().equals(student.getId())) {
                    throw new IllegalStateException("Mã sinh viên đã tồn tại");
                }
            }
        }
        if (student.getEmail() != null) {
            var existingEmailOpt = studentRepository.findByEmail(student.getEmail());
            if (existingEmailOpt.isPresent()) {
                if (student.getId() == null || !existingEmailOpt.get().getId().equals(student.getId())) {
                    throw new IllegalStateException("Email đã tồn tại");
                }
            }
        }
        if (student.getRoom() != null && student.getRoom().getId() != null) {
            checkRoomCapacity(student.getRoom(), student.getId());
        } else {
            // treat null id as no room selected
            student.setRoom(null);
        }
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public Page<Student> searchStudents(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return studentRepository.findAll(pageable);
        }
        return studentRepository.searchByCodeOrNameWord(search, pageable);
    }

    private void checkRoomCapacity(Room room, Long studentId) {
        Room actual = roomRepository.findById(room.getId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        long current = studentRepository.countByRoom_Id(room.getId());
        if (studentId != null) {
            Optional<Student> existingOpt = studentRepository.findById(studentId);
            if (existingOpt.isPresent()) {
                Student existing = existingOpt.get();
                if (existing.getRoom() != null && existing.getRoom().getId().equals(room.getId())) {
                    current -= 1;
                }
            }
        }
        if (current >= actual.getCapacity()) {
            throw new IllegalStateException("Room capacity exceeded");
        }
    }

}