package com.example.dorm.config;

import com.example.dorm.model.User;
import com.example.dorm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.ApplicationArguments;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatasetAccountInitializerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationArguments applicationArguments;

    private DatasetAccountInitializer initializer;

    @BeforeEach
    void setUp() {
        initializer = new DatasetAccountInitializer(userRepository, passwordEncoder, "12102005");
    }

    @Test
    void normalizesDatasetUser() throws Exception {
        User datasetUser = new User();
        datasetUser.setId(1L);
        datasetUser.setEmail("admin01@example.com");
        datasetUser.setUsername("admin01@example.com");
        datasetUser.setPassword("{noop}password");

        when(userRepository.findAll()).thenReturn(List.of(datasetUser));
        when(userRepository.findByUsername("admin01")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("12102005")).thenReturn("encoded");

        initializer.run(applicationArguments);

        assertEquals("admin01", datasetUser.getUsername());
        assertEquals("encoded", datasetUser.getPassword());
        verify(userRepository).save(datasetUser);
    }

    @Test
    void skipsAlreadyNormalizedUser() throws Exception {
        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setEmail("staff@example.com");
        existingUser.setUsername("staff");
        existingUser.setPassword("$2a$10$012345678901234567890u123456789012345678901234567890");

        when(userRepository.findAll()).thenReturn(List.of(existingUser));

        initializer.run(applicationArguments);

        verify(userRepository, never()).save(any());
    }
}
