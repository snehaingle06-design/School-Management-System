package com.sms.ui;

import com.sms.model.User;
import com.sms.service.StudentService;
import com.sms.service.TeacherService;
import com.sms.service.ClassService;
import com.sms.service.FeeService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Admin Dashboard with sidebar navigation and CardLayout content area.
 * Provides overview statistics and access to all management modules.
 */
public class AdminDashboard extends JFrame {

    // Color scheme
    private static final Color PRIMARY = new Color(41, 128, 185);
    private static final Color PRIMARY_DARK = new Color(31, 97, 141);
    private static final Color SIDEBAR_BG = new Color(44, 62, 80);
    private static final Color SIDEBAR_HOVER = new Color(52, 73, 94);
    private static final Color SIDEBAR_TEXT = Color.WHITE;
    private static final Color BACKGROUND = new Color(236, 240, 241);
    private static final Color SUCCESS = new Color(39, 174, 96);
    private static final Color DANGER = new Color(231, 76, 60);
    private static final Color WARNING = new Color(243, 156, 18);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_DARK = new Color(44, 62, 80);

    private static final int SIDEBAR_WIDTH = 250;

    private final User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel sidebarPanel;
    private JButton selectedButton;
    private final Map<String, JPanel> loadedPanels = new HashMap<>();
    private final Map<String, JButton> navButtons = new HashMap<>();

    // Navigation items: key -> [icon, label]
    private static final String[][] NAV_ITEMS = {
            {"DASHBOARD",    "\uD83D\uDCCA", "Dashboard"},
            {"STUDENTS",     "\uD83D\uDC68\u200D\uD83C\uDF93", "Students"},
            {"TEACHERS",     "\uD83D\uDC68\u200D\uD83C\uDFEB", "Teachers"},
            {"CLASSES",      "\uD83D\uDCDA", "Classes"},
            {"SUBJECTS",     "\uD83D\uDCD6", "Subjects"},
            {"ATTENDANCE",   "\uD83D\uDCCB", "Attendance"},
            {"EXAMINATIONS", "\uD83D\uDCDD", "Examinations"},
            {"FEES",         "\uD83D\uDCB0", "Fees"},
            {"USERS",        "\uD83D\uDC65", "Users"},
            {"LOGOUT",       "\uD83D\uDEAA", "Logout"}
    };

    public AdminDashboard(User user) {
        this.currentUser = user;
        initializeFrame();
        buildUI();
    }

    private void initializeFrame() {
        setTitle("School Management System - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 700));
        getContentPane().setBackground(BACKGROUND);
    }

    private void buildUI() {
        setLayout(new BorderLayout());

        // Sidebar
        sidebarPanel = buildSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        // Content area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BACKGROUND);

        // Dashboard overview is always loaded
        JPanel dashboardOverview = buildDashboardOverview();
        contentPanel.add(dashboardOverview, "DASHBOARD");
        loadedPanels.put("DASHBOARD", dashboardOverview);

        add(contentPanel, BorderLayout.CENTER);

        // Select dashboard by default
        selectNavButton(navButtons.get("DASHBOARD"));
    }

    // ==================== SIDEBAR ====================

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 0));
        sidebar.setBackground(SIDEBAR_BG);

        // Top branding
        JPanel brandPanel = new JPanel(new BorderLayout());
        brandPanel.setBackground(new Color(34, 49, 63));
        brandPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel brandIcon = new JLabel("\uD83C\uDFEB", SwingConstants.LEFT);
        brandIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        brandPanel.add(brandIcon, BorderLayout.WEST);

        JPanel brandText = new JPanel(new GridLayout(2, 1));
        brandText.setOpaque(false);
        brandText.setBorder(new EmptyBorder(0, 12, 0, 0));

        JLabel brandTitle = new JLabel("SMS Admin");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 17));
        brandTitle.setForeground(SIDEBAR_TEXT);
        brandText.add(brandTitle);

        JLabel brandSubtitle = new JLabel(currentUser.getUsername());
        brandSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        brandSubtitle.setForeground(new Color(149, 165, 166));
        brandText.add(brandSubtitle);

        brandPanel.add(brandText, BorderLayout.CENTER);
        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Navigation buttons
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(SIDEBAR_BG);
        navPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        for (String[] item : NAV_ITEMS) {
            String key = item[0];
            String icon = item[1];
            String label = item[2];

            if (key.equals("LOGOUT")) {
                navPanel.add(Box.createVerticalGlue());
                navPanel.add(createSeparator());
            }

            JButton navBtn = createNavButton(icon, label, key);
            navButtons.put(key, navBtn);
            navPanel.add(navBtn);
        }

        JScrollPane scrollPane = new JScrollPane(navPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(SIDEBAR_BG);
        scrollPane.getViewport().setBackground(SIDEBAR_BG);
        sidebar.add(scrollPane, BorderLayout.CENTER);

        return sidebar;
    }

    private JPanel createSeparator() {
        JPanel sep = new JPanel();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 1));
        sep.setBackground(new Color(52, 73, 94));
        return sep;
    }

    private JButton createNavButton(String icon, String label, String actionKey) {
        JButton button = new JButton(icon + "   " + label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (this == selectedButton) {
                    g2.setColor(PRIMARY);
                } else if (getModel().isRollover()) {
                    g2.setColor(SIDEBAR_HOVER);
                } else {
                    g2.setColor(SIDEBAR_BG);
                }
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Left accent bar on selected
                if (this == selectedButton) {
                    g2.setColor(Color.WHITE);
                    g2.fillRect(0, 0, 4, getHeight());
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        button.setForeground(SIDEBAR_TEXT);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        button.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 48));
        button.setBorder(new EmptyBorder(0, 20, 0, 10));

        button.addActionListener(e -> handleNavClick(actionKey, button));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { button.repaint(); }
            @Override
            public void mouseExited(MouseEvent e) { button.repaint(); }
        });

        return button;
    }

    private void selectNavButton(JButton button) {
        selectedButton = button;
        sidebarPanel.repaint();
    }

    private void handleNavClick(String key, JButton button) {
        if (key.equals("LOGOUT")) {
            int result = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Confirm Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);
                });
            }
            return;
        }

        selectNavButton(button);

        // Lazy load content panels
        if (!loadedPanels.containsKey(key)) {
            JPanel panel = createContentPanel(key);
            contentPanel.add(panel, key);
            loadedPanels.put(key, panel);
        }

        cardLayout.show(contentPanel, key);
    }

    private JPanel createContentPanel(String key) {
        switch (key) {
            case "STUDENTS":
                return createModulePanel("com.sms.ui.StudentManagementPanel", "Student Management");
            case "TEACHERS":
                return createModulePanel("com.sms.ui.TeacherManagementPanel", "Teacher Management");
            case "CLASSES":
                return createModulePanel("com.sms.ui.ClassManagementPanel", "Class Management");
            case "SUBJECTS":
                return createModulePanel("com.sms.ui.SubjectManagementPanel", "Subject Management");
            case "ATTENDANCE":
                return createModulePanel("com.sms.ui.AttendanceManagementPanel", "Attendance Management");
            case "EXAMINATIONS":
                return createModulePanel("com.sms.ui.ExaminationManagementPanel", "Examination Management");
            case "FEES":
                return createModulePanel("com.sms.ui.FeeManagementPanel", "Fee Management");
            case "USERS":
                return new UserManagementPanel();
            default:
                return buildPlaceholderPanel(key);
        }
    }

    /**
     * Attempts to load a management panel class by name.
     * Falls back to a placeholder panel if the class is not found.
     */
    private JPanel createModulePanel(String className, String moduleName) {
        try {
            Class<?> clazz = Class.forName(className);
            Object instance = clazz.getDeclaredConstructor().newInstance();
            if (instance instanceof JPanel) {
                return (JPanel) instance;
            }
        } catch (Exception ignored) {}
        return buildPlaceholderPanel(moduleName);
    }

    private JPanel buildPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(40, 60, 40, 60)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(5, 0, 5, 0);

        JLabel icon = new JLabel("\uD83D\uDEA7"); // 🚧
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        gbc.gridy = 0;
        card.add(icon, gbc);

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(TEXT_DARK);
        gbc.gridy = 1;
        card.add(label, gbc);

        JLabel subLabel = new JLabel("This module is coming soon.");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subLabel.setForeground(new Color(127, 140, 141));
        gbc.gridy = 2;
        card.add(subLabel, gbc);

        panel.add(card);
        return panel;
    }

    // ==================== DASHBOARD OVERVIEW ====================

    private JPanel buildDashboardOverview() {
        JPanel overview = new JPanel(new BorderLayout(0, 20));
        overview.setBackground(BACKGROUND);
        overview.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Top welcome bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + "!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcomeLabel.setForeground(TEXT_DARK);
        topBar.add(welcomeLabel, BorderLayout.WEST);

        String dateStr = new SimpleDateFormat("EEEE, MMMM d, yyyy").format(new Date());
        JLabel dateLabel = new JLabel("\uD83D\uDCC5  " + dateStr);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(127, 140, 141));
        topBar.add(dateLabel, BorderLayout.EAST);

        overview.add(topBar, BorderLayout.NORTH);

        // Center content
        JPanel centerPanel = new JPanel(new BorderLayout(0, 25));
        centerPanel.setOpaque(false);

        // Stat cards row
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsPanel.setOpaque(false);

        // Fetch stats
        int totalStudents = 0;
        int totalTeachers = 0;
        int totalClasses = 0;
        double totalFees = 0.0;

        try {
            StudentService studentService = new StudentService();
            totalStudents = studentService.getTotalStudents();
        } catch (Exception e) {
            System.err.println("Error fetching student count: " + e.getMessage());
        }

        try {
            TeacherService teacherService = new TeacherService();
            totalTeachers = teacherService.getTotalTeachers();
        } catch (Exception e) {
            System.err.println("Error fetching teacher count: " + e.getMessage());
        }

        try {
            ClassService classService = new ClassService();
            totalClasses = classService.getTotalCount();
        } catch (Exception e) {
            System.err.println("Error fetching class count: " + e.getMessage());
        }

        try {
            FeeService feeService = new FeeService();
            totalFees = feeService.getTotalFeeCollected();
        } catch (Exception e) {
            System.err.println("Error fetching fee total: " + e.getMessage());
        }

        String feeFormatted = "\u20B9" + NumberFormat.getNumberInstance(new Locale("en", "IN")).format(totalFees);

        cardsPanel.add(createStatCard("\uD83D\uDC68\u200D\uD83C\uDF93", "Total Students",
                String.valueOf(totalStudents), PRIMARY));
        cardsPanel.add(createStatCard("\uD83D\uDC68\u200D\uD83C\uDFEB", "Total Teachers",
                String.valueOf(totalTeachers), SUCCESS));
        cardsPanel.add(createStatCard("\uD83D\uDCDA", "Total Classes",
                String.valueOf(totalClasses), WARNING));
        cardsPanel.add(createStatCard("\uD83D\uDCB0", "Fee Collected",
                feeFormatted, DANGER));

        centerPanel.add(cardsPanel, BorderLayout.NORTH);

        // Quick Actions
        JPanel quickActionsPanel = buildQuickActionsPanel();
        centerPanel.add(quickActionsPanel, BorderLayout.CENTER);

        overview.add(centerPanel, BorderLayout.CENTER);

        return overview;
    }

    private JPanel createStatCard(String icon, String label, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // shadow
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 12, 12);
                // card bg
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 12, 12);
                // left accent
                g2.setColor(accentColor);
                g2.fillRoundRect(0, 0, 6, getHeight() - 3, 6, 6);
                g2.fillRect(3, 0, 3, getHeight() - 3);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 24, 20, 20));
        card.setPreferredSize(new Dimension(0, 120));

        JPanel textPanel = new JPanel(new GridBagLayout());
        textPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 8, 0);
        textPanel.add(iconLabel, gbc);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(TEXT_DARK);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 3, 0);
        textPanel.add(valueLabel, gbc);

        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        labelLabel.setForeground(new Color(127, 140, 141));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        textPanel.add(labelLabel, gbc);

        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel buildQuickActionsPanel() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 15));
        wrapper.setOpaque(false);

        JLabel title = new JLabel("Quick Actions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_DARK);
        wrapper.add(title, BorderLayout.NORTH);

        JPanel actionsGrid = new JPanel(new GridLayout(2, 4, 15, 15));
        actionsGrid.setOpaque(false);

        actionsGrid.add(createQuickActionButton("\uD83D\uDC68\u200D\uD83C\uDF93", "Add Student", PRIMARY,
                () -> navigateTo("STUDENTS")));
        actionsGrid.add(createQuickActionButton("\uD83D\uDC68\u200D\uD83C\uDFEB", "Add Teacher", SUCCESS,
                () -> navigateTo("TEACHERS")));
        actionsGrid.add(createQuickActionButton("\uD83D\uDCDA", "Manage Classes", WARNING,
                () -> navigateTo("CLASSES")));
        actionsGrid.add(createQuickActionButton("\uD83D\uDCCB", "Take Attendance", new Color(155, 89, 182),
                () -> navigateTo("ATTENDANCE")));
        actionsGrid.add(createQuickActionButton("\uD83D\uDCDD", "Examinations", new Color(52, 152, 219),
                () -> navigateTo("EXAMINATIONS")));
        actionsGrid.add(createQuickActionButton("\uD83D\uDCB0", "Collect Fees", DANGER,
                () -> navigateTo("FEES")));
        actionsGrid.add(createQuickActionButton("\uD83D\uDCD6", "Manage Subjects", new Color(230, 126, 34),
                () -> navigateTo("SUBJECTS")));
        actionsGrid.add(createQuickActionButton("\uD83D\uDC65", "Manage Users", new Color(22, 160, 133),
                () -> navigateTo("USERS")));

        wrapper.add(actionsGrid, BorderLayout.CENTER);
        return wrapper;
    }

    private void navigateTo(String key) {
        JButton btn = navButtons.get(key);
        if (btn != null) {
            handleNavClick(key, btn);
        }
    }

    private JPanel createQuickActionButton(String icon, String label, Color accentColor, Runnable action) {
        JPanel card = new JPanel(new GridBagLayout()) {
            private boolean hovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hovered = true;
                        setCursor(new Cursor(Cursor.HAND_CURSOR));
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hovered = false;
                        setCursor(Cursor.getDefaultCursor());
                        repaint();
                    }

                    @Override
                    public void mouseClicked(MouseEvent e) {
                        action.run();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (hovered) {
                    g2.setColor(accentColor);
                } else {
                    g2.setColor(CARD_BG);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                // subtle border
                g2.setColor(new Color(0, 0, 0, 15));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(0, 90));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 5, 0);
        card.add(iconLbl, gbc);

        JLabel labelLbl = new JLabel(label);
        labelLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        labelLbl.setForeground(TEXT_DARK);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(labelLbl, gbc);

        // Update colors on hover
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                iconLbl.setForeground(Color.WHITE);
                labelLbl.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                iconLbl.setForeground(UIManager.getColor("Label.foreground"));
                labelLbl.setForeground(TEXT_DARK);
            }
        });

        return card;
    }
}
