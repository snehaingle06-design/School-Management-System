package com.sms.ui;

import com.sms.model.Subject;
import com.sms.service.SubjectService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Subject Management Panel - provides CRUD operations for subjects.
 * Embedded in AdminDashboard's CardLayout content area.
 */
public class SubjectManagementPanel extends JPanel {

    // Color scheme
    private static final Color PRIMARY = new Color(41, 128, 185);
    private static final Color PRIMARY_DARK = new Color(31, 97, 141);
    private static final Color BACKGROUND = new Color(236, 240, 241);
    private static final Color SUCCESS = new Color(39, 174, 96);
    private static final Color DANGER = new Color(231, 76, 60);
    private static final Color WARNING = new Color(243, 156, 18);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(44, 62, 80);

    private JTable subjectTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    private final SubjectService subjectService;

    public SubjectManagementPanel() {
        subjectService = new SubjectService();
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

        JLabel titleLabel = new JLabel("Subject Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_DARK);
        topBar.add(titleLabel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setBackground(BACKGROUND);

        JButton addBtn = createStyledButton("Add Subject", SUCCESS);
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

        String[] columns = {"ID", "Subject Code", "Subject Name", "Max Marks", "Pass Marks"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        subjectTable = new JTable(tableModel);
        subjectTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subjectTable.setRowHeight(30);
        subjectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        subjectTable.setSelectionBackground(new Color(174, 214, 241));
        subjectTable.setSelectionForeground(TEXT_DARK);
        subjectTable.setGridColor(new Color(230, 230, 230));
        subjectTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        subjectTable.getTableHeader().setBackground(PRIMARY);
        subjectTable.getTableHeader().setForeground(Color.WHITE);
        subjectTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        subjectTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Alternating row colors
        subjectTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        subjectTable.setRowSorter(rowSorter);

        // Double-click to edit
        subjectTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedSubject();
                }
            }
        });

        // Hide ID column
        subjectTable.getColumnModel().getColumn(0).setMinWidth(0);
        subjectTable.getColumnModel().getColumn(0).setMaxWidth(0);
        subjectTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane scrollPane = new JScrollPane(subjectTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    // ======================== BOTTOM BAR ========================
    private JPanel createBottomBar() {
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        bottomBar.setBackground(BACKGROUND);

        JButton editBtn = createStyledButton("Edit", WARNING);
        editBtn.addActionListener(e -> editSelectedSubject());
        bottomBar.add(editBtn);

        JButton deleteBtn = createStyledButton("Delete", DANGER);
        deleteBtn.addActionListener(e -> deleteSelectedSubject());
        bottomBar.add(deleteBtn);

        JButton refreshBtn = createStyledButton("Refresh", PRIMARY_DARK);
        refreshBtn.addActionListener(e -> loadTableData());
        bottomBar.add(refreshBtn);

        return bottomBar;
    }

    // ======================== DATA OPERATIONS ========================
    private void loadTableData() {
        try {
            List<Subject> subjects = subjectService.getAllSubjects();
            populateTable(subjects);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading subjects: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void populateTable(List<Subject> subjects) {
        tableModel.setRowCount(0);
        if (subjects != null) {
            for (Subject s : subjects) {
                tableModel.addRow(new Object[]{
                        s.getId(),
                        s.getSubjectCode(),
                        s.getSubjectName(),
                        s.getMaxMarks(),
                        s.getPassMarks()
                });
            }
        }
    }

    private void editSelectedSubject() {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a subject to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = subjectTable.convertRowIndexToModel(selectedRow);
        int subjectId = (int) tableModel.getValueAt(modelRow, 0);
        try {
            Subject subject = subjectService.getSubjectById(subjectId);
            if (subject != null) {
                showAddEditDialog(subject);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching subject details: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteSelectedSubject() {
        int selectedRow = subjectTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a subject to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = subjectTable.convertRowIndexToModel(selectedRow);
        int subjectId = (int) tableModel.getValueAt(modelRow, 0);
        String subjectName = (String) tableModel.getValueAt(modelRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete subject: " + subjectName + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = subjectService.deleteSubject(subjectId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Subject deleted successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete subject.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting subject: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    // ======================== ADD/EDIT DIALOG ========================
    private void showAddEditDialog(Subject subject) {
        boolean isEdit = (subject != null);
        String title = isEdit ? "Edit Subject" : "Add New Subject";

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), title, true);
        dialog.setSize(450, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(CARD_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel(title, SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 18));
        header.setForeground(PRIMARY);
        mainPanel.add(header, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Subject Code
        JTextField subjectCodeField = createFormTextField(isEdit ? subject.getSubjectCode() : "");
        addFormRow(formPanel, gbc, row++, "Subject Code *:", subjectCodeField);

        // Subject Name
        JTextField subjectNameField = createFormTextField(isEdit ? subject.getSubjectName() : "");
        addFormRow(formPanel, gbc, row++, "Subject Name *:", subjectNameField);

        // Max Marks
        SpinnerNumberModel maxMarksModel = new SpinnerNumberModel(
                isEdit ? subject.getMaxMarks() : 100, 0, 1000, 1);
        JSpinner maxMarksSpinner = new JSpinner(maxMarksModel);
        maxMarksSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addFormRow(formPanel, gbc, row++, "Max Marks:", maxMarksSpinner);

        // Pass Marks
        SpinnerNumberModel passMarksModel = new SpinnerNumberModel(
                isEdit ? subject.getPassMarks() : 35, 0, 1000, 1);
        JSpinner passMarksSpinner = new JSpinner(passMarksModel);
        passMarksSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addFormRow(formPanel, gbc, row++, "Pass Marks:", passMarksSpinner);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.setBackground(CARD_BG);

        JButton saveBtn = createStyledButton("Save", SUCCESS);
        saveBtn.addActionListener(e -> {
            String subjectCode = subjectCodeField.getText().trim();
            String subjectName = subjectNameField.getText().trim();

            if (subjectCode.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Subject Code is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                subjectCodeField.requestFocus();
                return;
            }
            if (subjectName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Subject Name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                subjectNameField.requestFocus();
                return;
            }

            int maxMarks = (int) maxMarksSpinner.getValue();
            int passMarks = (int) passMarksSpinner.getValue();

            if (passMarks > maxMarks) {
                JOptionPane.showMessageDialog(dialog, "Pass Marks cannot exceed Max Marks.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Subject s = isEdit ? subject : new Subject();
            s.setSubjectCode(subjectCode);
            s.setSubjectName(subjectName);
            s.setMaxMarks(maxMarks);
            s.setPassMarks(passMarks);

            try {
                boolean success;
                if (isEdit) {
                    success = subjectService.updateSubject(s);
                } else {
                    success = subjectService.addSubject(s);
                }

                if (success) {
                    JOptionPane.showMessageDialog(dialog,
                            isEdit ? "Subject updated successfully." : "Subject added successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            isEdit ? "Failed to update subject." : "Failed to add subject.",
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
}
