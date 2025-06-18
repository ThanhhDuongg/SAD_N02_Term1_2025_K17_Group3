package Repository;

import com.example.dorm.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByNumberContainingIgnoreCaseOrTypeContainingIgnoreCase(String number, String type);
}