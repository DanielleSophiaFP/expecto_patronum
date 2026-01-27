/*
 * Click nbfs://nbhost/SystemFileServices/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileServices/Classes/Class.java to edit this template
 */
package com.mycompany.oopnewcp2;

/**
 *
 * @author Ej
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.DateTimeException;
import java.io.File;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
// import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;

public class MotorPHGuiZ extends MotorPHGui {
    private JFrame frame;
    private JPanel sidebar;
    private JTextArea contentArea;
    private Account currentUser;

    // MotorPH Color Palette
    private static final Color PRIMARY_PURPLE = new Color(200, 150, 220);
    private static final Color PRIMARY_BLUE = new Color(25, 55, 130);
    private static final Color ACCENT_RED = new Color(220, 40, 50);
    private static final Color ACCENT_YELLOW = new Color(255, 200, 50);
    private static final Color SIDEBAR_BG = new Color(31, 30, 71);
    private static final Color LIGHT_BG = new Color(245, 245, 245);
    private static final Color TEXT_PRIMARY = new Color(0, 0, 0);
    private static final Color TEXT_SIDEBAR = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(100, 100, 100);
    private static final Color BORDER_COLOR = new Color(200, 200, 200);
    private static final Color HEADER_GRADIENT_START = new Color(120, 80, 180);
    private static final Color HEADER_GRADIENT_END = new Color(255, 200, 150);

    // IMAGE PATHS
    private static final String LOGO_PATH = "src/main/java/motorphappguidatabaseimage/logo/logo2.png";
    private static final String LOGIN_ILLUSTRATION_PATH = "src/main/java/motorphappguidatabaseimage/login_illustration.png";
    private static final String EMPLOYEE_PICTURES_DIR = "src/main/java/motorphappguidatabaseimage/emppic/";

    // Custom Rounded Button
    private static class ModernButton extends JButton {
        private boolean isHovered = false;
        private final Color baseColor;
        private final Color hoverColor;
        private final Color textColor;

        public ModernButton(String text, Color baseColor, Color hoverColor, Color textColor) {
            super(text);
            this.baseColor = baseColor;
            this.hoverColor = hoverColor;
            this.textColor = textColor;
            
            setFont(new Font("Segoe UI", Font.BOLD, 15));
            setForeground(textColor);
            setBackground(baseColor);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(200, 50));
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color currentColor = isHovered ? hoverColor : baseColor;
            g2d.setColor(currentColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

            super.paintComponent(g);
        }
    }

    // Custom Sidebar Button
    private static class SidebarButton extends JButton {
        private boolean isHovered = false;

        public SidebarButton(String text) {
        super(text);
        setFont(new Font("Segoe UI", Font.PLAIN, 12));
        setBackground(SIDEBAR_BG);
        setForeground(TEXT_SIDEBAR);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setMaximumSize(new Dimension(200, 50));
        setPreferredSize(new Dimension(200, 50));
        setHorizontalAlignment(JButton.CENTER);
        setVerticalAlignment(JButton.CENTER);
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(5, 5, 5, 5));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isHovered) {
                g2d.setColor(new Color(80, 60, 140));
            } else {
                g2d.setColor(SIDEBAR_BG);
            }
            g2d.fillRect(0, 0, getWidth(), getHeight());

            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MotorPHGuiZ().initialize());
    }

    private void initialize() {
        loadAllData();

        frame = new JFrame("MotorPH Payroll & Employee Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1400, 900);
        frame.setMinimumSize(new Dimension(1200, 800));
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.WHITE);

        showLoginPanel();
        frame.setVisible(true);
    }

    private void showLoginPanel() {
        frame.getContentPane().removeAll();

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        mainPanel.setBackground(Color.WHITE);

       // ==================== LEFT SIDE - ILLUSTRATION ====================
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(PRIMARY_PURPLE);

        // Load illustration image from your specific path
        String imagePath = "C:\\Users\\steph\\OneDrive\\Documents\\NetBeansProjects\\expecto_patronum\\OOPNEWCP2\\src\\main\\java\\motorphappguidatabaseimage\\logo\\motorph.jpg";
        File illustrationFile = new File(imagePath);

        if (illustrationFile.exists()) {
            JLabel illustrationLabel = new JLabel();
            ImageIcon illustrationIcon = new ImageIcon(imagePath);
            java.awt.Image scaledImage = illustrationIcon.getImage().getScaledInstance(1000, 1000, java.awt.Image.SCALE_SMOOTH);
            illustrationLabel.setIcon(new ImageIcon(scaledImage));
            illustrationLabel.setHorizontalAlignment(JLabel.CENTER);
            illustrationLabel.setVerticalAlignment(JLabel.CENTER);
            leftPanel.add(illustrationLabel, BorderLayout.CENTER);
            System.out.println("✓ Illustration loaded: " + imagePath);
        } else {
            // Placeholder if image doesn't exist
            JLabel placeholderLabel = new JLabel();
            placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
            placeholderLabel.setVerticalAlignment(JLabel.CENTER);
            placeholderLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            placeholderLabel.setForeground(Color.WHITE);
            placeholderLabel.setText("Image not found at:\n" + imagePath);
            leftPanel.add(placeholderLabel, BorderLayout.CENTER);
            System.out.println("! Illustration not found at: " + imagePath);
        }

        // ==================== RIGHT SIDE - LOGIN FORM ====================
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;

        // Logo at top
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(Color.WHITE);
        
        File logoFile = new File(LOGO_PATH);
        JLabel logoLabel = new JLabel();
        logoLabel.setPreferredSize(new Dimension(180, 180));
        
        if (logoFile.exists()) {
            ImageIcon logoIcon = new ImageIcon(LOGO_PATH);
            java.awt.Image scaledImage = logoIcon.getImage().getScaledInstance(180, 180, java.awt.Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaledImage));
            System.out.println("✓ Logo loaded: " + logoFile.getAbsolutePath());
        } else {
            logoLabel.setText("LOGO");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            logoLabel.setForeground(PRIMARY_BLUE);
            System.out.println("! Logo not found at: " + logoFile.getAbsolutePath());
        }
        logoPanel.add(logoLabel);
        gbc.gridy = 0;
        rightPanel.add(logoPanel, gbc);

        // Title
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(PRIMARY_BLUE);
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 30, 0);
        rightPanel.add(titleLabel, gbc);

        // Username Label
        JLabel userLbl = new JLabel("USER NAME");
        userLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLbl.setForeground(TEXT_PRIMARY);
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 5, 0);
        rightPanel.add(userLbl, gbc);

        // Username Field
        JTextField userField = new UnderlineTextField();
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userField.setPreferredSize(new Dimension(300, 40));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 15, 0);
        rightPanel.add(userField, gbc);

        // Password Label
        JLabel passLbl = new JLabel("PASSWORD");
        passLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLbl.setForeground(TEXT_PRIMARY);
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 0, 5, 0);
        rightPanel.add(passLbl, gbc);

        // Password Field
        JPasswordField passField = new UnderlinePasswordField();
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passField.setPreferredSize(new Dimension(300, 40));
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 5, 0);
        rightPanel.add(passField, gbc);

        // Remember Me and Forgot Password
        JPanel checkPanel = new JPanel(new BorderLayout());
        checkPanel.setBackground(Color.WHITE);
        JCheckBox rememberMe = new JCheckBox("Remember Me");
        rememberMe.setBackground(Color.WHITE);
        rememberMe.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JLabel forgotLabel = new JLabel("<html><u>Forgot Password</u></html>");
        forgotLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        forgotLabel.setForeground(PRIMARY_BLUE);
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        checkPanel.add(rememberMe, BorderLayout.WEST);
        checkPanel.add(forgotLabel, BorderLayout.EAST);
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 30, 0);
        rightPanel.add(checkPanel, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        
        ModernButton loginBtn = new ModernButton("Login", PRIMARY_BLUE, new Color(15, 40, 100), Color.WHITE);
        loginBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            Account account = null;
            for (Account acc : getAccounts()) {
                if (acc.getUsername().equals(username) && acc.authenticate(password)) {
                    account = acc;
                    break;
                }
            }

            if (account != null) {
                currentUser = account;
                showDashboard();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(loginBtn);

        ModernButton signUpBtn = new ModernButton("Sign Up", Color.WHITE, new Color(240, 240, 240), PRIMARY_BLUE);
        signUpBtn.setBorder(BorderFactory.createLineBorder(PRIMARY_BLUE, 2));
        signUpBtn.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Sign Up - Coming soon"));
        buttonPanel.add(signUpBtn);

        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 0, 0);
        rightPanel.add(buttonPanel, gbc);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        frame.getContentPane().add(mainPanel);
        frame.revalidate();
        frame.repaint();
    }

    private void showDashboard() {
        frame.getContentPane().removeAll();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_BG);

        // ==================== TOP HEADER ====================
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, HEADER_GRADIENT_START, getWidth(), getHeight(), HEADER_GRADIENT_END);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 120));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));

        // Welcome message
        Employee emp = getEmployees().get(currentUser.getEmployeeID());
        String displayName = (emp != null) ? emp.getFirstName() + " " + emp.getLastName() : currentUser.getUsername();
        String displayID = currentUser.getEmployeeID() != null ? currentUser.getEmployeeID() : "N/A";
        String displayRole = currentUser.getRole();

        JLabel welcomeLabel = new JLabel("Welcome back, " + displayName);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        // User info panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        userPanel.setOpaque(false);

        JPanel userInfoText = new JPanel(new GridLayout(2, 1));
        userInfoText.setOpaque(false);
        JLabel userNameLabel = new JLabel(displayName);
        userNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 25));
        userNameLabel.setForeground(Color.WHITE);
        JLabel userRoleLabel = new JLabel(displayID + " - " + displayRole);
        userRoleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userRoleLabel.setForeground(new Color(240, 240, 240));
        userInfoText.add(userNameLabel);
        userInfoText.add(userRoleLabel);
        userPanel.add(userInfoText);

        // Employee Avatar
        JLabel avatarLabel = new JLabel();
        avatarLabel.setPreferredSize(new Dimension(92, 92));
        avatarLabel.setHorizontalAlignment(JLabel.CENTER);
        avatarLabel.setOpaque(true);
        avatarLabel.setBackground(new Color(200, 200, 200));
        avatarLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        
        ImageIcon empImg = loadEmployeeImage(displayID, 90, 90);
        if (empImg != null) {
            avatarLabel.setIcon(empImg);
        } else {
            avatarLabel.setText(displayName.substring(0, 1).toUpperCase());
            avatarLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
            avatarLabel.setForeground(Color.WHITE);
            avatarLabel.setBackground(PRIMARY_BLUE);
        }
        
        userPanel.add(avatarLabel);
        headerPanel.add(userPanel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // ==================== SIDEBAR ====================
       sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        sidebar.setPreferredSize(new Dimension(200, 0));
        
        // Logo in sidebar
        JPanel sidebarLogoPanel = new JPanel(new GridBagLayout());
        sidebarLogoPanel.setOpaque(false);
        sidebarLogoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        sidebarLogoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        File sidebarLogoFile = new File(LOGO_PATH);
        JLabel sidebarLogoLabel = new JLabel();
        sidebarLogoLabel.setPreferredSize(new Dimension(150, 150));
        
        if (sidebarLogoFile.exists()) {
            ImageIcon sidebarLogoIcon = new ImageIcon(LOGO_PATH);
            java.awt.Image scaledImage = sidebarLogoIcon.getImage().getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH);
            sidebarLogoLabel.setIcon(new ImageIcon(scaledImage));
        } else {
            sidebarLogoLabel.setText("LOGO");
            sidebarLogoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            sidebarLogoLabel.setForeground(ACCENT_YELLOW);
        }
        
        GridBagConstraints gbc2 = new GridBagConstraints();
        sidebarLogoPanel.add(sidebarLogoLabel, gbc2);
        sidebar.add(sidebarLogoPanel);

        // Menu items
        String role = currentUser.getRole().toLowerCase();

        if ("admin".equals(role)) {
            addSidebarSection("EMPLOYEE MANAGEMENT");
            sidebar.add(createSidebarBtn("Dashboard", e -> updateContent("dashboard")));
            sidebar.add(createSidebarBtn("View All Employees", e -> showAllEmployees()));
            sidebar.add(createSidebarBtn("View Details", e -> showEmployeeDetailsPrompt()));
            sidebar.add(Box.createVerticalStrut(20));

            addSidebarSection("PAYROLL MANAGEMENT");
            sidebar.add(createSidebarBtn("View Attendance", e -> showAttendancePrompt()));
            sidebar.add(createSidebarBtn("View Payroll", e -> showPayrollPrompt()));
            sidebar.add(createSidebarBtn("Download Payslip", e -> downloadPayslipPrompt()));
            sidebar.add(Box.createVerticalStrut(10));

            
            sidebar.add(createSidebarBtn("Settings", e -> JOptionPane.showMessageDialog(frame, "Settings - Coming soon")));
            sidebar.add(createSidebarBtn("Help", e -> JOptionPane.showMessageDialog(frame, "Help - Coming soon")));

        } else if ("manager".equals(role)) {
            addSidebarSection("EMPLOYEE");
            sidebar.add(createSidebarBtn("Dashboard", e -> updateContent("dashboard")));
            sidebar.add(createSidebarBtn("View Employees", e -> showAllEmployees()));
            sidebar.add(Box.createVerticalStrut(10));

            addSidebarSection("PAYROLL MANAGEMENT");
            sidebar.add(createSidebarBtn("View Attendance", e -> showAttendancePrompt()));
            sidebar.add(createSidebarBtn("View Payroll", e -> showPayrollPrompt()));
            sidebar.add(createSidebarBtn("Download Payslip", e -> downloadPayslipPrompt()));
            sidebar.add(Box.createVerticalStrut(10));

            sidebar.add(createSidebarBtn("Settings", e -> JOptionPane.showMessageDialog(frame, "Settings - Coming soon")));
            sidebar.add(createSidebarBtn("Help", e -> JOptionPane.showMessageDialog(frame, "Help - Coming soon")));

        } else {
            sidebar.add(createSidebarBtn("Dashboard", e -> updateContent("dashboard")));
            sidebar.add(createSidebarBtn("My Profile", e -> showEmployeeDetails(currentUser.getEmployeeID())));
            sidebar.add(Box.createVerticalStrut(10));

            addSidebarSection("PAYROLL MANAGEMENT");
            sidebar.add(createSidebarBtn("View Attendance", e -> showAttendancePrompt()));
            sidebar.add(createSidebarBtn("View Payroll", e -> showPayrollForSelf()));
            sidebar.add(createSidebarBtn("Download Payslip", e -> downloadPayslipForSelf()));
            sidebar.add(Box.createVerticalStrut(10));

            sidebar.add(createSidebarBtn("Settings", e -> JOptionPane.showMessageDialog(frame, "Settings - Coming soon")));
            sidebar.add(createSidebarBtn("Help", e -> JOptionPane.showMessageDialog(frame, "Help - Coming soon")));
        }

        // Logout
        sidebar.add(Box.createVerticalGlue());
        ModernButton logout = new ModernButton("Log Out", ACCENT_RED, new Color(180, 30, 40), Color.WHITE);
        logout.setMaximumSize(new Dimension(200, 50));
        logout.setPreferredSize(new Dimension(200, 50));
        logout.setAlignmentX(Component.CENTER_ALIGNMENT);
        logout.addActionListener(e -> showLoginPanel());
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(logout);

        JScrollPane sidebarScroll = new JScrollPane(sidebar);
        sidebarScroll.setBorder(null);
        sidebarScroll.setBackground(SIDEBAR_BG);
        sidebarScroll.getViewport().setBackground(SIDEBAR_BG);

        mainPanel.add(sidebarScroll, BorderLayout.WEST);

        // ==================== MAIN CONTENT AREA ====================
        JPanel contentMainPanel = new JPanel(new BorderLayout());
        contentMainPanel.setBackground(LIGHT_BG);
        contentMainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Content Area
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        contentArea = new JTextArea();
        contentArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        contentArea.setEditable(false);
        contentArea.setBackground(Color.WHITE);
        contentArea.setForeground(TEXT_PRIMARY);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setCaretColor(TEXT_PRIMARY);
        
        JScrollPane scroll = new JScrollPane(contentArea);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        contentPanel.add(scroll, BorderLayout.CENTER);

        contentMainPanel.add(contentPanel, BorderLayout.CENTER);

        mainPanel.add(contentMainPanel, BorderLayout.CENTER);

        frame.getContentPane().add(mainPanel);
        frame.revalidate();
        frame.repaint();

        updateContent("dashboard");
    }

    private void updateContent(String section) {
    if ("dashboard".equals(section)) {
        StringBuilder content = new StringBuilder();
        content.append("\n════════════════════════════════════════\n");
        content.append("            MOTORPH DASHBOARD           \n");
        content.append("════════════════════════════════════════\n\n");

        // Fetch stats dynamically
        java.util.Map<String, Employee> employees = getEmployees();
        int totalEmployees = employees.size();

        LocalDate today = LocalDate.now();
        int attendanceToday = 0;
        int employeesLate = 0;
        int overtimeToday = 0;

        for (Employee emp : employees.values()) {
            for (Attendance att : emp.getAttendanceList()) {
                if (att.getDate().equals(today)) {
                    attendanceToday++;
                    double workedHours = att.getWorkedHours();
                    if (att.getLogIn().isAfter(java.time.LocalTime.of(9, 10))) { // late if > 10 min
                        employeesLate++;
                    }
                    if (workedHours > 8) {
                        overtimeToday++;
                    }
                }
            }
        }

        // Placeholder for payrolls processed this month
        int payrollsProcessed = getPayCalculators().size(); // or compute for current month

        // Build dashboard content
        content.append(String.format("Total Employees: %d\n", totalEmployees));
        content.append(String.format("Attendance Today: %d\n", attendanceToday));
        content.append(String.format("Employees Late Today: %d\n", employeesLate));
        content.append(String.format("Overtime Today: %d\n", overtimeToday));
        content.append(String.format("Payrolls Processed This Month: %d\n", payrollsProcessed));

        content.append("\n════════════════════════════════════════\n");
        contentArea.setText(content.toString());
    }
}

    private void addSidebarSection(String title) {
         JPanel sectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
         sectionPanel.setBackground(SIDEBAR_BG);
         sectionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
    
         JLabel sectionLabel = new JLabel(title);
         sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
         sectionLabel.setForeground(ACCENT_YELLOW);
    
         sectionPanel.add(sectionLabel);
         sidebar.add(sectionPanel);
    }

    private SidebarButton createSidebarBtn(String text, java.awt.event.ActionListener listener) {
        SidebarButton btn = new SidebarButton(text);
        btn.setMaximumSize(new Dimension(250, 40));
        btn.setPreferredSize(new Dimension(250, 40));
        btn.addActionListener(listener);
        return btn;
    }

    // NEW METHOD: Show all employees in center panel
    private void showAllEmployees() {
        StringBuilder content = new StringBuilder();
        
        content.append("\n");
        content.append("═══════════════════════════════════════════════════════════════════════════════════════════\n");
        content.append("                                ALL EMPLOYEES                                             \n");
        content.append("═══════════════════════════════════════════════════════════════════════════════════════════\n\n");
        
        content.append(String.format("%-12s %-20s %-20s %-30s %-10s\n", "ID", "First Name", "Last Name", "Position", "Status"));
        content.append("───────────────────────────────────────────────────────────────────────────────────────────\n");
        
        java.util.Map<String, Employee> employees = getEmployees();
        java.util.Map<String, PayCalculator> payCalculators = getPayCalculators();
        
        int count = 0;
        for (String empID : employees.keySet()) {
            Employee emp = employees.get(empID);
            PayCalculator pc = payCalculators.get(empID);
            
            if (emp != null && pc != null) {
                content.append(String.format("%-12s %-20s %-20s %-30s %-10s\n", 
                    empID, 
                    emp.getFirstName(), 
                    emp.getLastName(), 
                    pc.getPosition(),
                    emp.getStatus()));
                count++;
            }
        }
        
        content.append("═══════════════════════════════════════════════════════════════════════════════════════════\n");
        content.append(String.format("\nTotal Employees: %d\n", count));
        content.append("\nTo view full details of an employee, click on 'View Details' and enter their Employee ID.\n");
        
        contentArea.setText(content.toString());
    }

    // NEW METHOD: Show Attendance Records
    private void showAttendancePrompt() {
        String empID = JOptionPane.showInputDialog(frame, "Enter Employee ID:");
        if (empID != null && !empID.trim().isEmpty() && getEmployees().containsKey(empID.trim())) {
            showAttendanceDatePicker(empID.trim());
        } else if (empID != null) {
            JOptionPane.showMessageDialog(frame, "Invalid Employee ID!");
        }
    }

    private void showAttendanceDatePicker(String empID) {
        LocalDate today = LocalDate.now();

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(LIGHT_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel startPanel = new JPanel(new GridBagLayout());
        startPanel.setBackground(Color.WHITE);
        startPanel.setBorder(BorderFactory.createTitledBorder("Start Date"));
        startPanel.setLayout(new GridBagLayout());

        JComboBox<Integer> startDay = createDayCombo();
        startDay.setSelectedItem(today.getDayOfMonth());
        JComboBox<Integer> startMonth = createMonthCombo();
        startMonth.setSelectedItem(today.getMonthValue());
        JComboBox<Integer> startYear = createYearCombo(today);
        startYear.setSelectedItem(today.getYear());

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        startPanel.add(new JLabel("Day:"), g);
        g.gridx++;
        startPanel.add(startDay, g);
        g.gridx++;
        startPanel.add(new JLabel("Month:"), g);
        g.gridx++;
        startPanel.add(startMonth, g);
        g.gridx++;
        startPanel.add(new JLabel("Year:"), g);
        g.gridx++;
        startPanel.add(startYear, g);

        JPanel endPanel = new JPanel(new GridBagLayout());
        endPanel.setBackground(Color.WHITE);
        endPanel.setBorder(BorderFactory.createTitledBorder("End Date"));
        endPanel.setLayout(new GridBagLayout());

        JComboBox<Integer> endDay = createDayCombo();
        endDay.setSelectedItem(today.getDayOfMonth());
        JComboBox<Integer> endMonth = createMonthCombo();
        endMonth.setSelectedItem(today.getMonthValue());
        JComboBox<Integer> endYear = createYearCombo(today);
        endYear.setSelectedItem(today.getYear());

        g.gridx = 0;
        g.gridy = 0;
        endPanel.add(new JLabel("Day:"), g);
        g.gridx++;
        endPanel.add(endDay, g);
        g.gridx++;
        endPanel.add(new JLabel("Month:"), g);
        g.gridx++;
        endPanel.add(endMonth, g);
        g.gridx++;
        endPanel.add(new JLabel("Year:"), g);
        g.gridx++;
        endPanel.add(endYear, g);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(startPanel, gbc);
        gbc.gridy = 1;
        inputPanel.add(endPanel, gbc);

        int result = JOptionPane.showConfirmDialog(frame, inputPanel, "Select Attendance Period", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            LocalDate start = safeDate((Integer) startYear.getSelectedItem(), (Integer) startMonth.getSelectedItem(), (Integer) startDay.getSelectedItem());
            LocalDate end = safeDate((Integer) endYear.getSelectedItem(), (Integer) endMonth.getSelectedItem(), (Integer) endDay.getSelectedItem());

            if (start == null || end == null) {
                JOptionPane.showMessageDialog(frame, "Invalid date selected.");
                return;
            }
            if (end.isBefore(start)) {
                JOptionPane.showMessageDialog(frame, "End date cannot be before start date.");
                return;
            }

            showAttendanceRecords(empID, start, end);
        }
    }

    private void showAttendanceRecords(String empID, LocalDate startDate, LocalDate endDate) {
        Employee emp = getEmployees().get(empID);
        if (emp == null) {
            contentArea.setText("Employee not found.");
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append("\n");
        content.append("═══════════════════════════════════════════════════════════════════\n");
        content.append("                      ATTENDANCE RECORDS                           \n");
        content.append("═══════════════════════════════════════════════════════════════════\n");
        content.append(String.format("Employee: %s (%s)\n", emp.getFirstName() + " " + emp.getLastName(), empID));
        content.append(String.format("Period: %s to %s\n", startDate, endDate));
        content.append("═══════════════════════════════════════════════════════════════════\n\n");

        content.append(String.format("%-12s %-12s %-12s %-10s %-12s\n", "Date", "Time In", "Time Out", "Late (hrs)", "Overtime"));
        content.append("───────────────────────────────────────────────────────────────────\n");

        for (Attendance att : emp.getAttendanceList()) {
            LocalDate date = att.getDate();
            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                double workedHours = att.getWorkedHours();
                double overtimeHours = Math.max(workedHours - 8, 0);
                double lateHours = 0;
                
                // Calculate tardiness
                java.time.LocalTime scheduledIn = java.time.LocalTime.of(9, 0);
                if (att.getLogIn().isAfter(scheduledIn)) {
                    long tardyMinutes = java.time.Duration.between(scheduledIn, att.getLogIn()).toMinutes();
                    if (tardyMinutes > 10) {
                        lateHours = (tardyMinutes - 10) / 60.0;
                    }
                }

                content.append(String.format("%-12s %-12s %-12s %-10.2f %-12.2f\n",
                    date,
                    att.getLogIn(),
                    att.getLogOut(),
                    lateHours,
                    overtimeHours));
            }
        }

        content.append("═══════════════════════════════════════════════════���═══════════════\n");

        contentArea.setText(content.toString());
    }

    // Download Payslip Methods
    private void downloadPayslipPrompt() {
        String empID = JOptionPane.showInputDialog(frame, "Enter Employee ID:");
        if (empID != null && !empID.trim().isEmpty() && getPayCalculators().containsKey(empID.trim())) {
            downloadPayslipDatePicker(empID.trim());
        } else if (empID != null) {
            JOptionPane.showMessageDialog(frame, "Invalid Employee ID!");
        }
    }

    private void downloadPayslipForSelf() {
        downloadPayslipDatePicker(currentUser.getEmployeeID());
    }

    private void downloadPayslipDatePicker(String empID) {
        LocalDate today = LocalDate.now();

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(LIGHT_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel startPanel = new JPanel(new GridBagLayout());
        startPanel.setBackground(Color.WHITE);
        startPanel.setBorder(BorderFactory.createTitledBorder("Start Date"));
        startPanel.setLayout(new GridBagLayout());

        JComboBox<Integer> startDay = createDayCombo();
        startDay.setSelectedItem(today.getDayOfMonth());
        JComboBox<Integer> startMonth = createMonthCombo();
        startMonth.setSelectedItem(today.getMonthValue());
        JComboBox<Integer> startYear = createYearCombo(today);
        startYear.setSelectedItem(today.getYear());

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        startPanel.add(new JLabel("Day:"), g);
        g.gridx++;
        startPanel.add(startDay, g);
        g.gridx++;
        startPanel.add(new JLabel("Month:"), g);
        g.gridx++;
        startPanel.add(startMonth, g);
        g.gridx++;
        startPanel.add(new JLabel("Year:"), g);
        g.gridx++;
        startPanel.add(startYear, g);

        JPanel endPanel = new JPanel(new GridBagLayout());
        endPanel.setBackground(Color.WHITE);
        endPanel.setBorder(BorderFactory.createTitledBorder("End Date"));
        endPanel.setLayout(new GridBagLayout());

        JComboBox<Integer> endDay = createDayCombo();
        endDay.setSelectedItem(today.getDayOfMonth());
        JComboBox<Integer> endMonth = createMonthCombo();
        endMonth.setSelectedItem(today.getMonthValue());
        JComboBox<Integer> endYear = createYearCombo(today);
        endYear.setSelectedItem(today.getYear());

        g.gridx = 0;
        g.gridy = 0;
        endPanel.add(new JLabel("Day:"), g);
        g.gridx++;
        endPanel.add(endDay, g);
        g.gridx++;
        endPanel.add(new JLabel("Month:"), g);
        g.gridx++;
        endPanel.add(endMonth, g);
        g.gridx++;
        endPanel.add(new JLabel("Year:"), g);
        g.gridx++;
        endPanel.add(endYear, g);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(startPanel, gbc);
        gbc.gridy = 1;
        inputPanel.add(endPanel, gbc);

        int result = JOptionPane.showConfirmDialog(frame, inputPanel, "Select Payroll Period", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            LocalDate start = safeDate((Integer) startYear.getSelectedItem(), (Integer) startMonth.getSelectedItem(), (Integer) startDay.getSelectedItem());
            LocalDate end = safeDate((Integer) endYear.getSelectedItem(), (Integer) endMonth.getSelectedItem(), (Integer) endDay.getSelectedItem());

            if (start == null || end == null) {
                JOptionPane.showMessageDialog(frame, "Invalid date selected.");
                return;
            }
            if (end.isBefore(start)) {
                JOptionPane.showMessageDialog(frame, "End date cannot be before start date.");
                return;
            }

            generateAndDownloadPayslipPDF(empID, start, end);
        }
    }

    private void generateAndDownloadPayslipPDF(String empID, LocalDate startDate, LocalDate endDate) {
        try {
            PayCalculator pc = getPayCalculators().get(empID);
            Employee emp = getEmployees().get(empID);
            
            if (pc == null || emp == null) {
                JOptionPane.showMessageDialog(frame, "Employee not found.");
                return;
            }

            // Calculate payroll data
            double totalRegularHours = 0;
            double totalOvertimeHours = 0;
            double totalTardinessHours = 0;
            int daysLate = 0;

            for (Attendance att : emp.getAttendanceList()) {
                LocalDate date = att.getDate();
                if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                    double dailyHours = att.getWorkedHours();
                    totalRegularHours += Math.min(dailyHours, 8);
                    totalOvertimeHours += Math.max(dailyHours - 8, 0);

                    java.time.LocalTime logIn = att.getLogIn();
                    java.time.LocalTime scheduledIn = java.time.LocalTime.of(9, 0);
                    if (logIn.isAfter(scheduledIn)) {
                        long tardyMinutes = java.time.Duration.between(scheduledIn, logIn).toMinutes();
                        if (tardyMinutes > 10) {
                            totalTardinessHours += (tardyMinutes - 10) / 60.0;
                            daysLate++;
                        }
                    }
                }
            }

            double regularPay = totalRegularHours * pc.getHourlyRate();
            double overtimePay = totalOvertimeHours * pc.getHourlyRate() * 1.25;
            double allowances = pc.getTotalAllowances();
            double grossPay = regularPay + overtimePay + allowances;
            
            double tardinessDeduction = totalTardinessHours * pc.getHourlyRate();
            double sssDeduction = grossPay * 0.045;
            double philHealthDeduction = grossPay * 0.0225;
            double pagIbigDeduction = 100.0;
            
            double totalDeductions = tardinessDeduction + sssDeduction + philHealthDeduction + pagIbigDeduction;
            double netPay = grossPay - totalDeductions;

            // Create PDF
            String fileName = "Payslip_" + empID + "_" + startDate + "_to_" + endDate + ".pdf";
            String filePath = "payslips/" + fileName;
            
            new java.io.File("payslips").mkdirs();

            PdfWriter writer = new PdfWriter(filePath);
            Document document = new Document(new com.itextpdf.kernel.pdf.PdfDocument(writer));

            // Add Logo
            try {
                Image logo = new Image(ImageDataFactory.create(LOGO_PATH));
                logo.setWidth(100);
                logo.setHeight(100);
                document.add(logo);
            } catch (Exception e) {
                System.out.println("Logo not found: " + e.getMessage());
            }

            /*
            // Add Title
            Paragraph title = new Paragraph("MOTORPH PAYROLL STATEMENT")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(18)
                .setBold();
            document.add(title);
            */

            // Add Employee Info
            Paragraph empInfo = new Paragraph(
                "Employee: " + pc.getEmployeeName() + " | ID: " + empID + " | Position: " + pc.getPosition() + 
                "\nPeriod: " + startDate + " to " + endDate
            ).setFontSize(11);
            document.add(empInfo);
            document.add(new Paragraph("\n"));

            // Create table
            Table table = new Table(new float[]{2, 2, 3});
            table.addCell("Description");
            table.addCell("Hours");
            table.addCell("Amount");

            table.addCell("Basic Pay");
            table.addCell(String.format("%.2f", totalRegularHours));
            table.addCell(String.format("P%.2f", regularPay));

            table.addCell("Overtime");
            table.addCell(String.format("%.2f", totalOvertimeHours));
            table.addCell(String.format("P%.2f", overtimePay));

            table.addCell("Tardiness");
            table.addCell(String.format("%.2f", totalTardinessHours));
            table.addCell(String.format("-P%.2f", tardinessDeduction));

            table.addCell("Rice Subsidy");
            table.addCell("");
            table.addCell(String.format("P%.2f", pc.getRiceSubsidy()));

            table.addCell("Phone Allowance");
            table.addCell("");
            table.addCell(String.format("P%.2f", pc.getPhoneAllowance()));

            table.addCell("Clothing Allowance");
            table.addCell("");
            table.addCell(String.format("P%.2f", pc.getClothingAllowance()));

            table.addCell("GROSS PAY");
            table.addCell("");
            table.addCell(String.format("P%.2f", grossPay));

            table.addCell("TOTAL DEDUCTIONS");
            table.addCell("");
            table.addCell(String.format("-P%.2f", totalDeductions));

            table.addCell("NET PAY");
            table.addCell("");
            table.addCell(String.format("P%.2f", netPay));

            document.add(table);
            document.close();

            JOptionPane.showMessageDialog(frame, "Payslip downloaded successfully!\nSaved to: " + new java.io.File(filePath).getAbsolutePath());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Error generating PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Load employee image helper
    private ImageIcon loadEmployeeImage(String empID, int width, int height) {
        String role = currentUser.getRole().toLowerCase();
        String imageID = empID;
        
        if ("admin".equals(role)) {
            imageID = "null";
        } else if ("manager".equals(role)) {
            imageID = "10001";
        }
        
        File imgFile = new File(EMPLOYEE_PICTURES_DIR + imageID + ".png");
        if (!imgFile.exists()) {
            imgFile = new File(EMPLOYEE_PICTURES_DIR + imageID + ".jpg");
        }
        
        if (imgFile.exists()) {
            try {
                ImageIcon icon = new ImageIcon(imgFile.getAbsolutePath());
                java.awt.Image scaledImage = icon.getImage().getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            } catch (Exception ex) {
                return null;
            }
        }
        return null;
    }

    private void showPayrollPrompt() {
        String empID = JOptionPane.showInputDialog(frame, "Enter Employee ID:");
        if (empID != null && !empID.trim().isEmpty() && getPayCalculators().containsKey(empID.trim())) {
            showPayrollDatePicker(empID.trim());
        } else if (empID != null) {
            JOptionPane.showMessageDialog(frame, "Invalid Employee ID!");
        }
    }

    private void showPayrollForSelf() {
        showPayrollDatePicker(currentUser.getEmployeeID());
    }

    private void showPayrollDatePicker(String empID) {
        LocalDate today = LocalDate.now();

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(LIGHT_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel startPanel = new JPanel(new GridBagLayout());
        startPanel.setBackground(Color.WHITE);
        startPanel.setBorder(BorderFactory.createTitledBorder("Start Date"));
        startPanel.setLayout(new GridBagLayout());

        JComboBox<Integer> startDay = createDayCombo();
        startDay.setSelectedItem(today.getDayOfMonth());
        JComboBox<Integer> startMonth = createMonthCombo();
        startMonth.setSelectedItem(today.getMonthValue());
        JComboBox<Integer> startYear = createYearCombo(today);
        startYear.setSelectedItem(today.getYear());

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        startPanel.add(new JLabel("Day:"), g);
        g.gridx++;
        startPanel.add(startDay, g);
        g.gridx++;
        startPanel.add(new JLabel("Month:"), g);
        g.gridx++;
        startPanel.add(startMonth, g);
        g.gridx++;
        startPanel.add(new JLabel("Year:"), g);
        g.gridx++;
        startPanel.add(startYear, g);

        JPanel endPanel = new JPanel(new GridBagLayout());
        endPanel.setBackground(Color.WHITE);
        endPanel.setBorder(BorderFactory.createTitledBorder("End Date"));
        endPanel.setLayout(new GridBagLayout());

        JComboBox<Integer> endDay = createDayCombo();
        endDay.setSelectedItem(today.getDayOfMonth());
        JComboBox<Integer> endMonth = createMonthCombo();
        endMonth.setSelectedItem(today.getMonthValue());
        JComboBox<Integer> endYear = createYearCombo(today);
        endYear.setSelectedItem(today.getYear());

        g.gridx = 0;
        g.gridy = 0;
        endPanel.add(new JLabel("Day:"), g);
        g.gridx++;
        endPanel.add(endDay, g);
        g.gridx++;
        endPanel.add(new JLabel("Month:"), g);
        g.gridx++;
        endPanel.add(endMonth, g);
        g.gridx++;
        endPanel.add(new JLabel("Year:"), g);
        g.gridx++;
        endPanel.add(endYear, g);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(startPanel, gbc);
        gbc.gridy = 1;
        inputPanel.add(endPanel, gbc);

        int result = JOptionPane.showConfirmDialog(frame, inputPanel, "Select Payroll Period", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            LocalDate start = safeDate((Integer) startYear.getSelectedItem(), (Integer) startMonth.getSelectedItem(), (Integer) startDay.getSelectedItem());
            LocalDate end = safeDate((Integer) endYear.getSelectedItem(), (Integer) endMonth.getSelectedItem(), (Integer) endDay.getSelectedItem());

            if (start == null || end == null) {
                JOptionPane.showMessageDialog(frame, "Invalid date selected.");
                return;
            }
            if (end.isBefore(start)) {
                JOptionPane.showMessageDialog(frame, "End date cannot be before start date.");
                return;
            }

            calculateAndDisplayPayroll(empID, start, end, contentArea);
        }
    }

    private JComboBox<Integer> createDayCombo() {
        JComboBox<Integer> box = new JComboBox<>();
        for (int i = 1; i <= 31; i++) box.addItem(i);
        box.setPreferredSize(new Dimension(80, 35));
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return box;
    }

    private JComboBox<Integer> createMonthCombo() {
        JComboBox<Integer> box = new JComboBox<>();
        for (int i = 1; i <= 12; i++) box.addItem(i);
        box.setPreferredSize(new Dimension(80, 35));
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return box;
    }

    private JComboBox<Integer> createYearCombo(LocalDate ref) {
        JComboBox<Integer> box = new JComboBox<>();
        int year = ref.getYear();
        for (int i = year - 5; i <= year + 5; i++) box.addItem(i);
        box.setPreferredSize(new Dimension(120, 35));
        box.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return box;
    }

    private LocalDate safeDate(int year, int month, int day) {
        try {
            return LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            return null;
        }
    }

    private void showEmployeeDetailsPrompt() {
        String empID = JOptionPane.showInputDialog(frame, "Enter Employee ID:");
        if (empID != null && !empID.trim().isEmpty() && getEmployees().containsKey(empID.trim())) {
            showEmployeeDetails(empID.trim());
        } else if (empID != null) {
            JOptionPane.showMessageDialog(frame, "Invalid Employee ID!");
        }
    }

    private void showEmployeeDetails(String empID) {
        Employee emp = getEmployees().get(empID);
        PayCalculator pc = getPayCalculators().get(empID);
        if (emp == null || pc == null) {
            contentArea.setText("Employee not found.");
            return;
        }

        StringBuilder details = new StringBuilder();
        
        details.append("\n");
        details.append("═══════════════════════════════════════════════════════════════════\n");
        details.append("                          EMPLOYEE PROFILE                          \n");
        details.append("══��════════════════════════════════════════════════════════════════\n\n");
        
        details.append("PERSONAL INFORMATION\n");
        details.append("───────────────────────────────────────────────────────────────────\n");
        details.append(String.format("%-30s: %s\n", "Employee ID", empID));
        details.append(String.format("%-30s: %s\n", "Full Name", emp.getFirstName() + " " + emp.getLastName()));
        details.append(String.format("%-30s: %s\n", "Position", pc.getPosition()));
        details.append(String.format("%-30s: %s\n", "Birthday", emp.getBirthday()));
        details.append(String.format("%-30s: %s\n", "Address", emp.getAddress()));
        details.append(String.format("%-30s: %s\n", "Phone Number", emp.getPhoneNumber()));
        details.append(String.format("%-30s: %s\n", "Status", emp.getStatus()));
        details.append(String.format("%-30s: %s\n", "Immediate Supervisor", emp.getImmediateSupervisor()));
        
        details.append("\n");
        details.append("GOVERNMENT IDs\n");
        details.append("───────────────────────────────────────────────────────────────────\n");
        details.append(String.format("%-30s: %s\n", "SSS #", emp.getSss()));
        details.append(String.format("%-30s: %s\n", "PhilHealth #", emp.getPhilHealth()));
        details.append(String.format("%-30s: %s\n", "TIN #", emp.getTin()));
        details.append(String.format("%-30s: %s\n", "Pag-IBIG #", emp.getPagIbig()));
        
        details.append("\n");
        details.append("COMPENSATION\n");
        details.append("───────────────────────────────────────────────────────────────────\n");
        details.append(String.format("%-30s: P%,.2f\n", "Hourly Rate", pc.getHourlyRate()));
        details.append(String.format("%-30s: P%,.2f\n", "Rice Subsidy", pc.getRiceSubsidy()));
        details.append(String.format("%-30s: P%,.2f\n", "Phone Allowance", pc.getPhoneAllowance()));
        details.append(String.format("%-30s: P%,.2f\n", "Clothing Allowance", pc.getClothingAllowance()));
        
        details.append("═══════════════════════════════════════════════════════════════════\n");

        contentArea.setText(details.toString());
    }

    // Custom Underline Text Field
    private static class UnderlineTextField extends JTextField {
        public UnderlineTextField() {
            super();
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(TEXT_SECONDARY);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawLine(0, getHeight() - 2, getWidth(), getHeight() - 2);
        }
    }

    // Custom Underline Password Field
    private static class UnderlinePasswordField extends JPasswordField {
        public UnderlinePasswordField() {
            super();
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(TEXT_SECONDARY);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawLine(0, getHeight() - 2, getWidth(), getHeight() - 2);
        }
    }
}