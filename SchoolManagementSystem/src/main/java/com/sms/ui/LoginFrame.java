package com.sms.ui;

import com.sms.model.User;
import com.sms.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Professional login screen for the School Management System.
 * Authenticates users and routes to the appropriate dashboard based on role.
 */
public class LoginFrame extends JFrame {

    // Color scheme
    private static final Color PRIMARY = new Color(41, 128, 185);
    private static final Color PRIMARY_DARK = new Color(31, 97, 141);
    private static final Color BACKGROUND = new Color(236, 240, 241);
    private static final Color TEXT_DARK = new Color(44, 62, 80);
    private static final Color DANGER = new Color(231, 76, 60);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color FIELD_BORDER = new Color(189, 195, 199);
    private static final Color FIELD_FOCUS_BORDER = PRIMARY;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    private JCheckBox rememberMeCheckBox;

    private final AuthService authService;

    public LoginFrame() {
        authService = new AuthService();
        initializeFrame();
        buildUI();
        setLocationRelativeTo(null);
    }

    private void initializeFrame() {
        setTitle("School Management System - Login");
        setSize(450, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BACKGROUND);
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 0);

        // --- Card Panel ---
        JPanel cardPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                // subtle shadow
                g2.setColor(new Color(0, 0, 0, 15));
                g2.fillRoundRect(3, 3, getWidth() - 3, getHeight() - 3, 20, 20);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 20, 20);
                g2.dispose();
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setBorder(new EmptyBorder(30, 35, 30, 35));

        GridBagConstraints cc = new GridBagConstraints();
        cc.gridx = 0;
        cc.fill = GridBagConstraints.HORIZONTAL;
        cc.weightx = 1.0;

        // School logo placeholder
        JLabel logoLabel = new JLabel("\uD83C\uDFEB", SwingConstants.CENTER); // 🏫
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 56));
        cc.gridy = 0;
        cc.insets = new Insets(0, 0, 5, 0);
        cardPanel.add(logoLabel, cc);

        // Title
        JLabel titleLabel = new JLabel("School Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(TEXT_DARK);
        cc.gridy = 1;
        cc.insets = new Insets(0, 0, 3, 0);
        cardPanel.add(titleLabel, cc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Sign in to continue", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        cc.gridy = 2;
        cc.insets = new Insets(0, 0, 25, 0);
        cardPanel.add(subtitleLabel, cc);

        // Username label
        JLabel usernameLabel = new JLabel("  \uD83D\uDC64  Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        usernameLabel.setForeground(TEXT_DARK);
        cc.gridy = 3;
        cc.insets = new Insets(0, 0, 5, 0);
        cardPanel.add(usernameLabel, cc);

        // Username field
        usernameField = createStyledTextField("Enter your username");
        cc.gridy = 4;
        cc.insets = new Insets(0, 0, 15, 0);
        cardPanel.add(usernameField, cc);

        // Password label
        JLabel passwordLabel = new JLabel("  \uD83D\uDD12  Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordLabel.setForeground(TEXT_DARK);
        cc.gridy = 5;
        cc.insets = new Insets(0, 0, 5, 0);
        cardPanel.add(passwordLabel, cc);

        // Password field
        passwordField = createStyledPasswordField("Enter your password");
        cc.gridy = 6;
        cc.insets = new Insets(0, 0, 10, 0);
        cardPanel.add(passwordField, cc);

        // Remember me checkbox
        rememberMeCheckBox = new JCheckBox("Remember Me");
        rememberMeCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rememberMeCheckBox.setForeground(new Color(127, 140, 141));
        rememberMeCheckBox.setOpaque(false);
        rememberMeCheckBox.setFocusPainted(false);
        cc.gridy = 7;
        cc.insets = new Insets(0, 0, 18, 0);
        cardPanel.add(rememberMeCheckBox, cc);

        // Login button
        loginButton = createLoginButton();
        cc.gridy = 8;
        cc.insets = new Insets(0, 0, 15, 0);
        cardPanel.add(loginButton, cc);

        // Status label
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(DANGER);
        cc.gridy = 9;
        cc.insets = new Insets(0, 0, 0, 0);
        cardPanel.add(statusLabel, cc);

        // Add card to main panel
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(cardPanel, gbc);

        setContentPane(mainPanel);

        // Key bindings
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });

        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    passwordField.requestFocusInWindow();
                }
            }
        });

        loginButton.addActionListener(e -> performLogin());
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(new Color(189, 195, 199));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets insets = getInsets();
                    g2.drawString(placeholder, insets.left + 2, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                    g2.dispose();
                }
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(0, 42));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(Color.WHITE);
        field.setCaretColor(TEXT_DARK);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FIELD_FOCUS_BORDER, 2, true),
                        new EmptyBorder(7, 11, 7, 11)
                ));
                field.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
                        new EmptyBorder(8, 12, 8, 12)
                ));
                field.repaint();
            }
        });

        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(new Color(189, 195, 199));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets insets = getInsets();
                    g2.drawString(placeholder, insets.left + 2, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                    g2.dispose();
                }
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(0, 42));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        field.setBackground(Color.WHITE);
        field.setCaretColor(TEXT_DARK);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FIELD_FOCUS_BORDER, 2, true),
                        new EmptyBorder(7, 11, 7, 11)
                ));
                field.repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(FIELD_BORDER, 1, true),
                        new EmptyBorder(8, 12, 8, 12)
                ));
                field.repaint();
            }
        });

        return field;
    }

    private JButton createLoginButton() {
        JButton button = new JButton("Sign In") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(PRIMARY_DARK);
                } else if (getModel().isRollover()) {
                    g2.setColor(PRIMARY_DARK);
                } else {
                    g2.setColor(PRIMARY);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(0, 45));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password");
            return;
        }

        statusLabel.setText("Signing in...");
        statusLabel.setForeground(PRIMARY);
        loginButton.setEnabled(false);

        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() {
                return authService.authenticate(username, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    if (user != null) {
                        statusLabel.setText("Login successful! Redirecting...");
                        statusLabel.setForeground(new Color(39, 174, 96));

                        Timer timer = new Timer(500, evt -> {
                            openDashboard(user);
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        statusLabel.setText("Invalid username or password");
                        statusLabel.setForeground(DANGER);
                        passwordField.setText("");
                        passwordField.requestFocusInWindow();
                        loginButton.setEnabled(true);
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Login failed. Please try again.");
                    statusLabel.setForeground(DANGER);
                    loginButton.setEnabled(true);
                    ex.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void openDashboard(User user) {
        switch (user.getRole()) {
            case "ADMIN":
                AdminDashboard adminDashboard = new AdminDashboard(user);
                adminDashboard.setVisible(true);
                break;
            case "TEACHER":
                // TeacherDashboardFrame will be created as a separate module
                try {
                    Class<?> clazz = Class.forName("com.sms.ui.TeacherDashboardFrame");
                    JFrame teacherFrame = (JFrame) clazz.getDeclaredConstructor(User.class).newInstance(user);
                    teacherFrame.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Teacher Dashboard is not available yet.",
                            "Module Not Found", JOptionPane.INFORMATION_MESSAGE);
                    loginButton.setEnabled(true);
                    statusLabel.setText(" ");
                    return;
                }
                break;
            case "STUDENT":
                // StudentDashboardFrame will be created as a separate module
                try {
                    Class<?> clazz = Class.forName("com.sms.ui.StudentDashboardFrame");
                    JFrame studentFrame = (JFrame) clazz.getDeclaredConstructor(User.class).newInstance(user);
                    studentFrame.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Student Dashboard is not available yet.",
                            "Module Not Found", JOptionPane.INFORMATION_MESSAGE);
                    loginButton.setEnabled(true);
                    statusLabel.setText(" ");
                    return;
                }
                break;
            default:
                JOptionPane.showMessageDialog(this,
                        "Unknown user role: " + user.getRole(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                loginButton.setEnabled(true);
                statusLabel.setText(" ");
                return;
        }
        dispose();
    }

    /**
     * Application entry point.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception e) {
            System.err.println("FlatLaf not available, using system look and feel.");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        });
    }
}
