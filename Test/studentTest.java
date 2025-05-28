package Model;

import java.util.ArrayList;
import java.util.List;

public class StudentManager {
    private List<Student> studentList = new ArrayList<>();

    public void addStudent(Student student) {
        studentList.add(student);
    }

    public Student getStudentById(String studentId) {
        for (Student s : studentList) {
            if (s.getStudentId().equals(studentId)) {
                return s;
            }
        }
        return null;
    }

    public List<Student> getAllStudents() {
        return studentList;
    }
  
    public boolean updateStudent(String studentId, Student updatedStudent) {
        for (int i = 0; i < studentList.size(); i++) {
            if (studentList.get(i).getStudentId().equals(studentId)) {
                studentList.set(i, updatedStudent);
                return true;
            }
        }
        return false;
    }
  
    public boolean deleteStudent(String studentId) {
        return studentList.removeIf(s -> s.getStudentId().equals(studentId));
    }
}

