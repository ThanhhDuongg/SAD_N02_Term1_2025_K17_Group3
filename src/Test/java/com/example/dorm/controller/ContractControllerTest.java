package com.example.dorm.controller;

import com.example.dorm.model.Contract;
import com.example.dorm.service.ContractService;
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

@WebMvcTest(ContractController.class)
public class ContractControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContractService contractService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void contextLoads() throws Exception {
        // Đảm bảo controller được load lên
        mockMvc.perform(get("/contracts"))
                .andExpect(status().isOk());
    }

    @Test
    void testListContracts() throws Exception {
        mockMvc.perform(get("/contracts"))
                .andExpect(status().isOk())
                .andExpect(view().name("contracts/list"));
    }

    @Test
    void testShowCreateForm() throws Exception {
        mockMvc.perform(get("/contracts/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("contracts/form"));
    }

    @Test
    void testCreateContractWithInvalidData() throws Exception {
        mockMvc.perform(post("/contracts")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("student.id", "") // thiếu student id
        )
                .andExpect(status().isOk())
                .andExpect(view().name("contracts/form"));
    }

    @Test
    void testViewContractNotFound() throws Exception {
        org.mockito.Mockito.when(contractService.getContract(999L)).thenReturn(java.util.Optional.empty());
        mockMvc.perform(get("/contracts/999"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));
    }

    @Test
    void testShowUpdateFormNotFound() throws Exception {
        org.mockito.Mockito.when(contractService.getContract(999L)).thenReturn(java.util.Optional.empty());
        mockMvc.perform(get("/contracts/999/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));
    }

    @Test
    void testDeleteContractNotFound() throws Exception {
        org.mockito.Mockito.when(contractService.getContract(999L)).thenReturn(java.util.Optional.empty());
        mockMvc.perform(get("/contracts/999/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contracts"));
    }

    @Test
    void testViewContractSuccess() throws Exception {
        Contract contract = new Contract();
        contract.setId(1L);
        org.mockito.Mockito.when(contractService.getContract(1L)).thenReturn(java.util.Optional.of(contract));
        mockMvc.perform(get("/contracts/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("contracts/detail"));
    }

    @Test
    void testShowUpdateFormSuccess() throws Exception {
        Contract contract = new Contract();
        contract.setId(1L);
        org.mockito.Mockito.when(contractService.getContract(1L)).thenReturn(java.util.Optional.of(contract));
        mockMvc.perform(get("/contracts/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("contracts/form"));
    }

    @Test
    void testDeleteContractSuccess() throws Exception {
        Contract contract = new Contract();
        contract.setId(1L);
        org.mockito.Mockito.when(contractService.getContract(1L)).thenReturn(java.util.Optional.of(contract));
        mockMvc.perform(get("/contracts/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contracts"));
    }

    @Test
    void testCreateContractSuccess() throws Exception {
        org.mockito.Mockito.when(contractService.createContract(org.mockito.Mockito.any(Contract.class))).thenReturn(new Contract());
        mockMvc.perform(post("/contracts")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("student.id", "1")
                .param("room.id", "1")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contracts"));
    }

    @Test
    void testUpdateContractSuccess() throws Exception {
        org.mockito.Mockito.when(contractService.updateContract(org.mockito.Mockito.eq(1L), org.mockito.Mockito.any(Contract.class))).thenReturn(new Contract());
        mockMvc.perform(post("/contracts/1")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("student.id", "1")
                .param("room.id", "1")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contracts"));
    }
} 