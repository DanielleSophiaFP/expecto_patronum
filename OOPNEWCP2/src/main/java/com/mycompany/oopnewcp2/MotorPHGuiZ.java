/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopnewcp2;

/**
 *
 * @author Ej
 */
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.DateTimeException;

public class MotorPHGuiZ extends MotorPHGui {
    protected JFrame frame;
    private JPanel sidebar;
    private JTextArea contentArea;
    protected Account currentUser;

    // Polymorphic Interface for Role Dashboards
    private interface RoleDashboard {
        void buildSidebar(JPanel sidebar, MotorPHGuiZ app);
        void setContentArea(JTextArea contentArea);
    }

    // Admin Dashboard (Polymorphic Implementation)
    private class AdminDashboard implements RoleDashboard {
        private JTextArea contentArea;

        @Override
        public void setContentArea(JTextArea contentArea) {
            this.contentArea = contentArea;
        }

        @Override
        public void buildSidebar(JPanel sidebar, MotorPHGuiZ app) {
            sidebar.add(app.createSidebarBtn("View All Employees", e -> new EmployeeDataGUI(app).setVisible(true)));
            sidebar.add(Box.createVerticalStrut(15));
            sidebar.add(app.createSidebarBtn("View Employee Details", e -> app.showEmployeeDetailsPrompt()));
            sidebar.add(Box.createVerticalStrut(15));
            sidebar.add(app.createSidebarBtn("View Employee Salary", e -> app.showPayrollPrompt()));
            sidebar.add(Box.createVerticalStrut(15));
            sidebar.add(app.createSidebarBtn("View Employee Payroll", e -> app.showPayrollPrompt()));
            sidebar.add(Box.createVerticalStrut(15));
            sidebar.add(app.createSidebarBtn("Calculate Payroll", e -> app.showPayrollPrompt()));
            sidebar.add(Box.createVerticalStrut(15));
            sidebar.add(app.createSidebarBtn("Add New Employee", e -> JOptionPane.showMessageDialog(app.frame, "Add New Employee - Not implemented yet")));
            sidebar.add(Box.createVerticalStrut(15));
            sidebar.add(app.createSidebarBtn("Update Employee", e -> JOptionPane.showMessageDialog(app.frame, "Update Employee - Not implemented yet")));
            sidebar.add(Box.createVerticalStrut(15));
            sidebar.add(app.createSidebarBtn("Delete Employee", e -> JOptionPane.showMessageDialog(app.frame, "Delete Employee - Not implemented yet")));
        }
    }

    // Manager Dashboard (Polymorphic Implementation)
    private class ManagerDashboard implements RoleDashboard {
        private JTextArea contentArea;

        @Override
        public void setContentArea(JTextArea contentArea) {
            this.contentArea = contentArea;
        }

        @Override
        public void buildSidebar(JPanel sidebar, MotorPHGuiZ app) {
            sidebar.add(app.createSidebarBtn("View All Employees", e -> new EmployeeDataGUI(app).setVisible(true)));
            sidebar.add(Box.createVerticalStrut(15));
            sidebar.add(app.createSidebarBtn("View Employee Salary", e -> app.showPayrollPrompt()));
            sidebar.add(Box.createVerticalStrut(15));
            sidebar.add(app.createSidebarBtn("Calculate Payroll", e -> app.showPayrollPrompt()));
        }
    }

    // Employee Dashboard (Polymorphic Implementation)
    private class EmployeeDashboard implements RoleDashboard {
        private JTextArea contentArea;

        @Override
        public void setContentArea(JTextArea contentArea) {
            this.contentArea = contentArea;
        }

        @Override
        public void buildSidebar(JPanel sidebar, MotorPHGuiZ app) {
            String empID = app.currentUser.getEmployeeID();
            sidebar.add(app.createSidebarBtn("View My Details", e -> app.showEmployeeDetails(empID)));
            sidebar.add(Box.createVerticalStrut(15));
            sidebar.add(app.createSidebarBtn("View My Salary", e -> app.showPayrollForSelf()));
            sidebar.add(Box.createVerticalStrut(15));
            sidebar.add(app.createSidebarBtn("View My Payroll", e -> app.showPayrollForSelf()));
            sidebar.add(Box.createVerticalStrut(15));
            sidebar.add(app.createSidebarBtn("Calculate My Payroll", e -> app.showPayrollForSelf()));
        }
    }

    // Sidebar Button Style
    private static class SidebarButton extends JButton {
        public SidebarButton(String text) {
            super(text);
            setFont(new Font("Segoe UI", Font.BOLD, 16));
            setBackground(new Color(60, 141, 188));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    setBackground(new Color(45, 105, 141));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    setBackground(new Color(60, 141, 188));
                }
            });
        }
    }

    public SidebarButton createSidebarBtn(String text, java.awt.event.ActionListener listener) {
        SidebarButton btn = new SidebarButton(text);
        btn.addActionListener(listener);
        return btn;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MotorPHGuiZ().initialize());
    }

    private void initialize() {
        loadAllData();

        frame = new JFrame("MotorPH Employee App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1300, 800);
        frame.setMinimumSize(new Dimension(1100, 700));
        frame.setLocationRelativeTo(null);

        showLoginPanel();
        frame.setVisible(true);
    }

    private void showLoginPanel() {
        frame.getContentPane().removeAll();

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel title = new JLabel("MotorPH Employee App");
        title.setFont(new Font("Segoe UI", Font.BOLD, 40));
        title.setForeground(new Color(60, 141, 188));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        gbc.gridwidth = 1; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        JLabel userLbl = new JLabel("Username:");
        userLbl.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        panel.add(userLbl, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JTextField userField = new JTextField(25);
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        userField.setPreferredSize(new Dimension(400, 50));
        panel.add(userField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        JLabel passLbl = new JLabel("Password:");
        passLbl.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        panel.add(passLbl, gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        JPasswordField passField = new JPasswordField(25);
        passField.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        passField.setPreferredSize(new Dimension(400, 50));
        panel.add(passField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        JButton loginBtn = new SidebarButton("Login");
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
        panel.add(loginBtn, gbc);

        frame.getContentPane().add(panel);
        frame.revalidate();
        frame.repaint();
    }

    private void showDashboard() {
        frame.getContentPane().removeAll();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        contentArea = new JTextArea();
        contentArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
        contentArea.setEditable(false);
        contentArea.setBackground(Color.WHITE);
        contentArea.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        JScrollPane scroll = new JScrollPane(contentArea);
        mainPanel.add(scroll, BorderLayout.CENTER);

        sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(240, 248, 255));
        sidebar.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));
        sidebar.setPreferredSize(new Dimension(320, 0));

        // Polymorphism: Select and use the appropriate dashboard
        RoleDashboard dashboard = switch (currentUser.getRole().toLowerCase()) {
            case "admin" -> new AdminDashboard();
            case "manager" -> new ManagerDashboard();
            default -> new EmployeeDashboard();
        };

        dashboard.setContentArea(contentArea);
        dashboard.buildSidebar(sidebar, this);

        // Logout button
        sidebar.add(Box.createVerticalGlue());
        SidebarButton logout = new SidebarButton("Logout");
        logout.addActionListener(e -> showLoginPanel());
        sidebar.add(logout);

        mainPanel.add(sidebar, BorderLayout.EAST);
        frame.getContentPane().add(mainPanel);
        frame.revalidate();
        frame.repaint();

        contentArea.setText("Welcome, " + currentUser.getUsername() + "!\n\nSelect an option from the sidebar.");
    }

    public void showPayrollPrompt() {
        String empID = JOptionPane.showInputDialog(frame, "Enter Employee ID:");
        if (empID != null && !empID.trim().isEmpty() && getPayCalculators().containsKey(empID.trim())) {
            showPayrollDatePicker(empID.trim());
        } else if (empID != null) {
            JOptionPane.showMessageDialog(frame, "Invalid Employee ID!");
        }
    }

    public void showPayrollForSelf() {
        showPayrollDatePicker(currentUser.getEmployeeID());
    }

    private void showPayrollDatePicker(String empID) {
        LocalDate today = LocalDate.now();

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JPanel startPanel = new JPanel(new GridBagLayout());
        startPanel.setBorder(BorderFactory.createTitledBorder("Start Date"));

        JComboBox<Integer> startDay = createDayCombo();
        startDay.setSelectedItem(today.getDayOfMonth());
        JComboBox<Integer> startMonth = createMonthCombo();
        startMonth.setSelectedItem(today.getMonthValue());
        JComboBox<Integer> startYear = createYearCombo(today);
        startYear.setSelectedItem(today.getYear());

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        startPanel.add(new JLabel("Day:"), g); g.gridx++;
        startPanel.add(startDay, g); g.gridx++;
        startPanel.add(new JLabel("Month:"), g); g.gridx++;
        startPanel.add(startMonth, g); g.gridx++;
        startPanel.add(new JLabel("Year:"), g); g.gridx++;
        startPanel.add(startYear, g);

        JPanel endPanel = new JPanel(new GridBagLayout());
        endPanel.setBorder(BorderFactory.createTitledBorder("End Date"));

        JComboBox<Integer> endDay = createDayCombo();
        endDay.setSelectedItem(today.getDayOfMonth());
        JComboBox<Integer> endMonth = createMonthCombo();
        endMonth.setSelectedItem(today.getMonthValue());
        JComboBox<Integer> endYear = createYearCombo(today);
        endYear.setSelectedItem(today.getYear());

        g.gridx = 0; g.gridy = 0;
        endPanel.add(new JLabel("Day:"), g); g.gridx++;
        endPanel.add(endDay, g); g.gridx++;
        endPanel.add(new JLabel("Month:"), g); g.gridx++;
        endPanel.add(endMonth, g); g.gridx++;
        endPanel.add(new JLabel("Year:"), g); g.gridx++;
        endPanel.add(endYear, g);

        gbc.gridx = 0; gbc.gridy = 0;
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
        box.setPreferredSize(new Dimension(80, 40));
        box.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        return box;
    }

    private JComboBox<Integer> createMonthCombo() {
        JComboBox<Integer> box = new JComboBox<>();
        for (int i = 1; i <= 12; i++) box.addItem(i);
        box.setPreferredSize(new Dimension(80, 40));
        box.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        return box;
    }

    private JComboBox<Integer> createYearCombo(LocalDate ref) {
        JComboBox<Integer> box = new JComboBox<>();
        int year = ref.getYear();
        for (int i = year - 5; i <= year + 5; i++) box.addItem(i);
        box.setPreferredSize(new Dimension(120, 40));
        box.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        return box;
    }

    private LocalDate safeDate(int year, int month, int day) {
        try {
            return LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            return null;
        }
    }

    public void showEmployeeDetailsPrompt() {
        String empID = JOptionPane.showInputDialog(frame, "Enter Employee ID:");
        if (empID != null && !empID.trim().isEmpty() && getEmployees().containsKey(empID.trim())) {
            showEmployeeDetails(empID.trim());
        } else if (empID != null) {
            JOptionPane.showMessageDialog(frame, "Invalid Employee ID!");
        }
    }

    public void showEmployeeDetails(String empID) {
        Employee emp = getEmployees().get(empID);
        PayCalculator pc = getPayCalculators().get(empID);
        if (emp == null || pc == null) {
            contentArea.setText("Employee not found.");
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("=== Employee Details ===\n\n");
        details.append("Employee ID: ").append(empID).append("\n");
        details.append("Name: ").append(pc.getEmployeeName()).append("\n");
        details.append("Position: ").append(pc.getPosition()).append("\n");
        details.append("Birthday: ").append(emp.getBirthday()).append("\n");
        details.append("Address: ").append(emp.getAddress()).append("\n");
        details.append("Phone Number: ").append(emp.getPhoneNumber()).append("\n");
        details.append("SSS #: ").append(emp.getSss()).append("\n");
        details.append("PhilHealth #: ").append(emp.getPhilHealth()).append("\n");
        details.append("TIN #: ").append(emp.getTin()).append("\n");
        details.append("Pag-IBIG #: ").append(emp.getPagIbig()).append("\n");
        details.append("Status: ").append(emp.getStatus()).append("\n");
        details.append("Immediate Supervisor: ").append(emp.getImmediateSupervisor()).append("\n\n");
        details.append("Hourly Rate: P").append(String.format("%.2f", pc.getHourlyRate())).append("\n");
        details.append("Rice Subsidy: P").append(pc.getRiceSubsidy()).append("\n");
        details.append("Phone Allowance: P").append(pc.getPhoneAllowance()).append("\n");
        details.append("Clothing Allowance: P").append(pc.getClothingAllowance()).append("\n");

        contentArea.setText(details.toString());
    }
}
