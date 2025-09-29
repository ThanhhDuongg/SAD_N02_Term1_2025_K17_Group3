package com.example.dorm.service;

import com.example.dorm.model.Student;
import com.example.dorm.repository.StudentRepository;
import com.example.dorm.repository.RoomRepository;
import com.example.dorm.model.Room;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;

    public StudentService(StudentRepository studentRepository, RoomRepository roomRepository) {
        this.studentRepository = studentRepository;
        this.roomRepository = roomRepository;
    }

    public Page<Student> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudent(Long id) {
        return studentRepository.findById(id);
    }

    public Student getRequiredStudent(Long id) {
        return getStudent(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sinh viên với ID: " + id));
    }

    public Student saveStudent(Student student) {
        validateUniqueCode(student);
        validateUniqueEmail(student);

        if (student.getRoom() != null && student.getRoom().getId() != null) {
            checkRoomCapacity(student.getRoom().getId(), student.getId());
        } else {
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

    public Optional<Student> findByUsername(String username) {
        return studentRepository.findByUser_Username(username);
    }

    public Optional<Student> findByCode(String code) {
        return studentRepository.findByCode(code);
    }

    public Optional<Student> findByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    public long countStudents() {
        return studentRepository.count();
    }

    public Student updateContactInfo(Long studentId, String phone, String email, String address) {
        Student student = getRequiredStudent(studentId);

        if (email != null && !email.isBlank()) {
            student.setEmail(email.trim());
        } else {
            student.setEmail(null);
        }
        student.setPhone(phone != null && !phone.isBlank() ? phone.trim() : null);
        student.setAddress(address != null && !address.isBlank() ? address.trim() : null);

        validateUniqueEmail(student);

        return studentRepository.save(student);
    }

    private void validateUniqueCode(Student student) {
        if (student.getCode() == null || student.getCode().isBlank()) {
            return;
        }
        studentRepository.findByCode(student.getCode())
                .filter(existing -> !existing.getId().equals(student.getId()))
                .ifPresent(existing -> {
                    throw new IllegalStateException("Mã sinh viên đã tồn tại");
                });
    }

    private void validateUniqueEmail(Student student) {
        if (student.getEmail() == null || student.getEmail().isBlank()) {
            return;
        }
        studentRepository.findByEmail(student.getEmail())
                .filter(existing -> !existing.getId().equals(student.getId()))
                .ifPresent(existing -> {
                    throw new IllegalStateException("Email đã tồn tại");
                });
    }

    private void checkRoomCapacity(Long roomId, Long studentId) {
        Room actual = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        long currentOccupancy = studentRepository.countByRoom_Id(roomId);

        if (studentId != null) {
            boolean sameRoom = studentRepository.findById(studentId)
                    .map(Student::getRoom)
                    .map(Room::getId)
                    .filter(roomId::equals)
                    .isPresent();
            if (sameRoom) {
                currentOccupancy -= 1;
            }
        }

        if (currentOccupancy >= actual.getCapacity()) {
            throw new IllegalStateException("Room capacity exceeded");
        }
    }
}
