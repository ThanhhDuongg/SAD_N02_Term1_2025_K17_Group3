package com.example.dorm.service;

import com.example.dorm.model.Role;
import com.example.dorm.model.RoleName;
import com.example.dorm.model.Student;
import com.example.dorm.model.User;
import com.example.dorm.repository.RoleRepository;
import com.example.dorm.repository.StudentRepository;
import com.example.dorm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class StudentServiceIntegrationTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        studentRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role studentRole = new Role();
        studentRole.setName(RoleName.ROLE_STUDENT);
        studentRole.setDescription("Student role");
        roleRepository.save(studentRole);
    }

    @Test
    void creatingStudentCreatesLoginAccountWithDefaultPassword() {
        Student student = buildStudent("SV100", "student100@example.com");

        Student saved = studentService.saveStudent(student);

        assertThat(saved.getUser()).isNotNull();
        User account = userRepository.findById(saved.getUser().getId()).orElseThrow();
        assertThat(account.getUsername()).isEqualTo("student100@example.com");
        assertThat(account.getEmail()).isEqualTo("student100@example.com");
        assertThat(account.getRoles()).extracting(role -> role.getName()).containsExactly(RoleName.ROLE_STUDENT);
        assertThat(passwordEncoder.matches("123", account.getPassword())).isTrue();
    }

    @Test
    void saveStudentWithExistingAccountKeepsAccountAndUpdatesProfile() {
        Student existing = buildStudent("SV200", "legacy@example.com");
        existing = studentRepository.save(existing);

        User customAccount = userService.createUser("custom_username", "student200@example.com", "StrongPass!1", RoleName.ROLE_STUDENT);

        existing.setEmail("student200@example.com");
        existing.setUser(customAccount);
        existing.setPhone("0987654321");
        existing.setName("Updated Name");

        Student updated = studentService.saveStudent(existing);

        assertThat(userRepository.count()).isEqualTo(1);
        User linkedAccount = userRepository.findById(updated.getUser().getId()).orElseThrow();
        assertThat(linkedAccount.getId()).isEqualTo(customAccount.getId());
        assertThat(linkedAccount.getUsername()).isEqualTo("student200@example.com");
        assertThat(linkedAccount.getEmail()).isEqualTo("student200@example.com");
        assertThat(linkedAccount.getFullName()).isEqualTo("Updated Name");
        assertThat(linkedAccount.getPhone()).isEqualTo("0987654321");
        assertThat(passwordEncoder.matches("StrongPass!1", linkedAccount.getPassword())).isTrue();
    }

    private Student buildStudent(String code, String email) {
        Student student = new Student();
        student.setCode(code);
        student.setName("Test Student");
        student.setDob(LocalDate.of(2003, 1, 1));
        student.setGender("Nam");
        student.setPhone("0123456789");
        student.setAddress("123 Test Street");
        student.setEmail(email);
        student.setDepartment("IT");
        student.setStudyYear(2);
        student.setRoom(null);
        return student;
    }
}
