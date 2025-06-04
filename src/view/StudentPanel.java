// Gói và import giữ nguyên
package view;

import Model.Contract;
import Model.Room;
import Model.Student;
import service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StudentPanel extends JPanel {
    private JTable studentTable;
    private JTextField searchField;
    private JButton addStudentButton;
    private JButton editStudentButton;
    private JButton deleteStudentButton;
    private JButton loadStudentButton;
    private JButton searchButton;
    private StudentService studentService;

    public StudentPanel() {
        setLayout(new BorderLayout());
        studentService = new StudentService();

        studentTable = new JTable(new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Name", "DOB", "Gender", "Address", "Phone", "Email", "Room ID", "Contract ID"}
        ));

        searchField = new JTextField(20);
        addStudentButton = new JButton("Add");
        editStudentButton = new JButton("Edit");
        deleteStudentButton = new JButton("Delete");
        loadStudentButton = new JButton("Refresh");
        searchButton = new JButton("Search");

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search by name: "));
        topPanel.add(searchField);
        topPanel.add(searchButton);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(addStudentButton);
        bottomPanel.add(editStudentButton);
        bottomPanel.add(deleteStudentButton);
        bottomPanel.add(loadStudentButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(studentTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        addStudentButton.addActionListener(e -> onAddStudent());
        editStudentButton.addActionListener(e -> onEditStudent());
        deleteStudentButton.addActionListener(e -> onDeleteStudent());
        loadStudentButton.addActionListener(e -> loadStudentTable());
        searchButton.addActionListener(e -> onSearchStudent());

        loadStudentTable();
    }

    public Student showStudentDialog(Student student) {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField dobField = new JTextField();
        JTextField genderField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField roomIdField = new JTextField();
        JTextField contractIdField = new JTextField();

        if (student != null) {
            idField.setText(String.valueOf(student.getStudentId()));
            idField.setEnabled(false);
            nameField.setText(student.getFullName());
            dobField.setText(student.getDateOfBirth());
            genderField.setText(student.getGender());
            addressField.setText(student.getAddress());
            phoneField.setText(student.getPhoneNumber());
            emailField.setText(student.getEmail());
            roomIdField.setText(student.getRoom() != null ? String.valueOf(student.getRoom().getRoomId()) : "");
            contractIdField.setText(student.getContract() != null ? String.valueOf(student.getContract().getcontract_id()) : "");
        }

        JPanel panel = new JPanel(new GridLayout(9, 2));
        panel.add(new JLabel("Student ID:")); panel.add(idField);
        panel.add(new JLabel("Name:")); panel.add(nameField);
        panel.add(new JLabel("Date of Birth:")); panel.add(dobField);
        panel.add(new JLabel("Gender:")); panel.add(genderField);
        panel.add(new JLabel("Address:")); panel.add(addressField);
        panel.add(new JLabel("Phone:")); panel.add(phoneField);
        panel.add(new JLabel("Email:")); panel.add(emailField);
        panel.add(new JLabel("Room ID:")); panel.add(roomIdField);
        panel.add(new JLabel("Contract ID:")); panel.add(contractIdField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Student Information", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Student newStudent = new Student();
                newStudent.setStudentId(Integer.parseInt(idField.getText()));
                newStudent.setFullName(nameField.getText());
                newStudent.setDateOfBirth(dobField.getText());
                newStudent.setGender(genderField.getText());
                newStudent.setAddress(addressField.getText());
                newStudent.setPhoneNumber(phoneField.getText());
                newStudent.setEmail(emailField.getText());
                newStudent.setRoom(new Room(Integer.parseInt(roomIdField.getText())));
                newStudent.setContract(new Contract(Integer.parseInt(contractIdField.getText())));
                return newStudent;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Thông tin không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

    private void loadStudentTable() {
        List<Student> students = studentService.getAllStudents();
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        model.setRowCount(0);

        for (Student student : students) {
            model.addRow(new Object[]{
                    student.getStudentId(),
                    student.getFullName(),
                    student.getDateOfBirth(),
                    student.getGender(),
                    student.getAddress(),
                    student.getPhoneNumber(),
                    student.getEmail(),
                    student.getRoom() != null ? student.getRoom().getRoomId() : "N/A",
                    student.getContract() != null ? student.getContract().getcontract_id() : "N/A"
            });
        }
    }

    private void onAddStudent() {
        Student student = showStudentDialog(null);
        if (student != null) {
            boolean isAdded = studentService.addStudentWithContract(student);
            if (isAdded) {
                JOptionPane.showMessageDialog(this, "Thêm sinh viên và hợp đồng thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadStudentTable();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm sinh viên và hợp đồng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEditStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        int studentId = (int) model.getValueAt(selectedRow, 0);
        Student student = studentService.getStudentById(studentId);

        Student updatedStudent = showStudentDialog(student);
        if (updatedStudent != null) {
            boolean isUpdated = studentService.updateStudent(updatedStudent);
            if (isUpdated) {
                JOptionPane.showMessageDialog(this, "Cập nhật sinh viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadStudentTable();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật sinh viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onDeleteStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sinh viên cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        int studentId = (int) model.getValueAt(selectedRow, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa sinh viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean isDeleted = studentService.deleteStudent(studentId);
            if (isDeleted) {
                JOptionPane.showMessageDialog(this, "Xóa sinh viên thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                loadStudentTable();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa sinh viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onSearchStudent() {
        String keyword = searchField.getText();
        List<Student> students = studentService.searchStudentsByName(keyword);

        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        model.setRowCount(0);

        for (Student student : students) {
            model.addRow(new Object[]{
                    student.getStudentId(),
                    student.getFullName(),
                    student.getDateOfBirth(),
                    student.getGender(),
                    student.getAddress(),
                    student.getPhoneNumber(),
                    student.getEmail(),
                    student.getRoom() != null ? student.getRoom().getRoomId() : "N/A",
                    student.getContract() != null ? student.getContract().getcontract_id() : "N/A"
            });
        }
    }
}
