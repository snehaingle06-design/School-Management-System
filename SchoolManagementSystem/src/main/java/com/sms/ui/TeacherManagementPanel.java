package com.sms.ui;

import com.sms.model.SchoolClass;
import com.sms.model.Subject;
import com.sms.model.Teacher;
import com.sms.service.ClassService;
import com.sms.service.SubjectService;
import com.sms.service.TeacherService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Teacher Management Panel - provides full CRUD operations for teachers.
 * Includes multi-select subject and class assignment sections.
 */
public class TeacherManagementPanel extends JPanel {

    // Color scheme
    private static final Color PRIMARY = new Color(41, 128, 185);
    private static final Color PRIMARY_DARK = new Color(31, 97, 141);
    private static final Color BACKGROUND = new Color(236, 240, 241);
    private static final Color SUCCESS = new Color(39, 174, 96);
    private static final Color DANGER = new Color(231, 76, 60);
    private static final Color WARNING = new Color(243, 156, 18);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(44, 62, 80);

    private JTable teacherTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField searchField;

    private final TeacherService teacherService;
    private final SubjectService subjectService;
    private final ClassService classService;

    public TeacherManagementPanel() {
        teacherService = new TeacherService();
        subjectService = new SubjectService();
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

        JLabel titleLabel = new JLabel("Teacher Management");
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

        JButton addBtn = createStyledButton("Add Teacher", SUCCESS);
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

        String[] columns = {"ID", "Teacher ID", "First Name", "Last Name", "Qualification", "Experience", "Phone", "Email", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        teacherTable = new JTable(tableModel);
        teacherTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        teacherTable.setRowHeight(30);
        teacherTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        teacherTable.setSelectionBackground(new Color(174, 214, 241));
        teacherTable.setSelectionForeground(TEXT_DARK);
        teacherTable.setGridColor(new Color(230, 230, 230));
        teacherTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        teacherTable.getTableHeader().setBackground(PRIMARY);
        teacherTable.getTableHeader().setForeground(Color.WHITE);
        teacherTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        teacherTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Alternating row colors
        teacherTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        teacherTable.setRowSorter(rowSorter);

        // Double-click to view
        teacherTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewSelectedTeacher();
                }
            }
        });

        // Hide the ID column
        teacherTable.getColumnModel().getColumn(0).setMinWidth(0);
        teacherTable.getColumnModel().getColumn(0).setMaxWidth(0);
        teacherTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane scrollPane = new JScrollPane(teacherTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    // ======================== BOTTOM BAR ========================
    private JPanel createBottomBar() {
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        bottomBar.setBackground(BACKGROUND);

        JButton viewBtn = createStyledButton("View", PRIMARY);
        viewBtn.addActionListener(e -> viewSelectedTeacher());
        bottomBar.add(viewBtn);

        JButton editBtn = createStyledButton("Edit", WARNING);
        editBtn.addActionListener(e -> editSelectedTeacher());
        bottomBar.add(editBtn);

        JButton deleteBtn = createStyledButton("Delete", DANGER);
        deleteBtn.addActionListener(e -> deleteSelectedTeacher());
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
            List<Teacher> teachers = teacherService.getAllTeachers();
            populateTable(teachers);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading teachers: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void populateTable(List<Teacher> teachers) {
        tableModel.setRowCount(0);
        if (teachers != null) {
            for (Teacher t : teachers) {
                tableModel.addRow(new Object[]{
                        t.getId(),
                        t.getTeacherId(),
                        t.getFirstName(),
                        t.getLastName(),
                        t.getQualification(),
                        t.getExperienceYears() + " yrs",
                        t.getPhone(),
                        t.getEmail(),
                        t.getStatus()
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
            List<Teacher> results = teacherService.searchTeachers(keyword);
            populateTable(results);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error searching teachers: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private Teacher getSelectedTeacher() {
        int selectedRow = teacherTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a teacher from the table.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int modelRow = teacherTable.convertRowIndexToModel(selectedRow);
        int teacherId = (int) tableModel.getValueAt(modelRow, 0);
        try {
            return teacherService.getTeacherById(teacherId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching teacher details: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return null;
        }
    }

    private void viewSelectedTeacher() {
        Teacher teacher = getSelectedTeacher();
        if (teacher != null) {
            showViewDialog(teacher);
        }
    }

    private void editSelectedTeacher() {
        Teacher teacher = getSelectedTeacher();
        if (teacher != null) {
            showAddEditDialog(teacher);
        }
    }

    private void deleteSelectedTeacher() {
        int selectedRow = teacherTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a teacher to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = teacherTable.convertRowIndexToModel(selectedRow);
        int teacherId = (int) tableModel.getValueAt(modelRow, 0);
        String teacherName = tableModel.getValueAt(modelRow, 2) + " " + tableModel.getValueAt(modelRow, 3);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete teacher: " + teacherName + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = teacherService.deleteTeacher(teacherId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Teacher deleted successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete teacher.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting teacher: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    // ======================== VIEW DIALOG ========================
    private void showViewDialog(Teacher teacher) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Teacher Details", true);
        dialog.setSize(550, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(CARD_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Teacher Details", SwingConstants.CENTER);
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
        addDetailRow(detailsPanel, gbc, row++, "Teacher ID:", teacher.getTeacherId());
        addDetailRow(detailsPanel, gbc, row++, "First Name:", teacher.getFirstName());
        addDetailRow(detailsPanel, gbc, row++, "Last Name:", teacher.getLastName());
        addDetailRow(detailsPanel, gbc, row++, "Date of Birth:", teacher.getDateOfBirth());
        addDetailRow(detailsPanel, gbc, row++, "Gender:", teacher.getGender());
        addDetailRow(detailsPanel, gbc, row++, "Address:", teacher.getAddress());
        addDetailRow(detailsPanel, gbc, row++, "Phone:", teacher.getPhone());
        addDetailRow(detailsPanel, gbc, row++, "Email:", teacher.getEmail());
        addDetailRow(detailsPanel, gbc, row++, "Qualification:", teacher.getQualification());
        addDetailRow(detailsPanel, gbc, row++, "Experience:", teacher.getExperienceYears() + " years");
        addDetailRow(detailsPanel, gbc, row++, "Hire Date:", teacher.getHireDate());
        addDetailRow(detailsPanel, gbc, row++, "Status:", teacher.getStatus());

        // Assigned Subjects
        StringBuilder subjectNames = new StringBuilder();
        try {
            List<Subject> assignedSubjects = teacherService.getAssignedSubjects(teacher.getId());
            if (assignedSubjects != null && !assignedSubjects.isEmpty()) {
                for (int i = 0; i < assignedSubjects.size(); i++) {
                    if (i > 0) subjectNames.append(", ");
                    subjectNames.append(assignedSubjects.get(i).getSubjectName());
                }
            } else {
                subjectNames.append("None");
            }
        } catch (Exception e) {
            subjectNames.append("N/A");
            e.printStackTrace();
        }
        addDetailRow(detailsPanel, gbc, row++, "Subjects:", subjectNames.toString());

        // Assigned Classes
        StringBuilder classNames = new StringBuilder();
        try {
            List<SchoolClass> assignedClasses = teacherService.getAssignedClasses(teacher.getId());
            if (assignedClasses != null && !assignedClasses.isEmpty()) {
                for (int i = 0; i < assignedClasses.size(); i++) {
                    if (i > 0) classNames.append(", ");
                    SchoolClass sc = assignedClasses.get(i);
                    classNames.append(sc.getClassName());
                    if (sc.getDivision() != null && !sc.getDivision().isEmpty()) {
                        classNames.append(" - ").append(sc.getDivision());
                    }
                }
            } else {
                classNames.append("None");
            }
        } catch (Exception e) {
            classNames.append("N/A");
            e.printStackTrace();
        }
        addDetailRow(detailsPanel, gbc, row++, "Classes:", classNames.toString());

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
    private void showAddEditDialog(Teacher teacher) {
        boolean isEdit = (teacher != null);
        String title = isEdit ? "Edit Teacher" : "Add New Teacher";

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(600, 750);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(CARD_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel(title, SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(PRIMARY);
        mainPanel.add(header, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Teacher ID
        JTextField teacherIdField = new JTextField(20);
        teacherIdField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        if (isEdit) {
            teacherIdField.setText(teacher.getTeacherId());
            teacherIdField.setEnabled(false);
        } else {
            teacherIdField.setText(generateTeacherId());
            teacherIdField.setEnabled(false);
        }
        addFormRow(formPanel, gbc, row++, "Teacher ID:", teacherIdField);

        // First Name
        JTextField firstNameField = createFormTextField(isEdit ? teacher.getFirstName() : "");
        addFormRow(formPanel, gbc, row++, "First Name *:", firstNameField);

        // Last Name
        JTextField lastNameField = createFormTextField(isEdit ? teacher.getLastName() : "");
        addFormRow(formPanel, gbc, row++, "Last Name *:", lastNameField);

        // Date of Birth
        JTextField dobField = createFormTextField(isEdit && teacher.getDateOfBirth() != null ? teacher.getDateOfBirth() : "");
        dobField.setToolTipText("Format: YYYY-MM-DD");
        addFormRow(formPanel, gbc, row++, "Date of Birth:", dobField);

        // Gender
        JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        genderCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        if (isEdit && teacher.getGender() != null) {
            genderCombo.setSelectedItem(teacher.getGender());
        }
        addFormRow(formPanel, gbc, row++, "Gender:", genderCombo);

        // Address
        JTextArea addressArea = new JTextArea(3, 20);
        addressArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        if (isEdit && teacher.getAddress() != null) {
            addressArea.setText(teacher.getAddress());
        }
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addFormRow(formPanel, gbc, row++, "Address:", addressScroll);

        // Phone
        JTextField phoneField = createFormTextField(isEdit ? teacher.getPhone() : "");
        addFormRow(formPanel, gbc, row++, "Phone:", phoneField);

        // Email
        JTextField emailField = createFormTextField(isEdit ? teacher.getEmail() : "");
        addFormRow(formPanel, gbc, row++, "Email:", emailField);

        // Qualification
        JTextField qualificationField = createFormTextField(isEdit ? teacher.getQualification() : "");
        addFormRow(formPanel, gbc, row++, "Qualification *:", qualificationField);

        // Experience Years
        SpinnerNumberModel expModel = new SpinnerNumberModel(
                isEdit ? teacher.getExperienceYears() : 0, 0, 50, 1);
        JSpinner experienceSpinner = new JSpinner(expModel);
        experienceSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addFormRow(formPanel, gbc, row++, "Experience (Years):", experienceSpinner);

        // Hire Date
        JTextField hireDateField = createFormTextField(isEdit && teacher.getHireDate() != null ? teacher.getHireDate() : "");
        hireDateField.setToolTipText("Format: YYYY-MM-DD");
        addFormRow(formPanel, gbc, row++, "Hire Date:", hireDateField);

        // Subject Assignments
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        JLabel subLabel = new JLabel("Subject Assignments:");
        subLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        subLabel.setForeground(PRIMARY);
        formPanel.add(subLabel, gbc);
        row++;

        // Multi-select JList for subjects
        DefaultListModel<Subject> subjectListModel = new DefaultListModel<>();
        JList<Subject> subjectJList = new JList<>(subjectListModel);
        subjectJList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subjectJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        subjectJList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Subject) {
                    Subject s = (Subject) value;
                    setText(s.getSubjectCode() + " - " + s.getSubjectName());
                }
                return this;
            }
        });

        List<Subject> allSubjects = new ArrayList<>();
        try {
            allSubjects = subjectService.getAllSubjects();
            if (allSubjects != null) {
                for (Subject s : allSubjects) {
                    subjectListModel.addElement(s);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Pre-select assigned subjects
        if (isEdit) {
            try {
                List<Subject> assignedSubjects = teacherService.getAssignedSubjects(teacher.getId());
                if (assignedSubjects != null) {
                    List<Integer> selectedIndices = new ArrayList<>();
                    for (int i = 0; i < subjectListModel.getSize(); i++) {
                        Subject s = subjectListModel.getElementAt(i);
                        for (Subject assigned : assignedSubjects) {
                            if (s.getId() == assigned.getId()) {
                                selectedIndices.add(i);
                                break;
                            }
                        }
                    }
                    int[] indices = selectedIndices.stream().mapToInt(Integer::intValue).toArray();
                    subjectJList.setSelectedIndices(indices);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JScrollPane subjectScroll = new JScrollPane(subjectJList);
        subjectScroll.setPreferredSize(new Dimension(300, 80));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(subjectScroll, gbc);
        row++;

        // Class Assignments
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        JLabel classLabel = new JLabel("Class Assignments:");
        classLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        classLabel.setForeground(PRIMARY);
        formPanel.add(classLabel, gbc);
        row++;

        // Multi-select JList for classes
        DefaultListModel<SchoolClass> classListModel = new DefaultListModel<>();
        JList<SchoolClass> classJList = new JList<>(classListModel);
        classJList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        classJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        classJList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SchoolClass) {
                    SchoolClass sc = (SchoolClass) value;
                    String display = sc.getClassName();
                    if (sc.getDivision() != null && !sc.getDivision().isEmpty()) {
                        display += " - " + sc.getDivision();
                    }
                    setText(display);
                }
                return this;
            }
        });

        List<SchoolClass> allClasses = new ArrayList<>();
        try {
            allClasses = classService.getAllClasses();
            if (allClasses != null) {
                for (SchoolClass sc : allClasses) {
                    classListModel.addElement(sc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Pre-select assigned classes
        if (isEdit) {
            try {
                List<SchoolClass> assignedClasses = teacherService.getAssignedClasses(teacher.getId());
                if (assignedClasses != null) {
                    List<Integer> selectedIndices = new ArrayList<>();
                    for (int i = 0; i < classListModel.getSize(); i++) {
                        SchoolClass sc = classListModel.getElementAt(i);
                        for (SchoolClass assigned : assignedClasses) {
                            if (sc.getId() == assigned.getId()) {
                                selectedIndices.add(i);
                                break;
                            }
                        }
                    }
                    int[] indices = selectedIndices.stream().mapToInt(Integer::intValue).toArray();
                    classJList.setSelectedIndices(indices);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        JScrollPane classScroll = new JScrollPane(classJList);
        classScroll.setPreferredSize(new Dimension(300, 80));
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        formPanel.add(classScroll, gbc);
        row++;

        // Reset gridwidth
        gbc.gridwidth = 1;

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
            String qualification = qualificationField.getText().trim();

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
            if (qualification.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Qualification is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                qualificationField.requestFocus();
                return;
            }

            // Build teacher object
            Teacher t = isEdit ? teacher : new Teacher();
            t.setTeacherId(teacherIdField.getText().trim());
            t.setFirstName(firstName);
            t.setLastName(lastName);
            t.setDateOfBirth(dobField.getText().trim());
            t.setGender((String) genderCombo.getSelectedItem());
            t.setAddress(addressArea.getText().trim());
            t.setPhone(phoneField.getText().trim());
            t.setEmail(emailField.getText().trim());
            t.setQualification(qualification);
            t.setExperienceYears((int) experienceSpinner.getValue());
            t.setHireDate(hireDateField.getText().trim());

            // Get selected subject IDs
            List<Integer> selectedSubjectIds = new ArrayList<>();
            for (Subject s : subjectJList.getSelectedValuesList()) {
                selectedSubjectIds.add(s.getId());
            }

            // Get selected class IDs
            List<Integer> selectedClassIds = new ArrayList<>();
            for (SchoolClass sc : classJList.getSelectedValuesList()) {
                selectedClassIds.add(sc.getId());
            }

            try {
                boolean success;
                if (isEdit) {
                    success = teacherService.updateTeacher(t);
                } else {
                    success = teacherService.addTeacher(t);
                }

                if (success) {
                    // Update subject assignments
                    try {
                        teacherService.updateSubjectAssignments(t.getId(), selectedSubjectIds);
                    } catch (Exception ex) {
                        System.err.println("Warning: Could not update subject assignments: " + ex.getMessage());
                    }

                    // Update class assignments
                    try {
                        teacherService.updateClassAssignments(t.getId(), selectedClassIds);
                    } catch (Exception ex) {
                        System.err.println("Warning: Could not update class assignments: " + ex.getMessage());
                    }

                    JOptionPane.showMessageDialog(dialog,
                            isEdit ? "Teacher updated successfully." : "Teacher added successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            isEdit ? "Failed to update teacher." : "Failed to add teacher.",
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
        gbc.gridwidth = 1;
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

    private String generateTeacherId() {
        return "TCH" + System.currentTimeMillis() % 100000;
    }
}
