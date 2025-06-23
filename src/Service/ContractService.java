package Service;

import Model.Contract;
import Model.Room;
import Repository.ContractRepository;
import Repository.StudentRepository;
import Repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private RoomRepository roomRepository;

    public Page<Contract> getAllContracts(Pageable pageable) {
        return contractRepository.findAll(pageable);
    }

    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

    public Optional<Contract> getContract(Long id) {
        return contractRepository.findById(id);
    }

    private void checkRoomCapacity(Room room, Long studentId) {
        Room actual = roomRepository.findById(room.getId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        long current = studentRepository.countByRoom_Id(room.getId());
        if (studentId != null) {
            Optional<com.example.dorm.model.Student> existingOpt = studentRepository.findById(studentId);
            if (existingOpt.isPresent()) {
                com.example.dorm.model.Student existing = existingOpt.get();
                if (existing.getRoom() != null && existing.getRoom().getId().equals(room.getId())) {
                    current -= 1;
                }
            }
        }
        if (current >= actual.getCapacity()) {
            throw new IllegalStateException("Room capacity exceeded");
        }
    }

    public Contract createContract(Contract contract) {
        checkRoomCapacity(contract.getRoom(), contract.getStudent() != null ? contract.getStudent().getId() : null);
        if (contract.getStudent() != null && contract.getStudent().getId() != null) {
            var student = studentRepository.findById(contract.getStudent().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Student not found"));
            student.setRoom(contract.getRoom());
            studentRepository.save(student);
            contract.setStudent(student);
        }
        return contractRepository.save(contract);
    }

    public Contract updateContract(Long id, Contract contract) {
        Contract existing = contractRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found"));
        if (!existing.getRoom().getId().equals(contract.getRoom().getId())) {
            checkRoomCapacity(contract.getRoom(), contract.getStudent() != null ? contract.getStudent().getId() : null);
        }
        if (contract.getStudent() != null && contract.getStudent().getId() != null) {
            var student = studentRepository.findById(contract.getStudent().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Student not found"));
            student.setRoom(contract.getRoom());
            studentRepository.save(student);
            existing.setStudent(student);
        } else {
            existing.setStudent(null);
        }
        existing.setRoom(contract.getRoom());
        existing.setStartDate(contract.getStartDate());
        existing.setEndDate(contract.getEndDate());
        existing.setStatus(contract.getStatus());
        return contractRepository.save(existing);
    }

    public void deleteContract(Long id) {
        contractRepository.deleteById(id);
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
}
       