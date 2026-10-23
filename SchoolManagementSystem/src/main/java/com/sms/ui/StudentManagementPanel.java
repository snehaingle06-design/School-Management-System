package com.sms.ui;

import com.sms.model.SchoolClass;
import com.sms.model.Student;
import com.sms.service.ClassService;
import com.sms.service.StudentService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Student Management Panel - provides full CRUD operations for students.
 * Embedded in AdminDashboard's CardLayout content area.
 */
public class StudentManagementPanel extends JPanel {

    // Color scheme
    private static final Color PRIMARY = new Color(41, 128, 185);
    private static final Color PRIMARY_DARK = new Color(31, 97, 141);
    private static final Color BACKGROUND = new Color(236, 240, 241);
    private static final Color SUCCESS = new Color(39, 174, 96);
    private static final Color DANGER = new Color(231, 76, 60);
    private static final Color WARNING = new Color(243, 156, 18);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(44, 62, 80);

    private JTable studentTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField searchField;

    private final StudentService studentService;
    private final ClassService classService;

    public StudentManagementPanel() {
        studentService = new StudentService();
        classService = new ClassService();
        initComponents();
        loadTableData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 10));
        setBackground(BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(createTopBar(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createBottomBar(), BorderLayout.SOUTH);
    }

    // ======================== TOP BAR ========================
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setBackground(BACKGROUND);

        JLabel titleLabel = new JLabel("Student Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_DARK);
        topBar.add(titleLabel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setBackground(BACKGROUND);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(220, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSearch();
                }
            }
        });
        rightPanel.add(searchField);

        JButton searchBtn = createStyledButton("Search", PRIMARY);
        searchBtn.addActionListener(e -> performSearch());
        rightPanel.add(searchBtn);

        JButton addBtn = createStyledButton("Add Student", SUCCESS);
        addBtn.addActionListener(e -> showAddEditDialog(null));
        rightPanel.add(addBtn);

        topBar.add(rightPanel, BorderLayout.EAST);
        return topBar;
    }

    // ======================== TABLE ========================
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_BG);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        String[] columns = {"ID", "Student ID", "First Name", "Last Name", "Gender", "Class", "Phone", "Guardian", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentTable.setRowHeight(30);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setSelectionBackground(new Color(174, 214, 241));
        studentTable.setSelectionForeground(TEXT_DARK);
        studentTable.setGridColor(new Color(230, 230, 230));
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        studentTable.getTableHeader().setBackground(PRIMARY);
        studentTable.getTableHeader().setForeground(Color.WHITE);
        studentTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        studentTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Alternating row colors
        studentTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 248, 250));
                }
                return c;
            }
        });

        // Row sorter
        rowSorter = new TableRowSorter<>(tableModel);
        studentTable.setRowSorter(rowSorter);

        // Double-click to view
        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewSelectedStudent();
                }
            }
        });

        // Hide the ID column visually but keep it in the model
        studentTable.getColumnModel().getColumn(0).setMinWidth(0);
        studentTable.getColumnModel().getColumn(0).setMaxWidth(0);
        studentTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    // ======================== BOTTOM BAR ========================
    private JPanel createBottomBar() {
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        bottomBar.setBackground(BACKGROUND);

        JButton viewBtn = createStyledButton("View", PRIMARY);
        viewBtn.addActionListener(e -> viewSelectedStudent());
        bottomBar.add(viewBtn);

        JButton editBtn = createStyledButton("Edit", WARNING);
        editBtn.addActionListener(e -> editSelectedStudent());
        bottomBar.add(editBtn);

        JButton deleteBtn = createStyledButton("Delete", DANGER);
        deleteBtn.addActionListener(e -> deleteSelectedStudent());
        bottomBar.add(deleteBtn);

        JButton refreshBtn = createStyledButton("Refresh", PRIMARY_DARK);
        refreshBtn.addActionListener(e -> loadTableData());
        bottomBar.add(refreshBtn);

        JButton clearBtn = createStyledButton("Clear", new Color(149, 165, 166));
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            loadTableData();
        });
        bottomBar.add(clearBtn);

        return bottomBar;
    }

    // ======================== DATA OPERATIONS ========================
    private void loadTableData() {
        try {
            List<Student> students = studentService.getAllStudents();
            populateTable(students);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading students: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void populateTable(List<Student> students) {
        tableModel.setRowCount(0);
        if (students != null) {
            for (Student s : students) {
                tableModel.addRow(new Object[]{
                        s.getId(),
                        s.getStudentId(),
                        s.getFirstName(),
                        s.getLastName(),
                        s.getGender(),
                        s.getClassName() != null ? s.getClassName() : "",
                        s.getPhone(),
                        s.getGuardianName(),
                        s.getStatus()
                });
            }
        }
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadTableData();
            return;
        }
        try {
            List<Student> results = studentService.searchStudents(keyword);
            populateTable(results);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error searching students: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private Student getSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int modelRow = studentTable.convertRowIndexToModel(selectedRow);
        int studentId = (int) tableModel.getValueAt(modelRow, 0);
        try {
            return studentService.getStudentById(studentId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching student details: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }

    private void viewSelectedStudent() {
        Student student = getSelectedStudent();
        if (student != null) {
            showViewDialog(student);
        }
    }

    private void editSelectedStudent() {
        Student student = getSelectedStudent();
        if (student != null) {
            showAddEditDialog(student);
        }
    }

    private void deleteSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = studentTable.convertRowIndexToModel(selectedRow);
        int studentId = (int) tableModel.getValueAt(modelRow, 0);
        String studentName = tableModel.getValueAt(modelRow, 2) + " " + tableModel.getValueAt(modelRow, 3);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete student: " + studentName + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = studentService.deleteStudent(studentId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Student deleted successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete student.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting student: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    // ======================== VIEW DIALOG ========================
    private void showViewDialog(Student student) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Student Details", true);
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(CARD_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Student Details", SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(PRIMARY);
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addDetailRow(detailsPanel, gbc, row++, "Student ID:", student.getStudentId());
        addDetailRow(detailsPanel, gbc, row++, "First Name:", student.getFirstName());
        addDetailRow(detailsPanel, gbc, row++, "Last Name:", student.getLastName());
        addDetailRow(detailsPanel, gbc, row++, "Date of Birth:", student.getDateOfBirth());
        addDetailRow(detailsPanel, gbc, row++, "Gender:", student.getGender());
        addDetailRow(detailsPanel, gbc, row++, "Address:", student.getAddress());
        addDetailRow(detailsPanel, gbc, row++, "Phone:", student.getPhone());
        addDetailRow(detailsPanel, gbc, row++, "Email:", student.getEmail());
        addDetailRow(detailsPanel, gbc, row++, "Guardian Name:", student.getGuardianName());
        addDetailRow(detailsPanel, gbc, row++, "Guardian Phone:", student.getGuardianPhone());
        addDetailRow(detailsPanel, gbc, row++, "Guardian Relation:", student.getGuardianRelation());
        addDetailRow(detailsPanel, gbc, row++, "Class:", student.getClassName() != null ? student.getClassName() : "N/A");
        addDetailRow(detailsPanel, gbc, row++, "Enrollment Date:", student.getEnrollmentDate());
        addDetailRow(detailsPanel, gbc, row++, "Status:", student.getStatus());

        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton closeBtn = createStyledButton("Close", PRIMARY);
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setBackground(CARD_BG);
        btnPanel.add(closeBtn);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_DARK);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JLabel val = new JLabel(value != null ? value : "N/A");
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        val.setForeground(new Color(100, 100, 100));
        panel.add(val, gbc);
    }

    // ======================== ADD/EDIT DIALOG ========================
    private void showAddEditDialog(Student student) {
        boolean isEdit = (student != null);
        String title = isEdit ? "Edit Student" : "Add New Student";

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(550, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(CARD_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel(title, SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(PRIMARY);
        mainPanel.add(header, BorderLayout.NORTH);

        // Form fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Student ID
        JTextField studentIdField = new JTextField(20);
        studentIdField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        if (isEdit) {
            studentIdField.setText(student.getStudentId());
            studentIdField.setEnabled(false);
        } else {
            studentIdField.setText(generateStudentId());
            studentIdField.setEnabled(false);
        }
        addFormRow(formPanel, gbc, row++, "Student ID:", studentIdField);

        // First Name
        JTextField firstNameField = createFormTextField(isEdit ? student.getFirstName() : "");
        addFormRow(formPanel, gbc, row++, "First Name *:", firstNameField);

        // Last Name
        JTextField lastNameField = createFormTextField(isEdit ? student.getLastName() : "");
        addFormRow(formPanel, gbc, row++, "Last Name *:", lastNameField);

        // Date of Birth
        JTextField dobField = createFormTextField(isEdit && student.getDateOfBirth() != null ? student.getDateOfBirth() : "");
        dobField.setToolTipText("Format: YYYY-MM-DD");
        addFormRow(formPanel, gbc, row++, "Date of Birth:", dobField);

        // Gender
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        if (isEdit && student.getGender() != null) {
            genderCombo.setSelectedItem(student.getGender());
        }
        addFormRow(formPanel, gbc, row++, "Gender:", genderCombo);

        // Address
        JTextArea addressArea = new JTextArea(3, 20);
        addressArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        if (isEdit && student.getAddress() != null) {
            addressArea.setText(student.getAddress());
        }
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addFormRow(formPanel, gbc, row++, "Address:", addressScroll);

        // Phone
        JTextField phoneField = createFormTextField(isEdit ? student.getPhone() : "");
        addFormRow(formPanel, gbc, row++, "Phone:", phoneField);

        // Email
        JTextField emailField = createFormTextField(isEdit ? student.getEmail() : "");
        addFormRow(formPanel, gbc, row++, "Email:", emailField);

        // Guardian Name
        JTextField guardianNameField = createFormTextField(isEdit ? student.getGuardianName() : "");
        addFormRow(formPanel, gbc, row++, "Guardian Name *:", guardianNameField);

        // Guardian Phone
        JTextField guardianPhoneField = createFormTextField(isEdit ? student.getGuardianPhone() : "");
        addFormRow(formPanel, gbc, row++, "Guardian Phone:", guardianPhoneField);

        // Guardian Relation
        JComboBox<String> guardianRelationCombo = new JComboBox<>(new String[]{"Father", "Mother", "Guardian", "Other"});
        guardianRelationCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        if (isEdit && student.getGuardianRelation() != null) {
            guardianRelationCombo.setSelectedItem(student.getGuardianRelation());
        }
        addFormRow(formPanel, gbc, row++, "Guardian Relation:", guardianRelationCombo);

        // Class
        JComboBox<SchoolClass> classCombo = new JComboBox<>();
        classCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        classCombo.addItem(null); // Allow no selection
        try {
            List<SchoolClass> classes = classService.getAllClasses();
            if (classes != null) {
                for (SchoolClass sc : classes) {
                    classCombo.addItem(sc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        classCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("-- Select Class --");
                } else {
                    SchoolClass sc = (SchoolClass) value;
                    setText(sc.getClassName() + (sc.getDivision() != null && !sc.getDivision().isEmpty() ? " - " + sc.getDivision() : ""));
                }
                return this;
            }
        });
        if (isEdit && student.getClassId() > 0) {
            for (int i = 0; i < classCombo.getItemCount(); i++) {
                SchoolClass item = classCombo.getItemAt(i);
                if (item != null && item.getId() == student.getClassId()) {
                    classCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        addFormRow(formPanel, gbc, row++, "Class:", classCombo);

        // Enrollment Date
        JTextField enrollmentDateField = createFormTextField(isEdit && student.getEnrollmentDate() != null ? student.getEnrollmentDate() : "");
        enrollmentDateField.setToolTipText("Format: YYYY-MM-DD");
        addFormRow(formPanel, gbc, row++, "Enrollment Date:", enrollmentDateField);

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(formScroll, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.setBackground(CARD_BG);

        JButton saveBtn = createStyledButton("Save", SUCCESS);
        saveBtn.addActionListener(e -> {
            // Validate required fields
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String guardianName = guardianNameField.getText().trim();

            if (firstName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "First Name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                firstNameField.requestFocus();
                return;
            }
            if (lastName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Last Name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                lastNameField.requestFocus();
                return;
            }
            if (guardianName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Guardian Name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                guardianNameField.requestFocus();
                return;
            }

            // Build student object
            Student s = isEdit ? student : new Student();
            s.setStudentId(studentIdField.getText().trim());
            s.setFirstName(firstName);
            s.setLastName(lastName);
            s.setDateOfBirth(dobField.getText().trim());
            s.setGender((String) genderCombo.getSelectedItem());
            s.setAddress(addressArea.getText().trim());
            s.setPhone(phoneField.getText().trim());
            s.setEmail(emailField.getText().trim());
            s.setGuardianName(guardianName);
            s.setGuardianPhone(guardianPhoneField.getText().trim());
            s.setGuardianRelation((String) guardianRelationCombo.getSelectedItem());
            s.setEnrollmentDate(enrollmentDateField.getText().trim());

            SchoolClass selectedClass = (SchoolClass) classCombo.getSelectedItem();
            if (selectedClass != null) {
                s.setClassId(selectedClass.getId());
            }

            try {
                boolean success;
                if (isEdit) {
                    success = studentService.updateStudent(s);
                } else {
                    success = studentService.addStudent(s);
                }

                if (success) {
                    JOptionPane.showMessageDialog(dialog,
                            isEdit ? "Student updated successfully." : "Student added successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            isEdit ? "Failed to update student." : "Failed to add student.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        btnPanel.add(saveBtn);

        JButton cancelBtn = createStyledButton("Cancel", DANGER);
        cancelBtn.addActionListener(e -> dialog.dispose());
        btnPanel.add(cancelBtn);

        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        dialog.setContentPane(mainPanel);
        dialog.setVisible(true);
    }

    // ======================== HELPER METHODS ========================
    private JTextField createFormTextField(String text) {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        if (text != null) {
            field.setText(text);
        }
        return field;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_DARK);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(field, gbc);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(110, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private String generateStudentId() {
        return "STU" + System.currentTimeMillis() % 100000;
    }
}
