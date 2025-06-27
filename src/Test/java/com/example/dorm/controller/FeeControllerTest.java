package com.example.dorm.controller;

import com.example.dorm.model.Fee;
import com.example.dorm.service.FeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeeController.class)
public class FeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeeService feeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void contextLoads() throws Exception {
        // Đảm bảo controller được load lên
        mockMvc.perform(get("/fees"))
                .andExpect(status().isOk());
    }

    @Test
    void testListFees() throws Exception {
        mockMvc.perform(get("/fees"))
                .andExpect(status().isOk())
                .andExpect(view().name("fees/list"));
    }

    @Test
    void testShowCreateForm() throws Exception {
        mockMvc.perform(get("/fees/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("fees/form"));
    }

    @Test
    void testCreateFeeWithInvalidData() throws Exception {
        mockMvc.perform(post("/fees")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("amountInput", "") // thiếu amount
        )
                .andExpect(status().isOk())
                .andExpect(view().name("fees/form"));
    }

    @Test
    void testViewFeeNotFound() throws Exception {
        org.mockito.Mockito.when(feeService.getFee(999L)).thenReturn(java.util.Optional.empty());
        mockMvc.perform(get("/fees/999"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));
    }

    @Test
    void testShowUpdateFormNotFound() throws Exception {
        org.mockito.Mockito.when(feeService.getFee(999L)).thenReturn(java.util.Optional.empty());
        mockMvc.perform(get("/fees/999/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));
    }

    @Test
    void testDeleteFeeNotFound() throws Exception {
        org.mockito.Mockito.when(feeService.getFee(999L)).thenReturn(java.util.Optional.empty());
        mockMvc.perform(get("/fees/999/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fees"));
    }

    @Test
    void testViewFeeSuccess() throws Exception {
        Fee fee = new Fee();
        fee.setId(1L);
        org.mockito.Mockito.when(feeService.getFee(1L)).thenReturn(java.util.Optional.of(fee));
        mockMvc.perform(get("/fees/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("fees/detail"));
    }

    @Test
    void testShowUpdateFormSuccess() throws Exception {
        Fee fee = new Fee();
        fee.setId(1L);
        org.mockito.Mockito.when(feeService.getFee(1L)).thenReturn(java.util.Optional.of(fee));
        mockMvc.perform(get("/fees/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("fees/form"));
    }

    @Test
    void testDeleteFeeSuccess() throws Exception {
        Fee fee = new Fee();
        fee.setId(1L);
        org.mockito.Mockito.when(feeService.getFee(1L)).thenReturn(java.util.Optional.of(fee));
        mockMvc.perform(get("/fees/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fees"));
    }

    @Test
    void testCreateFeeSuccess() throws Exception {
        org.mockito.Mockito.when(feeService.createFee(org.mockito.Mockito.any(Fee.class))).thenReturn(new Fee());
        mockMvc.perform(post("/fees")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("amountInput", "100000")
                .param("contract.id", "1")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fees"));
    }

    @Test
    void testUpdateFeeSuccess() throws Exception {
        org.mockito.Mockito.when(feeService.updateFee(org.mockito.Mockito.eq(1L), org.mockito.Mockito.any(Fee.class))).thenReturn(new Fee());
        mockMvc.perform(post("/fees/1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("amountInput", "100000")
                .param("contract.id", "1")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/fees"));
    }
} 