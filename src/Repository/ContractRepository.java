package Repository;

import Model.Contract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByStudent_NameContainingIgnoreCaseOrRoom_NumberContainingIgnoreCaseOrStatusContainingIgnoreCase(String studentName, String roomNumber, String status);
}
