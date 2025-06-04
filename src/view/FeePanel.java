package view;

import Model.Fee;
import Model.FeeType;
import Model.Student;
import service.FeeService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FeePanel extends JPanel {
    private JTable feeTable;
    private JScrollPane scrollPane;
    private JButton addFeeButton, editFeeButton, deleteFeeButton, searchFeeButton, refreshButton;
    private FeeService feeService;
    private DefaultTableModel tableModel;
    private JTextField feeIdTextField;
    private JTextField feeTypeTextField;
    private JTextField studentIdTextField;

    public FeePanel() {
        setLayout(new BorderLayout());
        feeService = new FeeService();

        tableModel = new DefaultTableModel(new Object[]{"ID", "Type", "Amount", "Payment Date", "Student ID", "Status"}, 0);
        feeTable = new JTable(tableModel);
        scrollPane = new JScrollPane(feeTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addFeeButton = new JButton("Add");
        editFeeButton = new JButton("Update");
        deleteFeeButton = new JButton("Delete");
        searchFeeButton = new JButton("Search");
        refreshButton = new JButton("Refresh");

        feeIdTextField = new JTextField(10);
        feeTypeTextField = new JTextField(10);
        studentIdTextField = new JTextField(10);

        buttonPanel.add(addFeeButton);
        buttonPanel.add(editFeeButton);
        buttonPanel.add(deleteFeeButton);
        buttonPanel.add(refreshButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());
        searchPanel.add(new JLabel("Fee ID:"));
        searchPanel.add(feeIdTextField);
        searchPanel.add(new JLabel("Fee Type:"));
        searchPanel.add(feeTypeTextField);
        searchPanel.add(new JLabel("Student ID:"));
        searchPanel.add(studentIdTextField);
        searchPanel.add(searchFeeButton);

        add(searchPanel, BorderLayout.NORTH);

        addFeeButton.addActionListener(e -> addFee());
        editFeeButton.addActionListener(e -> onUpdateFee());
        deleteFeeButton.addActionListener(e -> onDeleteFee());
        searchFeeButton.addActionListener(e -> onSearchFee());
        refreshButton.addActionListener(e -> loadFeeData());

        loadFeeData();
    }

    private Fee showFeeDialog(Fee fee) {
        JComboBox<FeeType> feeTypeComboBox = new JComboBox<>(FeeType.values());
        if (fee != null && fee.getFeeType() != null) {
            feeTypeComboBox.setSelectedItem(fee.getFeeType());
        }

        JTextField feeAmountField = new JTextField(fee != null ? String.valueOf(fee.getFeeAmount()) : "", 20);
        JTextField paymentDateField = new JTextField(fee != null ? fee.getPaymentDate() : "", 20);
        JTextField studentIdField = new JTextField(fee != null && fee.getStudent() != null ? String.valueOf(fee.getStudent().getStudentId()) : "", 20);
        JCheckBox isPaidCheckBox = new JCheckBox("Is Paid", fee != null && "Đã thanh toán".equals(fee.getStatus()));

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Fee Type:"));
        panel.add(feeTypeComboBox);
        panel.add(new JLabel("Fee Amount:"));
        panel.add(feeAmountField);
        panel.add(new JLabel("Payment Date:"));
        panel.add(paymentDateField);
        panel.add(new JLabel("Student ID:"));
        panel.add(studentIdField);
        panel.add(new JLabel("Is Paid:"));
        panel.add(isPaidCheckBox);

        int result = JOptionPane.showConfirmDialog(null, panel, "Fee Information", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                Fee newFee = new Fee();
                if (fee != null) {
                    newFee.setFeeId(fee.getFeeId());
                }
                newFee.setFeeType((FeeType) feeTypeComboBox.getSelectedItem());
                newFee.setFeeAmount(Double.parseDouble(feeAmountField.getText()));
                newFee.setPaymentDate(paymentDateField.getText());

                Student student = new Student();
                student.setStudentId(Integer.parseInt(studentIdField.getText()));
                newFee.setStudent(student);

                newFee.setStatus(isPaidCheckBox.isSelected() ? "Đã thanh toán" : "Chưa thanh toán");
                return newFee;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid input! Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return null;
    }

    private void loadFeeData() {
        tableModel.setRowCount(0);
        List<Fee> feeList = feeService.getAllFees();
        for (Fee fee : feeList) {
            tableModel.addRow(new Object[]{
                    fee.getFeeId(),
                    fee.getFeeType(),
                    fee.getFeeAmount(),
                    fee.getPaymentDate(),
                    fee.getStudent().getStudentId(),
                    "Đã thanh toán".equals(fee.getStatus()) ? "Yes" : "No"
            });
        }
    }

    private void addFee() {
        Fee fee = showFeeDialog(null);
        if (fee != null) {
            boolean isAdded = feeService.addFee(fee);
            if (isAdded) {
                JOptionPane.showMessageDialog(this, "Fee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadFeeData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add fee.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void onUpdateFee() {
        int selectedRow = feeTable.getSelectedRow();
        if (selectedRow != -1) {
            int feeId = (int) feeTable.getValueAt(selectedRow, 0);
            Fee feeToEdit = new Fee(feeId);
            Fee editedFee = showFeeDialog(feeToEdit);
            if (editedFee != null) {
                boolean isUpdated = feeService.updateFee(editedFee);
                if (isUpdated) {
                    JOptionPane.showMessageDialog(this, "Fee updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadFeeData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update fee.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a fee to edit!", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void onDeleteFee() {
        int selectedRow = feeTable.getSelectedRow();
        if (selectedRow != -1) {
            int feeId = (int) feeTable.getValueAt(selectedRow, 0);
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this fee?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean isDeleted = feeService.deleteFee(feeId);
                if (isDeleted) {
                    JOptionPane.showMessageDialog(this, "Fee deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadFeeData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete fee.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a fee to delete!", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void onSearchFee() {
        String feeTypeInput = feeTypeTextField.getText().trim();
        String feeIdInput = feeIdTextField.getText().trim();
        String studentIdInput = studentIdTextField.getText().trim();

        List<Fee> searchResults = new ArrayList<>();

        if (!feeIdInput.isEmpty() || !feeTypeInput.isEmpty() || !studentIdInput.isEmpty()) {
            searchResults = feeService.searchFees(feeIdInput, feeTypeInput, studentIdInput);
            if (searchResults.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No matching fees found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please enter at least one search parameter.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        updateFeeTable(searchResults);
    }

    private void updateFeeTable(List<Fee> fees) {
        DefaultTableModel model = (DefaultTableModel) feeTable.getModel();
        model.setRowCount(0);

        for (Fee fee : fees) {
            model.addRow(new Object[]{
                    fee.getFeeId(),
                    fee.getFeeType(),
                    fee.getFeeAmount(),
                    fee.getPaymentDate(),
                    fee.getStudent().getStudentId(),
                    fee.getStatus()
            });
        }
    }

    public JButton getAddFeeButton() {
        return addFeeButton;
    }

    public JButton getEditFeeButton() {
        return editFeeButton;
    }

    public JButton getDeleteFeeButton() {
        return deleteFeeButton;
    }

    public JTable getFeeTable() {
        return feeTable;
    }
}
