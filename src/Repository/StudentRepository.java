package Repository;

import Model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByNameContainingIgnoreCase(String name);
    List<Student> findByNameIgnoreCase(String name);
    List<Student> findByRoom_Id(Long roomId);
    long countByRoom_Id(Long roomId);

    @org.springframework.data.jpa.repository.Query("""
            SELECT s FROM Student s
            WHERE lower(s.code) LIKE lower(concat('%', :search, '%'))
               OR lower(s.name) = lower(:search)
               OR lower(s.name) LIKE lower(concat(:search, ' %'))
               OR lower(s.name) LIKE lower(concat('% ', :search))
            """)
    Page<Student> searchByCodeOrNameWord(@org.springframework.data.repository.query.Param("search") String search, Pageable pageable);
}
