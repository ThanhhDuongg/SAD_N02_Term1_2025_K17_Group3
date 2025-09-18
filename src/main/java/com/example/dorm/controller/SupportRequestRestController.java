package com.example.dorm.controller;

import com.example.dorm.dto.SupportRequestCreateRequest;
import com.example.dorm.dto.SupportRequestResponse;
import com.example.dorm.dto.SupportRequestUpdateRequest;
import com.example.dorm.dto.SupportRequestViolationRequest;
import com.example.dorm.model.SupportRequestStatus;
import com.example.dorm.service.SupportRequestService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

@RestController
@RequestMapping("/api/support-requests")
public class SupportRequestRestController {

    private final SupportRequestService supportRequestService;

    public SupportRequestRestController(SupportRequestService supportRequestService) {
        this.supportRequestService = supportRequestService;
    }

    @PostMapping
    public ResponseEntity<SupportRequestResponse> createSupportRequest(@Valid @RequestBody SupportRequestCreateRequest payload,
                                                                       Authentication authentication) {
        SupportRequestResponse response = supportRequestService.createSupportRequest(authentication.getName(), payload);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public Page<SupportRequestResponse> getSupportRequests(@RequestParam(value = "status", required = false) SupportRequestStatus status,
                                                           @RequestParam(value = "violationOnly", required = false) Boolean violationOnly,
                                                           @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return supportRequestService.getRequestsForStaff(Optional.ofNullable(status), Optional.ofNullable(violationOnly), pageable);
    }

    @GetMapping("/mine")
    public Page<SupportRequestResponse> getMySupportRequests(@RequestParam(value = "status", required = false) SupportRequestStatus status,
                                                             Authentication authentication,
                                                             @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return supportRequestService.getRequestsForStudent(authentication.getName(), Optional.ofNullable(status), pageable);
    }

    @GetMapping("/{id}")
    public SupportRequestResponse getSupportRequest(@PathVariable Long id,
                                                     Authentication authentication) {
        return supportRequestService.getRequestForUser(id, authentication.getName());
    }

    @PatchMapping("/{id}")
    public SupportRequestResponse updateSupportRequest(@PathVariable Long id,
                                                        @Valid @RequestBody SupportRequestUpdateRequest payload,
                                                        Authentication authentication) {
        return supportRequestService.updateRequest(id, payload, authentication.getName());
    }

    @PostMapping("/{id}/violation")
    public SupportRequestResponse flagViolation(@PathVariable Long id,
                                                 @Valid @RequestBody SupportRequestViolationRequest payload,
                                                 Authentication authentication) {
        return supportRequestService.reportViolation(id, payload, authentication.getName());
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamSupportRequestEvents() {
        return supportRequestService.registerEmitter();
    }
}
