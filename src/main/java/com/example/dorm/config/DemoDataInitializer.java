package com.example.dorm.config;

import com.example.dorm.model.*;
import com.example.dorm.repository.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Component
public class DemoDataInitializer implements ApplicationRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final StudentRepository studentRepository;
    private final ContractRepository contractRepository;
    private final FeeRepository feeRepository;
    private final MaintenanceRequestRepository maintenanceRequestRepository;
    private final ViolationRepository violationRepository;
    private final DormRegistrationRequestRepository dormRegistrationRequestRepository;
    private final DormRegistrationPeriodRepository dormRegistrationPeriodRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataInitializer(RoleRepository roleRepository,
                               UserRepository userRepository,
                               RoomRepository roomRepository,
                               RoomTypeRepository roomTypeRepository,
                               StudentRepository studentRepository,
                               ContractRepository contractRepository,
                               FeeRepository feeRepository,
                               MaintenanceRequestRepository maintenanceRequestRepository,
                               ViolationRepository violationRepository,
                               DormRegistrationRequestRepository dormRegistrationRequestRepository,
                               DormRegistrationPeriodRepository dormRegistrationPeriodRepository,
                               PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.studentRepository = studentRepository;
        this.contractRepository = contractRepository;
        this.feeRepository = feeRepository;
        this.maintenanceRequestRepository = maintenanceRequestRepository;
        this.violationRepository = violationRepository;
        this.dormRegistrationRequestRepository = dormRegistrationRequestRepository;
        this.dormRegistrationPeriodRepository = dormRegistrationPeriodRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Role adminRole = ensureRole(RoleName.ROLE_ADMIN, "Quản lý KTX");
        Role staffRole = ensureRole(RoleName.ROLE_STAFF, "Nhân viên hỗ trợ");
        Role studentRole = ensureRole(RoleName.ROLE_STUDENT, "Sinh viên");

        ensureUser("admin", "admin@example.com", "password", adminRole);
        ensureUser("staff", "staff@example.com", "password", staffRole);
        User sv01User = ensureUser("sv01", "sv01-login@example.com", "password", studentRole);
        User sv02User = ensureUser("sv02", "sv02-login@example.com", "password", studentRole);

        RoomType phongTam = ensureRoomType("Phòng tám", 8, 1_200_000, "Phòng ở 8 người");
        RoomType phongBon = ensureRoomType("Phòng bốn", 4, 2_000_000, "Phòng ở 4 người");
        RoomType phongDoi = ensureRoomType("Phòng đôi", 2, 2_500_000, "Phòng ở 2 người");

        Room room101 = ensureRoom("101", phongTam);
        Room room102 = ensureRoom("102", phongTam);
        Room room201 = ensureRoom("201", phongBon);
        Room room202 = ensureRoom("202", phongBon);

        Student sv01 = ensureStudent("SV01", "Nguyễn Văn An", LocalDate.of(2005, 3, 15), "Nam",
                "0912345601", "Hà Nội", "sv01@example.com", "Công nghệ Thông tin", 3, sv01User, room101);
        Student sv02 = ensureStudent("SV02", "Trần Thị Bình", LocalDate.of(2005, 7, 22), "Nữ",
                "0912345602", "Ninh Bình", "sv02@example.com", "Kinh tế", 2, sv02User, room102);
        Student sv03 = ensureStudent("SV03", "Phạm Minh Châu", LocalDate.of(2004, 11, 5), "Nữ",
                "0912345603", "Đà Nẵng", "sv03@example.com", "Du lịch", 4, null, room201);
        Student sv04 = ensureStudent("SV04", "Lê Quốc Dũng", LocalDate.of(2005, 1, 30), "Nam",
                "0912345604", "Hải Phòng", "sv04@example.com", "Điện tử", 1, null, room202);

        Contract contract1 = ensureContract(sv01, room101, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "ACTIVE");
        Contract contract2 = ensureContract(sv02, room102, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "ACTIVE");
        Contract contract3 = ensureContract(sv03, room201, LocalDate.of(2025, 2, 1), LocalDate.of(2025, 12, 31), "ACTIVE");
        Contract contract4 = ensureContract(sv04, room202, LocalDate.of(2024, 9, 1), LocalDate.of(2025, 8, 31), "EXPIRED");

        ensureFee(contract1, FeeType.RENT, new BigDecimal("1200000"), LocalDate.of(2025, 3, 1), PaymentStatus.PAID);
        ensureFee(contract1, FeeType.ELECTRICITY, new BigDecimal("180000"), LocalDate.of(2025, 3, 5), PaymentStatus.UNPAID);
        ensureFee(contract2, FeeType.RENT, new BigDecimal("1200000"), LocalDate.of(2025, 3, 1), PaymentStatus.PAID);
        ensureFee(contract2, FeeType.WATER, new BigDecimal("90000"), LocalDate.of(2025, 3, 5), PaymentStatus.PAID);
        ensureFee(contract3, FeeType.RENT, new BigDecimal("2000000"), LocalDate.of(2025, 3, 10), PaymentStatus.UNPAID);
        ensureFee(contract3, FeeType.MAINTENANCE, new BigDecimal("85000"), LocalDate.of(2025, 3, 15), PaymentStatus.PAID);
        ensureFee(contract4, FeeType.RENT, new BigDecimal("2000000"), LocalDate.of(2024, 10, 1), PaymentStatus.PAID);
        ensureFee(contract4, FeeType.ELECTRICITY, new BigDecimal("150000"), LocalDate.of(2024, 10, 5), PaymentStatus.PAID);

        ensureMaintenanceRequest(sv01, room101, "Đèn phòng bị hỏng, cần thay mới", "MAINTENANCE", null, "PENDING");
        ensureMaintenanceRequest(sv02, room102, "Xin chuyển sang phòng 201 để học nhóm", "ROOM_TRANSFER", "201", "IN_PROGRESS");

        ensureViolation(sv01, room101, "Tụ tập quá giờ quy định", "MEDIUM", LocalDate.of(2025, 2, 15));
        ensureViolation(sv03, room201, "Không tuân thủ quy định dọn vệ sinh", "LOW", LocalDate.of(2025, 3, 1));

        DormRegistrationPeriod currentPeriod = ensureDormRegistrationPeriod(
                "Đợt đăng ký HK1 2025",
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(10),
                200,
                "Đợt demo đang mở",
                DormRegistrationPeriodStatus.OPEN);

        DormRegistrationPeriod closedPeriod = ensureDormRegistrationPeriod(
                "Đợt đăng ký HK2 2024",
                LocalDateTime.now().minusMonths(6),
                LocalDateTime.now().minusMonths(5),
                150,
                "Đợt đã kết thúc",
                DormRegistrationPeriodStatus.CLOSED);

        ensureDormRegistrationRequest(sv01, currentPeriod, "Phòng 4 người", null, LocalDate.now().plusWeeks(2),
                "Muốn chuyển sang phòng ít người để thuận tiện học nhóm", DormRegistrationStatus.PENDING, null);
        ensureDormRegistrationRequest(sv03, closedPeriod, "Phòng 2 người", "301", LocalDate.now().plusMonths(1),
                "Cần không gian yên tĩnh để chuẩn bị đồ án", DormRegistrationStatus.NEEDS_UPDATE,
                "Vui lòng bổ sung giấy xác nhận của khoa");
    }

    private Role ensureRole(RoleName name, String description) {
        return roleRepository.findByName(name).map(role -> {
            if (description != null && (role.getDescription() == null || !role.getDescription().equals(description))) {
                role.setDescription(description);
                return roleRepository.save(role);
            }
            return role;
        }).orElseGet(() -> {
            Role role = new Role();
            role.setName(name);
            role.setDescription(description);
            return roleRepository.save(role);
        });
    }

    private User ensureUser(String username, String email, String rawPassword, Role role) {
        Optional<User> existingOpt = userRepository.findByUsername(username);
        if (existingOpt.isPresent()) {
            User existing = existingOpt.get();
            boolean changed = false;
            if (email != null && (existing.getEmail() == null || !existing.getEmail().equalsIgnoreCase(email))) {
                existing.setEmail(email);
                changed = true;
            }
            Set<Role> roles = existing.getRoles();
            if (roles == null) {
                roles = new HashSet<>();
                existing.setRoles(roles);
            }
            if (roles.stream().noneMatch(r -> r.getName() == role.getName())) {
                roles.add(role);
                changed = true;
            }
            if (rawPassword != null && !passwordEncoder.matches(rawPassword, existing.getPassword())) {
                existing.setPassword(passwordEncoder.encode(rawPassword));
                changed = true;
            }
            return changed ? userRepository.save(existing) : existing;
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(rawPassword != null ? passwordEncoder.encode(rawPassword) : null);
        user.setEnabled(true);
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
        return userRepository.save(user);
    }

    private Room ensureRoom(String number, RoomType roomType) {
        return roomRepository.findByNumber(number).map(existing -> {
            boolean changed = false;
            if (!number.equals(existing.getNumber())) {
                existing.setNumber(number);
                changed = true;
            }
            RoomType previousType = existing.getRoomType();
            String previousName = existing.getType();
            int previousCapacity = existing.getCapacity();
            int previousPrice = existing.getPrice();
            existing.setRoomType(roomType);
            if (previousType == null || !Objects.equals(previousType.getId(), roomType.getId())) {
                changed = true;
            } else if (!Objects.equals(previousName, existing.getType())
                    || previousCapacity != existing.getCapacity()
                    || previousPrice != existing.getPrice()) {
                changed = true;
            }
            return changed ? roomRepository.save(existing) : existing;
        }).orElseGet(() -> {
            Room room = new Room();
            room.setNumber(number);
            room.setRoomType(roomType);
            return roomRepository.save(room);
        });
    }

    private RoomType ensureRoomType(String name, int capacity, int price, String description) {
        String normalizedDescription = description != null ? description.trim() : null;
        return roomTypeRepository.findByNameIgnoreCase(name).map(existing -> {
            boolean changed = false;
            if (existing.getCapacity() != capacity) {
                existing.setCapacity(capacity);
                changed = true;
            }
            if (existing.getCurrentPrice() != price) {
                existing.setCurrentPrice(price);
                changed = true;
            }
            if (!Objects.equals(existing.getDescription(), normalizedDescription)) {
                existing.setDescription(normalizedDescription);
                changed = true;
            }
            return changed ? roomTypeRepository.save(existing) : existing;
        }).orElseGet(() -> {
            RoomType roomType = new RoomType();
            roomType.setName(name);
            roomType.setCapacity(capacity);
            roomType.setCurrentPrice(price);
            roomType.setDescription(normalizedDescription);
            return roomTypeRepository.save(roomType);
        });
    }

    private Student ensureStudent(String code,
                                  String name,
                                  LocalDate dob,
                                  String gender,
                                  String phone,
                                  String address,
                                  String email,
                                  String department,
                                  int studyYear,
                                  User user,
                                  Room room) {
        Student student = studentRepository.findByCode(code).orElseGet(Student::new);
        student.setCode(code);
        student.setName(name);
        student.setDob(dob);
        student.setGender(gender);
        student.setPhone(phone);
        student.setAddress(address);
        student.setEmail(email);
        student.setDepartment(department);
        student.setStudyYear(studyYear);
        student.setRoom(room);
        if (user != null) {
            student.setUser(user);
            user.setStudent(student);
            userRepository.save(user);
        }
        return studentRepository.save(student);
    }

    private Contract ensureContract(Student student,
                                    Room room,
                                    LocalDate startDate,
                                    LocalDate endDate,
                                    String status) {
        return contractRepository.findByStudent_IdAndRoom_IdAndStartDate(student.getId(), room.getId(), startDate)
                .map(existing -> {
                    boolean changed = false;
                    if (!startDate.equals(existing.getStartDate())) {
                        existing.setStartDate(startDate);
                        changed = true;
                    }
                    if (!endDate.equals(existing.getEndDate())) {
                        existing.setEndDate(endDate);
                        changed = true;
                    }
                    if (!status.equalsIgnoreCase(existing.getStatus())) {
                        existing.setStatus(status);
                        changed = true;
                    }
                    if (existing.getPaymentPlan() == null) {
                        existing.setPaymentPlan(PaymentPlan.MONTHLY);
                        changed = true;
                    }
                    if (existing.getBillingDayOfMonth() == null) {
                        existing.setBillingDayOfMonth(startDate.getDayOfMonth());
                        changed = true;
                    }
                    return changed ? contractRepository.save(existing) : existing;
                })
                .orElseGet(() -> {
                    Contract contract = new Contract();
                    contract.setStudent(student);
                    contract.setRoom(room);
                    contract.setStartDate(startDate);
                    contract.setEndDate(endDate);
                    contract.setStatus(status);
                    contract.setPaymentPlan(PaymentPlan.MONTHLY);
                    contract.setBillingDayOfMonth(startDate.getDayOfMonth());
                    return contractRepository.save(contract);
                });
    }

    private Fee ensureFee(Contract contract,
                          FeeType type,
                          BigDecimal amount,
                          LocalDate dueDate,
                          PaymentStatus status) {
        return feeRepository.findByContract_IdAndTypeAndDueDate(contract.getId(), type, dueDate)
                .map(existing -> {
                    boolean changed = false;
                    if (existing.getAmount() == null || existing.getAmount().compareTo(amount) != 0) {
                        existing.setAmount(amount);
                        existing.setTotalAmount(amount);
                        changed = true;
                    }
                    if (existing.getScope() == null) {
                        existing.setScope(FeeScope.INDIVIDUAL);
                        changed = true;
                    }
                    if (existing.getTotalAmount() == null) {
                        existing.setTotalAmount(amount);
                        changed = true;
                    }
                    if (!status.equals(existing.getPaymentStatus())) {
                        existing.setPaymentStatus(status);
                        changed = true;
                    }
                    return changed ? feeRepository.save(existing) : existing;
                })
                .orElseGet(() -> {
                    Fee fee = new Fee();
                    fee.setContract(contract);
                    fee.setType(type);
                    fee.setAmount(amount);
                    fee.setTotalAmount(amount);
                    fee.setScope(FeeScope.INDIVIDUAL);
                    fee.setDueDate(dueDate);
                    fee.setPaymentStatus(status);
                    return feeRepository.save(fee);
                });
    }

    private MaintenanceRequest ensureMaintenanceRequest(Student student,
                                                        Room room,
                                                        String description,
                                                        String requestType,
                                                        String desiredRoom,
                                                        String status) {
        return maintenanceRequestRepository.findByStudent_IdAndDescription(student.getId(), description)
                .map(existing -> {
                    boolean changed = false;
                    if (desiredRoom != null && (existing.getDesiredRoomNumber() == null || !desiredRoom.equals(existing.getDesiredRoomNumber()))) {
                        existing.setDesiredRoomNumber(desiredRoom);
                        changed = true;
                    }
                    if (room != null && (existing.getRoom() == null || !existing.getRoom().getId().equals(room.getId()))) {
                        existing.setRoom(room);
                        changed = true;
                    }
                    if (status != null && !status.equalsIgnoreCase(existing.getStatus())) {
                        existing.setStatus(status);
                        changed = true;
                    }
                    if (requestType != null && !requestType.equalsIgnoreCase(existing.getRequestType())) {
                        existing.setRequestType(requestType);
                        changed = true;
                    }
                    return changed ? maintenanceRequestRepository.save(existing) : existing;
                })
                .orElseGet(() -> {
                    MaintenanceRequest request = new MaintenanceRequest();
                    request.setStudent(student);
                    request.setRoom(room);
                    request.setDescription(description);
                    request.setRequestType(requestType);
                    request.setDesiredRoomNumber(desiredRoom);
                    request.setStatus(status);
                    request.setCreatedAt(LocalDateTime.now());
                    return maintenanceRequestRepository.save(request);
                });
    }

    private DormRegistrationPeriod ensureDormRegistrationPeriod(String name,
                                                                LocalDateTime startTime,
                                                                LocalDateTime endTime,
                                                                Integer capacity,
                                                                String notes,
                                                                DormRegistrationPeriodStatus status) {
        return dormRegistrationPeriodRepository.findAll().stream()
                .filter(period -> period.getName() != null && period.getName().equalsIgnoreCase(name))
                .findFirst()
                .map(existing -> {
                    boolean changed = false;
                    if (startTime != null && (existing.getStartTime() == null || !existing.getStartTime().equals(startTime))) {
                        existing.setStartTime(startTime);
                        changed = true;
                    }
                    if (endTime != null && (existing.getEndTime() == null || !existing.getEndTime().equals(endTime))) {
                        existing.setEndTime(endTime);
                        changed = true;
                    }
                    if (capacity != null && (existing.getCapacity() == null || !existing.getCapacity().equals(capacity))) {
                        existing.setCapacity(capacity);
                        changed = true;
                    }
                    if (notes != null && (existing.getNotes() == null || !existing.getNotes().equals(notes))) {
                        existing.setNotes(notes);
                        changed = true;
                    }
                    if (status != null && existing.getStatus() != status) {
                        existing.setStatus(status);
                        changed = true;
                    }
                    return changed ? dormRegistrationPeriodRepository.save(existing) : existing;
                })
                .orElseGet(() -> {
                    DormRegistrationPeriod period = new DormRegistrationPeriod();
                    period.setName(name);
                    period.setStartTime(startTime);
                    period.setEndTime(endTime);
                    period.setCapacity(capacity);
                    period.setNotes(notes);
                    period.setStatus(status != null ? status : DormRegistrationPeriodStatus.OPEN);
                    return dormRegistrationPeriodRepository.save(period);
                });
    }

    private DormRegistrationRequest ensureDormRegistrationRequest(Student student,
                                                                  DormRegistrationPeriod period,
                                                                  String desiredRoomType,
                                                                  String preferredRoomNumber,
                                                                  LocalDate expectedMoveInDate,
                                                                  String additionalNotes,
                                                                  DormRegistrationStatus status,
                                                                  String adminNotes) {
        if (student == null) {
            return null;
        }
        return dormRegistrationRequestRepository.findByStudentIdOrderByCreatedAtDesc(student.getId()).stream()
                .filter(existing -> desiredRoomType != null && desiredRoomType.equals(existing.getDesiredRoomType())
                        && expectedMoveInDate != null && expectedMoveInDate.equals(existing.getExpectedMoveInDate())
                        && ((existing.getPeriod() == null && period == null)
                        || (existing.getPeriod() != null && period != null && existing.getPeriod().getId().equals(period.getId()))))
                .findFirst()
                .map(existing -> {
                    boolean changed = false;
                    if (preferredRoomNumber != null && (existing.getPreferredRoomNumber() == null || !preferredRoomNumber.equals(existing.getPreferredRoomNumber()))) {
                        existing.setPreferredRoomNumber(preferredRoomNumber);
                        changed = true;
                    }
                    if (additionalNotes != null && (existing.getAdditionalNotes() == null || !additionalNotes.equals(existing.getAdditionalNotes()))) {
                        existing.setAdditionalNotes(additionalNotes);
                        changed = true;
                    }
                    if (status != null && existing.getStatus() != status) {
                        existing.setStatus(status);
                        changed = true;
                    }
                    if (adminNotes != null && (existing.getAdminNotes() == null || !adminNotes.equals(existing.getAdminNotes()))) {
                        existing.setAdminNotes(adminNotes);
                        changed = true;
                    }
                    if (period != null && (existing.getPeriod() == null || !period.getId().equals(existing.getPeriod().getId()))) {
                        existing.setPeriod(period);
                        changed = true;
                    }
                    return changed ? dormRegistrationRequestRepository.save(existing) : existing;
                })
                .orElseGet(() -> {
                    DormRegistrationRequest request = new DormRegistrationRequest();
                    request.setStudent(student);
                    request.setPeriod(period);
                    request.setDesiredRoomType(desiredRoomType);
                    request.setPreferredRoomNumber(preferredRoomNumber);
                    request.setExpectedMoveInDate(expectedMoveInDate);
                    request.setAdditionalNotes(additionalNotes);
                    request.setStatus(status);
                    request.setAdminNotes(adminNotes);
                    return dormRegistrationRequestRepository.save(request);
                });
    }

    private Violation ensureViolation(Student student,
                                      Room room,
                                      String description,
                                      String severity,
                                      LocalDate date) {
        return violationRepository.findByStudent_IdAndDescription(student.getId(), description)
                .map(existing -> {
                    boolean changed = false;
                    if (room != null && (existing.getRoom() == null || !existing.getRoom().getId().equals(room.getId()))) {
                        existing.setRoom(room);
                        changed = true;
                    }
                    if (severity != null && !severity.equalsIgnoreCase(existing.getSeverity())) {
                        existing.setSeverity(severity);
                        changed = true;
                    }
                    if (date != null && !date.equals(existing.getDate())) {
                        existing.setDate(date);
                        changed = true;
                    }
                    return changed ? violationRepository.save(existing) : existing;
                })
                .orElseGet(() -> {
                    Violation violation = new Violation();
                    violation.setStudent(student);
                    violation.setRoom(room);
                    violation.setDescription(description);
                    violation.setSeverity(severity);
                    violation.setDate(date);
                    return violationRepository.save(violation);
                });
    }
}
