package com.example.dorm.service;

import com.example.dorm.model.Fee;
import com.example.dorm.model.FeeType;
import com.example.dorm.repository.FeeRepository;
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
import java.math.BigDecimal;


@ExtendWith(MockitoExtension.class)
public class FeeServiceTest {
    @Mock FeeRepository feeRepository;
    @InjectMocks FeeService feeService;

    @Test
    void testGetAllFees() {
        Page<Fee> page = new PageImpl<>(List.of(new Fee()));
        when(feeRepository.findAll(any(Pageable.class))).thenReturn(page);
        assertEquals(1, feeService.getAllFees(Pageable.unpaged()).getTotalElements());
    }
    @Test
    void testGetFeeFound() {
        Fee f = new Fee(); f.setId(1L);
        when(feeRepository.findById(1L)).thenReturn(Optional.of(f));
        assertTrue(feeService.getFee(1L).isPresent());
    }
    @Test
    void testGetFeeNotFound() {
        when(feeRepository.findById(2L)).thenReturn(Optional.empty());
        assertFalse(feeService.getFee(2L).isPresent());
    }
    @Test
    void testCreateFee() {
        Fee f = new Fee();
        when(feeRepository.save(f)).thenReturn(f);
        assertEquals(f, feeService.createFee(f));
    }
    @Test
    void testUpdateFeeNotFound() {
        when(feeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> feeService.updateFee(1L, new Fee()));
    }
    @Test
    void testUpdateFeeSuccess() {
        Fee existing = new Fee(); existing.setId(1L);
        Fee update = new Fee(); update.setAmount(BigDecimal.valueOf(10000.0));
        when(feeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(feeRepository.save(any())).thenReturn(existing);
        Fee result = feeService.updateFee(1L, update);
        assertNotNull(result);
    }
    @Test
    void testDeleteFee() {
        doNothing().when(feeRepository).deleteById(1L);
        feeService.deleteFee(1L);
        verify(feeRepository, times(1)).deleteById(1L);
    }
    @Test
    void testSearchFeesByType() {
        Page<Fee> page = new PageImpl<>(List.of(new Fee()));
        when(feeRepository.findAll(any(Pageable.class))).thenReturn(page);
        assertEquals(1, feeService.searchFees(null, Pageable.unpaged()).getTotalElements());
    }
} 