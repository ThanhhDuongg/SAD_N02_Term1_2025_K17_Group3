package com.example.dorm.service;

import com.example.dorm.dto.StudentAccountCredentials;
import com.example.dorm.exception.StudentRegistrationException;
import com.example.dorm.model.Room;
import com.example.dorm.model.RoleName;
import com.example.dorm.model.Student;
import com.example.dorm.model.User;
import com.example.dorm.repository.RoomRepository;
import com.example.dorm.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;


@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;
    private final UserService userService;
    private final String defaultStudentPassword;
    private final SecureRandom secureRandom = new SecureRandom();

    private static final String PASSWORD_CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789@#&$%";
    private static final int PASSWORD_LENGTH = 12;

    public StudentService(StudentRepository studentRepository,
                          RoomRepository roomRepository,
                          UserService userService,
                          @Value("${app.students.default-password:${app.dataset.default-password:123}}")
                          String defaultStudentPassword) {
        this.studentRepository = studentRepository;
        this.roomRepository = roomRepository;
        this.userService = userService;
        this.defaultStudentPassword = defaultStudentPassword;
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

    @Transactional
    public Student saveStudent(Student student) {
        normalizeStudent(student);

        User existingAccount = student.getUser();
        if (student.getId() != null) {
            Student persisted = getRequiredStudent(student.getId());
            if (existingAccount == null) {
                existingAccount = persisted.getUser();
                student.setUser(existingAccount);
            }
        }

        validateUniqueCode(student);
        validateUniqueEmail(student);

        if (student.getRoom() != null && student.getRoom().getId() != null) {
            checkRoomCapacity(student.getRoom().getId(), student.getId());
        } else {
            student.setRoom(null);
        }

        syncStudentAccount(student, existingAccount);

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

    @Transactional
    public StudentAccountCredentials registerStudentAccount(String studentCode, String email) {
        String normalizedCode = trimToNull(studentCode);
        if (normalizedCode == null) {
            throw new StudentRegistrationException("studentCode", "Mã sinh viên không được để trống");
        }

        String normalizedEmail = normalizeEmail(email);
        if (normalizedEmail == null) {
            throw new StudentRegistrationException("email", "Email không được để trống");
        }

        Student student = studentRepository.findByCode(normalizedCode)
                .orElseGet(() -> {
                    Student freshStudent = new Student();
                    freshStudent.setCode(normalizedCode);
                    freshStudent.setStudyYear(1);
                    freshStudent.setName("Sinh viên " + normalizedCode);
                    return freshStudent;
                });

        if (student.getUser() != null) {
            throw new StudentRegistrationException("studentCode", "Sinh viên đã có tài khoản đăng nhập");
        }

        if (student.getEmail() != null && !student.getEmail().equalsIgnoreCase(normalizedEmail)) {
            throw new StudentRegistrationException("email", "Email không trùng khớp với hồ sơ sinh viên");
        }

        student.setEmail(normalizedEmail);
        student.setCode(normalizedCode);
        if (student.getStudyYear() == null) {
            student.setStudyYear(1);
        }

        try {
            validateUniqueEmail(student);
        } catch (IllegalStateException ex) {
            throw new StudentRegistrationException("email", ex.getMessage());
        }

        String username = normalizedEmail;
        String rawPassword = generateTemporaryPassword();

        User account = userService.createUser(username, normalizedEmail, rawPassword, RoleName.ROLE_STUDENT);
        student.setUser(account);
        studentRepository.save(student);

        return new StudentAccountCredentials(username, rawPassword);
    }

    @Transactional
    public Student updateContactInfo(Long studentId, String phone, String email, String address) {
        Student student = getRequiredStudent(studentId);

        student.setEmail(email);
        student.setPhone(phone);
        student.setAddress(address);

        normalizeStudent(student);
        validateUniqueEmail(student);
        syncStudentAccount(student, student.getUser());

        return studentRepository.save(student);
    }

    private void normalizeStudent(Student student) {
        if (student == null) {
            return;
        }
        student.setCode(trimToNull(student.getCode()));
        student.setName(trimToNull(student.getName()));
        student.setGender(trimToNull(student.getGender()));
        student.setPhone(trimToNull(student.getPhone()));
        student.setAddress(trimToNull(student.getAddress()));
        student.setDepartment(trimToNull(student.getDepartment()));
        student.setEmail(normalizeEmail(student.getEmail()));
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeEmail(String email) {
        String trimmed = trimToNull(email);
        return trimmed != null ? trimmed.toLowerCase() : null;
    }

    private void syncStudentAccount(Student student, User existingAccount) {
        if (student == null) {
            return;
        }

        String email = student.getEmail();
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("Sinh viên phải có email để tạo tài khoản đăng nhập");
        }

        User account = existingAccount;
        if (account == null) {
            account = userService.createUser(email, email, defaultStudentPassword, RoleName.ROLE_STUDENT);
        }

        User updatedAccount = userService.updateStudentAccount(account, email, student.getName(), student.getPhone());
        student.setUser(updatedAccount);
    }

    private String generateTemporaryPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = secureRandom.nextInt(PASSWORD_CHARACTERS.length());
            password.append(PASSWORD_CHARACTERS.charAt(index));
        }
        return password.toString();
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
