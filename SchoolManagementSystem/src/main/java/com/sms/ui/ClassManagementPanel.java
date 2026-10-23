package com.sms.ui;

import com.sms.model.SchoolClass;
import com.sms.service.ClassService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Class Management Panel - provides CRUD operations for school classes.
 * Embedded in AdminDashboard's CardLayout content area.
 */
public class ClassManagementPanel extends JPanel {

    // Color scheme
    private static final Color PRIMARY = new Color(41, 128, 185);
    private static final Color PRIMARY_DARK = new Color(31, 97, 141);
    private static final Color BACKGROUND = new Color(236, 240, 241);
    private static final Color SUCCESS = new Color(39, 174, 96);
    private static final Color DANGER = new Color(231, 76, 60);
    private static final Color WARNING = new Color(243, 156, 18);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(44, 62, 80);

    private JTable classTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;

    private final ClassService classService;

    public ClassManagementPanel() {
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

        JLabel titleLabel = new JLabel("Class Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_DARK);
        topBar.add(titleLabel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setBackground(BACKGROUND);

        JButton addBtn = createStyledButton("Add Class", SUCCESS);
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

        String[] columns = {"ID", "Class Name", "Division", "Room Number", "Capacity"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        classTable = new JTable(tableModel);
        classTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        classTable.setRowHeight(30);
        classTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classTable.setSelectionBackground(new Color(174, 214, 241));
        classTable.setSelectionForeground(TEXT_DARK);
        classTable.setGridColor(new Color(230, 230, 230));
        classTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        classTable.getTableHeader().setBackground(PRIMARY);
        classTable.getTableHeader().setForeground(Color.WHITE);
        classTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        classTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Alternating row colors
        classTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
        classTable.setRowSorter(rowSorter);

        // Double-click to edit
        classTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedClass();
                }
            }
        });

        // Hide ID column
        classTable.getColumnModel().getColumn(0).setMinWidth(0);
        classTable.getColumnModel().getColumn(0).setMaxWidth(0);
        classTable.getColumnModel().getColumn(0).setPreferredWidth(0);

        JScrollPane scrollPane = new JScrollPane(classTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    // ======================== BOTTOM BAR ========================
    private JPanel createBottomBar() {
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        bottomBar.setBackground(BACKGROUND);

        JButton editBtn = createStyledButton("Edit", WARNING);
        editBtn.addActionListener(e -> editSelectedClass());
        bottomBar.add(editBtn);

        JButton deleteBtn = createStyledButton("Delete", DANGER);
        deleteBtn.addActionListener(e -> deleteSelectedClass());
        bottomBar.add(deleteBtn);

        JButton refreshBtn = createStyledButton("Refresh", PRIMARY_DARK);
        refreshBtn.addActionListener(e -> loadTableData());
        bottomBar.add(refreshBtn);

        return bottomBar;
    }

    // ======================== DATA OPERATIONS ========================
    private void loadTableData() {
        try {
            List<SchoolClass> classes = classService.getAllClasses();
            populateTable(classes);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading classes: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void populateTable(List<SchoolClass> classes) {
        tableModel.setRowCount(0);
        if (classes != null) {
            for (SchoolClass sc : classes) {
                tableModel.addRow(new Object[]{
                        sc.getId(),
                        sc.getClassName(),
                        sc.getDivision(),
                        sc.getRoomNumber(),
                        sc.getCapacity()
                });
            }
        }
    }

    private void editSelectedClass() {
        int selectedRow = classTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a class to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = classTable.convertRowIndexToModel(selectedRow);
        int classId = (int) tableModel.getValueAt(modelRow, 0);
        try {
            SchoolClass schoolClass = classService.getClassById(classId);
            if (schoolClass != null) {
                showAddEditDialog(schoolClass);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching class details: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void deleteSelectedClass() {
        int selectedRow = classTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a class to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = classTable.convertRowIndexToModel(selectedRow);
        int classId = (int) tableModel.getValueAt(modelRow, 0);
        String className = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete class: " + className + "?\nThis may affect students assigned to this class.",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = classService.deleteClass(classId);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Class deleted successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete class.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting class: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    // ======================== ADD/EDIT DIALOG ========================
    private void showAddEditDialog(SchoolClass schoolClass) {
        boolean isEdit = (schoolClass != null);
        String title = isEdit ? "Edit Class" : "Add New Class";

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

        // Class Name
        JTextField classNameField = createFormTextField(isEdit ? schoolClass.getClassName() : "");
        addFormRow(formPanel, gbc, row++, "Class Name *:", classNameField);

        // Division
        JTextField divisionField = createFormTextField(isEdit ? schoolClass.getDivision() : "");
        addFormRow(formPanel, gbc, row++, "Division:", divisionField);

        // Room Number
        JTextField roomNumberField = createFormTextField(isEdit ? schoolClass.getRoomNumber() : "");
        addFormRow(formPanel, gbc, row++, "Room Number:", roomNumberField);

        // Capacity
        SpinnerNumberModel capacityModel = new SpinnerNumberModel(
                isEdit ? schoolClass.getCapacity() : 40, 1, 200, 1);
        JSpinner capacitySpinner = new JSpinner(capacityModel);
        capacitySpinner.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addFormRow(formPanel, gbc, row++, "Capacity:", capacitySpinner);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.setBackground(CARD_BG);

        JButton saveBtn = createStyledButton("Save", SUCCESS);
        saveBtn.addActionListener(e -> {
            String className = classNameField.getText().trim();
            if (className.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Class Name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                classNameField.requestFocus();
                return;
            }

            SchoolClass sc = isEdit ? schoolClass : new SchoolClass();
            sc.setClassName(className);
            sc.setDivision(divisionField.getText().trim());
            sc.setRoomNumber(roomNumberField.getText().trim());
            sc.setCapacity((int) capacitySpinner.getValue());

            try {
                boolean success;
                if (isEdit) {
                    success = classService.updateClass(sc);
                } else {
                    success = classService.addClass(sc);
                }

                if (success) {
                    JOptionPane.showMessageDialog(dialog,
                            isEdit ? "Class updated successfully." : "Class added successfully.",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadTableData();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            isEdit ? "Failed to update class." : "Failed to add class.",
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
