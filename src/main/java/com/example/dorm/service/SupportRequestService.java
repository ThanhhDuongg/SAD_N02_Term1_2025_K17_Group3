package com.example.dorm.service;

import com.example.dorm.dto.SupportRequestCreateRequest;
import com.example.dorm.dto.SupportRequestResponse;
import com.example.dorm.dto.SupportRequestUpdateRequest;
import com.example.dorm.dto.SupportRequestViolationRequest;
import com.example.dorm.model.Role;
import com.example.dorm.model.RoleName;
import com.example.dorm.model.Student;
import com.example.dorm.model.SupportRequest;
import com.example.dorm.model.SupportRequestStatus;
import com.example.dorm.model.User;
import com.example.dorm.repository.StudentRepository;
import com.example.dorm.repository.SupportRequestRepository;
import com.example.dorm.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SupportRequestService {

    private static final Logger log = LoggerFactory.getLogger(SupportRequestService.class);

    private final SupportRequestRepository supportRequestRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SupportRequestService(SupportRequestRepository supportRequestRepository,
                                 StudentRepository studentRepository,
                                 UserRepository userRepository) {
        this.supportRequestRepository = supportRequestRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SupportRequestResponse createSupportRequest(String username, SupportRequestCreateRequest payload) {
        Student student = studentRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Student profile not found"));

        SupportRequest request = new SupportRequest();
        request.setStudent(student);
        request.setTitle(payload.getTitle().strip());
        request.setDescription(payload.getDescription().strip());
        request.setStatus(SupportRequestStatus.PENDING);
        request.setViolationFlag(false);

        SupportRequest saved = supportRequestRepository.save(request);
        emitEvent("created", saved);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<SupportRequestResponse> getRequestsForStaff(Optional<SupportRequestStatus> status,
                                                            Optional<Boolean> violationOnly,
                                                            Pageable pageable) {
        Page<SupportRequest> page;
        boolean violation = violationOnly.orElse(false);
        if (status.isPresent() && violation) {
            page = supportRequestRepository.findByStatusAndViolationFlagTrue(status.get(), pageable);
        } else if (status.isPresent()) {
            page = supportRequestRepository.findByStatus(status.get(), pageable);
        } else if (violation) {
            page = supportRequestRepository.findByViolationFlagTrue(pageable);
        } else {
            page = supportRequestRepository.findAll(pageable);
        }
        return page.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<SupportRequestResponse> getRequestsForStudent(String username,
                                                              Optional<SupportRequestStatus> status,
                                                              Pageable pageable) {
        Page<SupportRequest> page;
        if (status.isPresent()) {
            page = supportRequestRepository.findByStudent_User_UsernameAndStatus(username, status.get(), pageable);
        } else {
            page = supportRequestRepository.findByStudent_User_Username(username, pageable);
        }
        return page.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public SupportRequestResponse getRequestForUser(Long id, String username) {
        SupportRequest request = supportRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Support request not found"));

        if (isOwner(request, username) || isStaffOrAdmin(username)) {
            return toResponse(request);
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
    }

    @Transactional
    public SupportRequestResponse updateRequest(Long id,
                                                SupportRequestUpdateRequest payload,
                                                String username) {
        User actor = requireStaffOrAdmin(username);
        SupportRequest request = supportRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Support request not found"));

        if (payload.getStatus() != null) {
            request.setStatus(payload.getStatus());
            if (EnumSet.of(SupportRequestStatus.RESOLVED, SupportRequestStatus.REJECTED).contains(payload.getStatus())) {
                request.setResolvedAt(LocalDateTime.now());
            } else if (payload.getStatus() == SupportRequestStatus.IN_PROGRESS && request.getResolvedAt() != null) {
                request.setResolvedAt(null);
            }
        }

        if (payload.getResponseMessage() != null) {
            String response = payload.getResponseMessage().strip();
            request.setResponseMessage(response.isEmpty() ? null : response);
        }

        if (payload.getAssignedStaffId() != null) {
            Long assignedId = payload.getAssignedStaffId();
            if (assignedId != null && assignedId <= 0) {
                request.setAssignedStaff(null);
            } else {
                User assignee = userRepository.findById(assignedId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assigned staff not found"));
                if (!hasAnyRole(assignee, RoleName.ROLE_ADMIN, RoleName.ROLE_STAFF)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assigned user must be staff or admin");
                }
                request.setAssignedStaff(assignee);
            }
        }

        if (payload.getViolationFlag() != null) {
            request.setViolationFlag(payload.getViolationFlag());
        }

        if (payload.getViolationNote() != null) {
            String note = payload.getViolationNote().strip();
            request.setViolationNote(note.isEmpty() ? null : note);
        }

        request.setLastUpdatedBy(actor);

        SupportRequest saved = supportRequestRepository.save(request);
        emitEvent("updated", saved);
        return toResponse(saved);
    }

    @Transactional
    public SupportRequestResponse reportViolation(Long id,
                                                  SupportRequestViolationRequest payload,
                                                  String username) {
        User actor = requireStaffOrAdmin(username);
        SupportRequest request = supportRequestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Support request not found"));

        request.setViolationFlag(payload.isViolation());
        if (payload.getNote() != null) {
            String note = payload.getNote().strip();
            request.setViolationNote(note.isEmpty() ? null : note);
        } else {
            request.setViolationNote(null);
        }
        request.setLastUpdatedBy(actor);

        SupportRequest saved = supportRequestRepository.save(request);
        emitEvent(payload.isViolation() ? "violation" : "violation-cleared", saved);
        return toResponse(saved);
    }

    public SseEmitter registerEmitter() {
        SseEmitter emitter = new SseEmitter(30L * 60 * 1000); // 30 minutes
        emitters.add(emitter);
        emitter.onTimeout(() -> {
            emitter.complete();
            emitters.remove(emitter);
        });
        emitter.onCompletion(() -> emitters.remove(emitter));
        return emitter;
    }

    private void emitEvent(String eventName, SupportRequest request) {
        SupportRequestResponse response = toResponse(request);
        List<SseEmitter> deadEmitters = new ArrayList<>();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name(eventName).data(response));
            } catch (IOException ex) {
                log.debug("Removing closed SSE emitter: {}", ex.getMessage());
                deadEmitters.add(emitter);
            }
        }
        emitters.removeAll(deadEmitters);
    }

    private SupportRequestResponse toResponse(SupportRequest request) {
        Student student = request.getStudent();
        User studentUser = student != null ? student.getUser() : null;
        User assigned = request.getAssignedStaff();
        User updatedBy = request.getLastUpdatedBy();
        return new SupportRequestResponse(
                request.getId(),
                student != null ? student.getCode() : null,
                student != null ? student.getName() : null,
                studentUser != null ? studentUser.getUsername() : null,
                studentUser != null ? studentUser.getEmail() : null,
                request.getTitle(),
                request.getDescription(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getUpdatedAt(),
                request.getResolvedAt(),
                request.getResponseMessage(),
                request.isViolationFlag(),
                request.getViolationNote(),
                assigned != null ? assigned.getId() : null,
                assigned != null ? assigned.getUsername() : null,
                assigned != null ? assigned.getEmail() : null,
                updatedBy != null ? updatedBy.getUsername() : null,
                updatedBy != null ? updatedBy.getEmail() : null
        );
    }

    private boolean isOwner(SupportRequest request, String username) {
        Student student = request.getStudent();
        return student != null
                && student.getUser() != null
                && Objects.equals(student.getUser().getUsername(), username);
    }

    private boolean isStaffOrAdmin(String username) {
        return userRepository.findByUsername(username)
                .map(user -> hasAnyRole(user, RoleName.ROLE_ADMIN, RoleName.ROLE_STAFF))
                .orElse(false);
    }

    private User requireStaffOrAdmin(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "User not found"));
        if (!hasAnyRole(user, RoleName.ROLE_ADMIN, RoleName.ROLE_STAFF)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient permissions");
        }
        return user;
    }

    private boolean hasAnyRole(User user, RoleName... roles) {
        Set<RoleName> allowed = EnumSet.noneOf(RoleName.class);
        for (RoleName roleName : roles) {
            allowed.add(roleName);
        }
        Set<Role> userRoles = user.getRoles();
        if (userRoles == null) {
            return false;
        }
        for (Role role : userRoles) {
            if (allowed.contains(role.getName())) {
                return true;
            }
        }
        return false;
    }
}
