package com.example.dorm.service;

import com.example.dorm.model.Contract;
import com.example.dorm.model.Room;
import com.example.dorm.repository.ContractRepository;
import com.example.dorm.repository.StudentRepository;
import com.example.dorm.repository.RoomRepository;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;

    public ContractService(ContractRepository contractRepository,
                           StudentRepository studentRepository,
                           RoomRepository roomRepository) {
        this.contractRepository = contractRepository;
        this.studentRepository = studentRepository;
        this.roomRepository = roomRepository;
    }

    public Page<Contract> getAllContracts(Pageable pageable) {
        return contractRepository.findAll(pageable);
    }

    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

    public Optional<Contract> getContract(Long id) {
        return contractRepository.findById(id);
    }

    public Contract getRequiredContract(Long id) {
        return getContract(id).orElseThrow(() -> new IllegalArgumentException("Contract not found"));
    }

    public Contract createContract(Contract contract) {
        Long roomId = extractRoomId(contract);
        Long studentId = extractStudentId(contract);

        checkRoomCapacity(roomId, studentId);

        Room room = getRequiredRoom(roomId);
        contract.setRoom(room);

        if (studentId != null) {
            if (contractRepository.existsByStudent_Id(studentId)) {
                throw new IllegalStateException("Đã có hợp đồng");
            }
            var student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found"));
            student.setRoom(room);
            studentRepository.save(student);
            contract.setStudent(student);
        }
        return contractRepository.save(contract);
    }

    public Contract updateContract(Long id, Contract contract) {
        Contract existing = getRequiredContract(id);
        Long newStudentId = extractStudentId(contract);
        Long existingStudentId = existing.getStudent() != null ? existing.getStudent().getId() : null;
        Long newRoomId = extractRoomId(contract);

        boolean roomChanged = !existing.getRoom().getId().equals(newRoomId);
        boolean studentChanged = newStudentId != null && !newStudentId.equals(existingStudentId);

        if (roomChanged || studentChanged) {
            checkRoomCapacity(newRoomId, newStudentId);
            if (studentChanged && contractRepository.existsByStudent_Id(newStudentId)) {
                throw new IllegalStateException("Đã có hợp đồng");
            }
            if (existingStudentId != null && (newStudentId == null || !existingStudentId.equals(newStudentId))) {
                studentRepository.findById(existingStudentId).ifPresent(s -> {
                    s.setRoom(null);
                    studentRepository.save(s);
                });
            }
        }

        Room room = getRequiredRoom(newRoomId);

        if (newStudentId != null) {
            var student = studentRepository.findById(newStudentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found"));
            student.setRoom(room);
            studentRepository.save(student);
            existing.setStudent(student);
        } else {
            existing.setStudent(null);
        }

        existing.setRoom(room);
        existing.setStartDate(contract.getStartDate());
        existing.setEndDate(contract.getEndDate());
        existing.setStatus(contract.getStatus());

        return contractRepository.save(existing);
    }

    public void deleteContract(Long id) {
        contractRepository.deleteById(id);
    }

    public List<Contract> getContractsByStudent(Long studentId) {
        return contractRepository.findByStudent_Id(studentId);
    }

    public Page<Contract> searchContracts(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return contractRepository.findAll(pageable);
        }
        return contractRepository
                .searchByStudentWordOrCodeOrRoomOrStatus(search, pageable);
    }

    public Page<Contract> searchContractsAutocomplete(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return contractRepository.findAll(pageable);
        }
        return contractRepository.searchByIdOrStudentWord(search, pageable);
    }

    public Contract findLatestContractByStudentId(Long studentId) {
        return contractRepository.findTopByStudent_IdOrderByEndDateDesc(studentId);
    }

    public long countContracts() {
        return contractRepository.count();
    }

    private Room getRequiredRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
    }

    private void checkRoomCapacity(Long roomId, Long studentId) {
        Room actual = getRequiredRoom(roomId);
        long current = studentRepository.countByRoom_Id(roomId);
        if (studentId != null) {
            studentRepository.findById(studentId)
                    .map(com.example.dorm.model.Student::getRoom)
                    .map(Room::getId)
                    .filter(roomId::equals)
                    .ifPresent(ignored -> current--);
        }
        if (current >= actual.getCapacity()) {
            throw new IllegalStateException("Room capacity exceeded");
        }
    }

    private Long extractStudentId(Contract contract) {
        return contract.getStudent() != null ? contract.getStudent().getId() : null;
    }

    private Long extractRoomId(Contract contract) {
        if (contract.getRoom() == null || contract.getRoom().getId() == null) {
            throw new IllegalArgumentException("Room must be provided");
        }
        return contract.getRoom().getId();
    }
}
