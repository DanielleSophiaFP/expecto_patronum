/*
 * Click nbproject://nbproject/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.motorphappgui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * @author basil
 */
public class MotorPHGuiZ extends MotorPHGui {
    // GUI components
    private JFrame frame;
    private JPanel currentPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextArea outputArea;
    private Account currentUser; // To track the logged-in user

    // Custom button class for consistent styling
    static class CustomButton extends JButton {
        public CustomButton(String text, Icon icon) {
            super(text, icon);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setBackground(new Color(60, 141, 188));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(new Color(45, 105, 141));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(new Color(60, 141, 188));
                }
            });
        }
    }

    // Custom font class to handle potential aliasing issues
    static class AliasedLabelFont extends Font {
        public AliasedLabelFont(String name, int style, int size) {
            super(name, style, size);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MotorPHGuiZ().initialize());
    }

    private void initialize() {
        loadAllData();
        frame = new JFrame("MotorPH Employee App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setMinimumSize(new Dimension(700, 500));
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(new Color(245, 245, 245));
        showLoginPanel();
        frame.setVisible(true);
    }

    private void showLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel("MotorPH Employee App");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(60, 141, 188));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(usernameLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        usernameField.setPreferredSize(new Dimension(250, 35));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordField.setPreferredSize(new Dimension(250, 35));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton loginButton = new CustomButton("Login", null);
        loginButton.setPreferredSize(new Dimension(120, 45));
        loginButton.addActionListener(e -> login());
        panel.add(loginButton, gbc);

        switchPanel(panel);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        for (Account acc : accounts) {
            if (acc.getUsername().equals(username) && acc.authenticate(password)) {
                currentUser = acc;
                JOptionPane.showMessageDialog(frame, "Login successful! Role: " + currentUser.getRole());
                switch (currentUser.getRole()) {
                    case "admin": showAdminPanel(); break;
                    case "manager": showManagerPanel(); break;
                    case "employee": showEmployeePanel(); break;
                    default: JOptionPane.showMessageDialog(frame, "Unknown role.");
                }
                return;
            }
        }
        JOptionPane.showMessageDialog(frame, "Invalid credentials. Try again.");
    }

    private void showAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        outputArea = new JTextArea(20, 60);
        outputArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        outputArea.setEditable(false);
        outputArea.setBackground(Color.WHITE);
        outputArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(9, 1, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        JButton viewAllButton = new CustomButton("View All Employees", null);
        JButton viewDetailsButton = new CustomButton("View Employee Details", null);
        JButton viewSalaryButton = new CustomButton("View Employee Salary", null);
        JButton viewPayrollButton = new CustomButton("View Employee Payroll", null);
        JButton calculatePayrollButton = new CustomButton("Calculate Payroll", null);
        JButton addButton = new CustomButton("Add New Employee", null);
        JButton updateButton = new CustomButton("Update Employee", null);
        JButton deleteButton = new CustomButton("Delete Employee", null);
        JButton logoutButton = new CustomButton("Logout", null);

        viewAllButton.addActionListener(e -> viewAllEmployees());
        viewDetailsButton.addActionListener(e -> viewEmployeeDetails());
        viewSalaryButton.addActionListener(e -> viewEmployeeSalary());
        viewPayrollButton.addActionListener(e -> viewEmployeePayroll());
        calculatePayrollButton.addActionListener(e -> calculatePayroll());
        addButton.addActionListener(e -> addEmployee());
        updateButton.addActionListener(e -> updateEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(viewAllButton);
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(viewSalaryButton);
        buttonPanel.add(viewPayrollButton);
        buttonPanel.add(calculatePayrollButton);
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(logoutButton);

        panel.add(buttonPanel, BorderLayout.EAST);
        switchPanel(panel);
    }

    private void showManagerPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        outputArea = new JTextArea(20, 60);
        outputArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        outputArea.setEditable(false);
        outputArea.setBackground(Color.WHITE);
        outputArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        JButton viewAllButton = new CustomButton("View All Employees", null);
        JButton viewSalaryButton = new CustomButton("View Employee Salary", null);
        JButton calculatePayrollButton = new CustomButton("Calculate Payroll", null);
        JButton logoutButton = new CustomButton("Logout", null);

        viewAllButton.addActionListener(e -> viewAllEmployees());
        viewSalaryButton.addActionListener(e -> viewEmployeeSalary());
        calculatePayrollButton.addActionListener(e -> calculatePayroll());
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(viewAllButton);
        buttonPanel.add(viewSalaryButton);
        buttonPanel.add(calculatePayrollButton);
        buttonPanel.add(logoutButton);

        panel.add(buttonPanel, BorderLayout.EAST);
        switchPanel(panel);
    }

    private void showEmployeePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        outputArea = new JTextArea(20, 60);
        outputArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        outputArea.setEditable(false);
        outputArea.setBackground(Color.WHITE);
        outputArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        JButton viewDetailsButton = new CustomButton("View My Details", null);
        JButton viewSalaryButton = new CustomButton("View My Salary", null);
        JButton viewPayrollButton = new CustomButton("View My Payroll", null);
        JButton calculatePayrollButton = new CustomButton("Calculate My Payroll", null);
        JButton logoutButton = new CustomButton("Logout", null);

        viewDetailsButton.addActionListener(e -> viewEmployeeByID(currentUser.getEmployeeID()));
        viewSalaryButton.addActionListener(e -> viewSalaryByID(currentUser.getEmployeeID()));
        viewPayrollButton.addActionListener(e -> viewPayrollByID(currentUser.getEmployeeID()));
        calculatePayrollButton.addActionListener(e -> calculatePayrollForCurrentUser());
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(viewSalaryButton);
        buttonPanel.add(viewPayrollButton);
        buttonPanel.add(calculatePayrollButton);
        buttonPanel.add(logoutButton);

        panel.add(buttonPanel, BorderLayout.EAST);
        switchPanel(panel);
    }

    private void switchPanel(JPanel newPanel) {
        if (currentPanel != null) {
            frame.remove(currentPanel);
        }
        currentPanel = newPanel;
        frame.add(currentPanel);
        frame.revalidate();
        frame.repaint();
    }

    private void logout() {
        currentUser = null;
        usernameField.setText("");
        passwordField.setText("");
        showLoginPanel();
    }

    private void viewAllEmployees() {
        SwingUtilities.invokeLater(() -> {
            EmployeeDataGUI employeeDataGUI = new EmployeeDataGUI();
            employeeDataGUI.setVisible(true);
        });
    }

    private JComboBox<String> createEmployeeComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        for (Employee emp : employees.values()) {
            comboBox.addItem(emp.getEmployeeID() + " - " + emp.getFirstName() + " " + emp.getLastName());
        }
        return comboBox;
    }

    private JComboBox<Integer> createDayComboBox() {
        JComboBox<Integer> comboBox = new JComboBox<>();
        for (int day = 1; day <= 31; day++) {
            comboBox.addItem(day);
        }
        return comboBox;
    }

    private JComboBox<Integer> createMonthComboBox() {
        JComboBox<Integer> comboBox = new JComboBox<>();
        for (int month = 1; month <= 12; month++) {
            comboBox.addItem(month);
        }
        return comboBox;
    }

    private JComboBox<Integer> createYearComboBox(LocalDate initialDate) {
        JComboBox<Integer> comboBox = new JComboBox<>();
        int currentYear = initialDate.getYear();
        for (int year = currentYear - 5; year <= currentYear + 5; year++) {
            comboBox.addItem(year);
        }
        return comboBox;
    }

    private LocalDate constructDateFromComponents(int day, int month, int year) {
        try {
            return LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            return null;
        }
    }

    private void viewEmployeeDetails() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel empLabel = new JLabel("Select Employee:");
        empLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JComboBox<String> empComboBox = createEmployeeComboBox();
        empComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        empComboBox.setPreferredSize(new Dimension(300, 35));

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(empLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(empComboBox, gbc);

        int result = JOptionPane.showConfirmDialog(frame, inputPanel, "View Employee Details", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String selectedEmp = (String) empComboBox.getSelectedItem();
            if (selectedEmp == null) {
                JOptionPane.showMessageDialog(frame, "Please select an employee.");
                return;
            }
            String empID = selectedEmp.split(" - ")[0];
            viewEmployeeByID(empID);
        }
    }

    private void viewEmployeePayroll() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel empLabel = new JLabel("Select Employee:");
        empLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JComboBox<String> empComboBox = createEmployeeComboBox();
        empComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        empComboBox.setPreferredSize(new Dimension(300, 35));

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(empLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(empComboBox, gbc);

        int result = JOptionPane.showConfirmDialog(frame, inputPanel, "View Employee Payroll", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String selectedEmp = (String) empComboBox.getSelectedItem();
            if (selectedEmp == null) {
                JOptionPane.showMessageDialog(frame, "Please select an employee.");
                return;
            }
            String empID = selectedEmp.split(" - ")[0];
            viewPayrollByID(empID);
        }
    }

    private void calculatePayroll() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(245, 245, 245));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Employee Selection
        JLabel empLabel = new JLabel("Select Employee:");
        empLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JComboBox<String> empComboBox = createEmployeeComboBox();
        empComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        empComboBox.setPreferredSize(new Dimension(250, 35));
        empComboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        inputPanel.add(empLabel, gbc);
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        inputPanel.add(empComboBox, gbc);

        // Start Date Panel
        JPanel startDatePanel = new JPanel(new GridBagLayout());
        startDatePanel.setBackground(new Color(245, 245, 245));
        startDatePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "Start Date"));
        GridBagConstraints gbcDate = new GridBagConstraints();
        gbcDate.insets = new Insets(5, 5, 5, 5);
        gbcDate.anchor = GridBagConstraints.WEST;

        LocalDate initialDate = LocalDate.now().withDayOfMonth(1);
        JComboBox<Integer> startDayComboBox = createDayComboBox();
        startDayComboBox.setSelectedItem(initialDate.getDayOfMonth());
        startDayComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        startDayComboBox.setPreferredSize(new Dimension(70, 30));
        startDayComboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JComboBox<Integer> startMonthComboBox = createMonthComboBox();
        startMonthComboBox.setSelectedItem(initialDate.getMonthValue());
        startMonthComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        startMonthComboBox.setPreferredSize(new Dimension(70, 30));
        startMonthComboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JComboBox<Integer> startYearComboBox = createYearComboBox(initialDate);
        startYearComboBox.setSelectedItem(initialDate.getYear());
        startYearComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        startYearComboBox.setPreferredSize(new Dimension(100, 30));
        startYearComboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        gbcDate.gridx = 0;
        gbcDate.gridy = 0;
        startDatePanel.add(new JLabel("Day:"), gbcDate);
        gbcDate.gridx = 1;
        startDatePanel.add(startDayComboBox, gbcDate);
        gbcDate.gridx = 2;
        startDatePanel.add(new JLabel("Month:"), gbcDate);
        gbcDate.gridx = 3;
        startDatePanel.add(startMonthComboBox, gbcDate);
        gbcDate.gridx = 4;
        startDatePanel.add(new JLabel("Year:"), gbcDate);
        gbcDate.gridx = 5;
        startDatePanel.add(startYearComboBox, gbcDate);

        // End Date Panel
        JPanel endDatePanel = new JPanel(new GridBagLayout());
        endDatePanel.setBackground(new Color(245, 245, 245));
        endDatePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "End Date"));
        JComboBox<Integer> endDayComboBox = createDayComboBox();
        endDayComboBox.setSelectedItem(initialDate.getDayOfMonth());
        endDayComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        endDayComboBox.setPreferredSize(new Dimension(70, 30));
        endDayComboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JComboBox<Integer> endMonthComboBox = createMonthComboBox();
        endMonthComboBox.setSelectedItem(initialDate.getMonthValue());
        endMonthComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        endMonthComboBox.setPreferredSize(new Dimension(70, 30));
        endMonthComboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JComboBox<Integer> endYearComboBox = createYearComboBox(initialDate);
        endYearComboBox.setSelectedItem(initialDate.getYear());
        endYearComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        endYearComboBox.setPreferredSize(new Dimension(100, 30));
        endYearComboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        gbcDate.gridx = 0;
        gbcDate.gridy = 0;
        endDatePanel.add(new JLabel("Day:"), gbcDate);
        gbcDate.gridx = 1;
        endDatePanel.add(endDayComboBox, gbcDate);
        gbcDate.gridx = 2;
        endDatePanel.add(new JLabel("Month:"), gbcDate);
        gbcDate.gridx = 3;
        endDatePanel.add(endMonthComboBox, gbcDate);
        gbcDate.gridx = 4;
        endDatePanel.add(new JLabel("Year:"), gbcDate);
        gbcDate.gridx = 5;
        endDatePanel.add(endYearComboBox, gbcDate);

        gbc.gridwidth = 2;
        gbc.gridy = 2;
        inputPanel.add(startDatePanel, gbc);
        gbc.gridy = 3;
        inputPanel.add(endDatePanel, gbc);

        int result = JOptionPane.showConfirmDialog(frame, inputPanel, "Calculate Payroll", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String selectedEmp = (String) empComboBox.getSelectedItem();
            if (selectedEmp == null) {
                JOptionPane.showMessageDialog(frame, "Please select an employee.");
                return;
            }
            String empID = selectedEmp.split(" - ")[0];
            int startDay = (Integer) startDayComboBox.getSelectedItem();
            int startMonth = (Integer) startMonthComboBox.getSelectedItem();
            int startYear = (Integer) startYearComboBox.getSelectedItem();
            int endDay = (Integer) endDayComboBox.getSelectedItem();
            int endMonth = (Integer) endMonthComboBox.getSelectedItem();
            int endYear = (Integer) endYearComboBox.getSelectedItem();

            LocalDate startDate = constructDateFromComponents(startDay, startMonth, startYear);
            LocalDate endDate = constructDateFromComponents(endDay, endMonth, endYear);
            if (startDate == null || endDate == null) {
                JOptionPane.showMessageDialog(frame, "Invalid date combination.");
                return;
            }
            if (endDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(frame, "End date cannot be before start date.");
                return;
            }
            displayPayrollResults(empID, startDate, endDate);
        }
    }

    private void displayPayrollResults(String empID, LocalDate startDate, LocalDate endDate) {
        Employee emp = employees.get(empID);
        if (emp == null) {
            JOptionPane.showMessageDialog(frame, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PayCalculator payCalc = employeeMapRates.get(empID);
        if (payCalc == null) {
            JOptionPane.showMessageDialog(frame, "Payroll data not available for this employee.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Simulate payroll calculation (replace with actual logic from PayCalculator)
        double hourlyRate = Double.parseDouble(emp.getHourlyRate());
        long daysWorked = startDate.datesUntil(endDate.plusDays(1)).count(); // Number of days
        double grossPay = daysWorked * 8 * hourlyRate; // Assuming 8 hours/day
        double totalDeductions = grossPay * 0.12; // Example: 12% deduction (e.g., taxes/SSS)
        double netPay = grossPay - totalDeductions;

        // Create a panel for payroll results
        JPanel payrollPanel = new JPanel(new BorderLayout(15, 15));
        payrollPanel.setBackground(new Color(245, 245, 245));
        payrollPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Payroll Results - ID: " + empID);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(60, 141, 188));
        payrollPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Employee Info
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(nameLabel, gbc);
        JLabel nameValue = new JLabel(emp.getFirstName() + " " + emp.getLastName());
        nameValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        contentPanel.add(nameValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel idLabel = new JLabel("ID:");
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        JLabel idValue = new JLabel(emp.getEmployeeID());
        idValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(idValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel periodLabel = new JLabel("Period:");
        periodLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(periodLabel, gbc);
        JLabel periodValue = new JLabel(startDate + " to " + endDate);
        periodValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        contentPanel.add(periodValue, gbc);

        // Payroll Details
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel grossPayLabel = new JLabel("Gross Pay:");
        grossPayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(grossPayLabel, gbc);
        gbc.gridx = 1;
        JLabel grossPayValue = new JLabel(String.format("PHP %.2f", grossPay));
        grossPayValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(grossPayValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel deductionsLabel = new JLabel("Total Deductions:");
        deductionsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(deductionsLabel, gbc);
        gbc.gridx = 1;
        JLabel deductionsValue = new JLabel(String.format("PHP %.2f", totalDeductions));
        deductionsValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(deductionsValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel netPayLabel = new JLabel("Net Pay:");
        netPayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(netPayLabel, gbc);
        gbc.gridx = 1;
        JLabel netPayValue = new JLabel(String.format("PHP %.2f", netPay));
        netPayValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(netPayValue, gbc);

        // Add a button to close the dialog
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new CustomButton("Close", null);
        closeButton.addActionListener(e -> ((JDialog) SwingUtilities.getWindowAncestor(payrollPanel)).dispose());
        buttonPanel.add(closeButton);
        payrollPanel.add(buttonPanel, BorderLayout.SOUTH);

        payrollPanel.add(contentPanel, BorderLayout.CENTER);

        // Display the panel in a dialog
        JDialog dialog = new JDialog(frame, "Payroll Results", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().add(payrollPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private String generateNextEmployeeID() {
        int maxID = 0;
        for (String id : employees.keySet()) {
            try {
                int num = Integer.parseInt(id);
                if (num > maxID) {
                    maxID = num;
                }
            } catch (NumberFormatException e) {
                // Ignore non-numeric IDs
            }
        }
        int nextID = maxID + 1;
        return String.format("%05d", nextID); // Assuming 5-digit IDs
    }

    private void addEmployee() {
        String nextID = generateNextEmployeeID();
        if (nextID == null) {
            JOptionPane.showMessageDialog(frame, "Unable to generate next employee ID.");
            return;
        }

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Employee ID (display only)
        JLabel idLabel = new JLabel("Employee ID:");
        JTextField idField = new JTextField(nextID, 10);
        idField.setEditable(false);

        // First Name
        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField(20);

        // Last Name
        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField(20);

        // Birthday
        JLabel birthdayLabel = new JLabel("Birthday (MM/DD/YYYY):");
        JTextField birthdayField = new JTextField(20);

        // Address
        JLabel addressLabel = new JLabel("Address:");
        JTextField addressField = new JTextField(30);

        // Phone Number
        JLabel phoneLabel = new JLabel("Phone Number:");
        JTextField phoneField = new JTextField(20);

        // SSS #
        JLabel sssLabel = new JLabel("SSS #:");
        JTextField sssField = new JTextField(20);

        // PhilHealth #
        JLabel philHealthLabel = new JLabel("PhilHealth #:");
        JTextField philHealthField = new JTextField(20);

        // TIN #
        JLabel tinLabel = new JLabel("TIN #:");
        JTextField tinField = new JTextField(20);

        // Pag-IBIG #
        JLabel pagIbigLabel = new JLabel("Pag-IBIG #:");
        JTextField pagIbigField = new JTextField(20);

        // Position
        JLabel positionLabel = new JLabel("Position:");
        JTextField positionField = new JTextField(20);

        // Immediate Supervisor
        JLabel supervisorLabel = new JLabel("Immediate Supervisor:");
        JTextField supervisorField = new JTextField(20);

        // Basic Salary
        JLabel basicSalaryLabel = new JLabel("Basic Salary:");
        JTextField basicSalaryField = new JTextField(20);

        // Rice Subsidy
        JLabel riceSubsidyLabel = new JLabel("Rice Subsidy:");
        JTextField riceSubsidyField = new JTextField(20);

        // Phone Allowance
        JLabel phoneAllowanceLabel = new JLabel("Phone Allowance:");
        JTextField phoneAllowanceField = new JTextField(20);

        // Clothing Allowance
        JLabel clothingAllowanceLabel = new JLabel("Clothing Allowance:");
        JTextField clothingAllowanceField = new JTextField(20);

        // Add components to panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(firstNameLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(firstNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(lastNameLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(lastNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(birthdayLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(birthdayField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(addressLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(addressField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        inputPanel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(phoneField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        inputPanel.add(sssLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(sssField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        inputPanel.add(philHealthLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(philHealthField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        inputPanel.add(tinLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(tinField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 9;
        inputPanel.add(pagIbigLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(pagIbigField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 10;
        inputPanel.add(positionLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(positionField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 11;
        inputPanel.add(supervisorLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(supervisorField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 12;
        inputPanel.add(basicSalaryLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(basicSalaryField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 13;
        inputPanel.add(riceSubsidyLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(riceSubsidyField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 14;
        inputPanel.add(phoneAllowanceLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(phoneAllowanceField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 15;
        inputPanel.add(clothingAllowanceLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(clothingAllowanceField, gbc);

        JScrollPane scrollPane = new JScrollPane(inputPanel);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        int result = JOptionPane.showConfirmDialog(frame, scrollPane, "Add New Employee", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String birthday = birthdayField.getText().trim();
            String address = addressField.getText().trim();
            String phone = phoneField.getText().trim();
            String sss = sssField.getText().trim();
            String philHealth = philHealthField.getText().trim();
            String tin = tinField.getText().trim();
            String pagIbig = pagIbigField.getText().trim();
            String position = positionField.getText().trim();
            String supervisor = supervisorField.getText().trim();
            String basicSalaryStr = basicSalaryField.getText().trim().replace(",", "");
            String riceSubsidyStr = riceSubsidyField.getText().trim().replace(",", "");
            String phoneAllowanceStr = phoneAllowanceField.getText().trim().replace(",", "");
            String clothingAllowanceStr = clothingAllowanceField.getText().trim().replace(",", "");

            if (firstName.isEmpty() || lastName.isEmpty() || birthday.isEmpty() || address.isEmpty() || phone.isEmpty() ||
                sss.isEmpty() || philHealth.isEmpty() || tin.isEmpty() || pagIbig.isEmpty() || position.isEmpty() ||
                supervisor.isEmpty() || basicSalaryStr.isEmpty() || riceSubsidyStr.isEmpty() || phoneAllowanceStr.isEmpty() ||
                clothingAllowanceStr.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
                return;
            }

            try {
                double basicSalary = Double.parseDouble(basicSalaryStr);
                int riceSubsidy = Integer.parseInt(riceSubsidyStr);
                int phoneAllowance = Integer.parseInt(phoneAllowanceStr);
                int clothingAllowance = Integer.parseInt(clothingAllowanceStr);

                double grossSemiMonthlyRate = basicSalary / 2;
                double hourlyRate = basicSalary / (22 * 8); // Assuming 22 working days, 8 hours

                Employee newEmployee = new Employee(nextID, lastName, firstName);
                newEmployee.setSSS(sss);
                newEmployee.setPhilHealth(philHealth);
                newEmployee.setTIN(tin);
                newEmployee.setPagIbig(pagIbig);
                newEmployee.setBasicSalary(basicSalaryStr);
                newEmployee.setRiceSubsidy(riceSubsidyStr);
                newEmployee.setPhoneAllowance(phoneAllowanceStr);
                newEmployee.setClothingAllowance(clothingAllowanceStr);
                newEmployee.setGrossSemiMonthlyRate(String.format("%.2f", grossSemiMonthlyRate));
                newEmployee.setHourlyRate(String.format("%.2f", hourlyRate));

                employees.put(nextID, newEmployee);

                // Update accounts.csv
                String username = nextID;
                String password = lastName.toLowerCase() + "pass"; // Default password
                java.util.List<String> accountData = Arrays.asList(username, password, "employee", nextID);
                try (CSVWriter writer = new CSVWriter(new FileWriter(getResourceFilePath(ACCOUNTS_CSV), true))) {
                    writer.writeNext(accountData.toArray(new String[0]));
                } catch (IOException e) {
                    System.err.println("Error writing to accounts.csv: " + e.getMessage());
                }

                // Update MotorPH_Employee_Data.csv
                java.util.List<String> empData = Arrays.asList(
                    nextID, lastName, firstName, birthday, address, phone, sss, philHealth, tin, pagIbig,
                    "Regular", position, supervisor, basicSalaryStr, riceSubsidyStr, phoneAllowanceStr,
                    clothingAllowanceStr, String.format("%.2f", grossSemiMonthlyRate), String.format("%.2f", hourlyRate)
                );
                try (CSVWriter writer = new CSVWriter(new FileWriter(getResourceFilePath(EMPLOYEE_DATA_CSV), true))) {
                    writer.writeNext(empData.toArray(new String[0]));
                } catch (IOException e) {
                    System.err.println("Error writing to MotorPH_Employee_Data.csv: " + e.getMessage());
                }

                // Update hourlyrate_allowances.csv
                java.util.List<String> allowanceData = Arrays.asList(
                    nextID, firstName + " " + lastName, riceSubsidyStr, phoneAllowanceStr, clothingAllowanceStr,
                    String.format("%.2f", hourlyRate), position
                );
                try (CSVWriter writer = new CSVWriter(new FileWriter(getResourceFilePath(HOURLY_RATE_ALLOWANCES_CSV), true))) {
                    writer.writeNext(allowanceData.toArray(new String[0]));
                } catch (IOException e) {
                    System.err.println("Error writing to hourlyrate_allowances.csv: " + e.getMessage());
                }

                // Update in-memory maps
                PayCalculator newPayCalc = new PayCalculator(riceSubsidy, phoneAllowance, clothingAllowance, hourlyRate);
                newPayCalc.setName(firstName + " " + lastName);
                newPayCalc.setPosition(position);
                employeeMapRates.put(nextID, newPayCalc);

                JOptionPane.showMessageDialog(frame, "Employee added successfully: " + firstName + " " + lastName);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid number format in salary or allowance fields.");
            }
        }
    }

    private String getResourceFilePath(String resourcePath) throws IOException {
        java.net.URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
        if (resourceUrl == null) {
            return new File(resourcePath).getAbsolutePath();
        }
        return new File(resourceUrl.getFile()).getAbsolutePath();
    }

    private void viewEmployeeByID(String empID) {
        Employee emp = employees.get(empID);
        if (emp == null) {
            JOptionPane.showMessageDialog(frame, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel detailsPanel = new JPanel(new BorderLayout(15, 15));
        detailsPanel.setBackground(new Color(245, 245, 245));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Employee Details - ID: " + empID);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(60, 141, 188));
        detailsPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel empDetailsHeader = new JLabel("=== Employee Details ===");
        empDetailsHeader.setFont(new AliasedLabelFont("Segoe UI", Font.BOLD, 16));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(empDetailsHeader, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        JLabel nameValue = new JLabel(emp.getFirstName() + " " + emp.getLastName());
        nameValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(nameValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel idLabel = new JLabel("ID:");
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        JLabel idValue = new JLabel(emp.getEmployeeID());
        idValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(idValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel sssLabel = new JLabel("SSS Number:");
        sssLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(sssLabel, gbc);
        gbc.gridx = 1;
        JLabel sssValue = new JLabel(emp.getSSS().isEmpty() ? "N/A" : emp.getSSS());
        sssValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(sssValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel philHealthLabel = new JLabel("PhilHealth Number:");
        philHealthLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(philHealthLabel, gbc);
        gbc.gridx = 1;
        JLabel philHealthValue = new JLabel(emp.getPhilHealth().isEmpty() ? "N/A" : emp.getPhilHealth());
        philHealthValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(philHealthValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel tinLabel = new JLabel("TIN Number:");
        tinLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(tinLabel, gbc);
        gbc.gridx = 1;
        JLabel tinValue = new JLabel(emp.getTIN().isEmpty() ? "N/A" : emp.getTIN());
        tinValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(tinValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel pagIbigLabel = new JLabel("Pag-IBIG Number:");
        pagIbigLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(pagIbigLabel, gbc);
        gbc.gridx = 1;
        JLabel pagIbigValue = new JLabel(emp.getPagIbig().isEmpty() ? "N/A" : emp.getPagIbig());
        pagIbigValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(pagIbigValue, gbc);

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel attHeader = new JLabel("=== Attendance Records ===");
        attHeader.setFont(new AliasedLabelFont("Segoe UI", Font.BOLD, 16));
        contentPanel.add(attHeader, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 8;
        if (emp.getAttendanceList().isEmpty()) {
            JLabel noAttLabel = new JLabel("No attendance records.");
            noAttLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            contentPanel.add(noAttLabel, gbc);
        } else {
            String[] columnNames = {"Date", "Log In", "Log Out"};
            Object[][] data = new Object[emp.getAttendanceList().size()][3];
            for (int i = 0; i < emp.getAttendanceList().size(); i++) {
                Attendance att = emp.getAttendanceList().get(i);
                data[i][0] = att.getDate();
                data[i][1] = att.getLogIn();
                data[i][2] = att.getLogOut();
            }
            JTable attTable = new JTable(data, columnNames);
            attTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            attTable.setRowHeight(25);
            attTable.setGridColor(new Color(220, 220, 220));
            attTable.setShowGrid(true);
            attTable.setBackground(Color.WHITE);
            attTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
            attTable.getTableHeader().setBackground(new Color(60, 141, 188));
            attTable.getTableHeader().setForeground(Color.WHITE);
            JScrollPane attScrollPane = new JScrollPane(attTable);
            attScrollPane.setPreferredSize(new Dimension(400, 200));
            gbc.gridx = 0;
            gbc.gridy = 8;
            gbc.gridwidth = 2;
            contentPanel.add(attScrollPane, gbc);
        }

        detailsPanel.add(contentPanel, BorderLayout.CENTER);
        JOptionPane.showMessageDialog(frame, detailsPanel, "Employee Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewPayrollByID(String empID) {
        Employee emp = employees.get(empID);
        if (emp == null) {
            JOptionPane.showMessageDialog(frame, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel payrollPanel = new JPanel(new BorderLayout(15, 15));
        payrollPanel.setBackground(new Color(245, 245, 245));
        payrollPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Payroll Details - ID: " + empID);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(60, 141, 188));
        payrollPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel payrollHeader = new JLabel("=== Payroll Details ===");
        payrollHeader.setFont(new AliasedLabelFont("Segoe UI", Font.BOLD, 16));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(payrollHeader, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        JLabel nameValue = new JLabel(emp.getFirstName() + " " + emp.getLastName());
        nameValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(nameValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel idLabel = new JLabel("ID:");
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        JLabel idValue = new JLabel(emp.getEmployeeID());
        idValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(idValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel basicSalaryLabel = new JLabel("Basic Salary:");
        basicSalaryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(basicSalaryLabel, gbc);
        gbc.gridx = 1;
        JLabel basicSalaryValue = new JLabel(emp.getBasicSalary().isEmpty() ? "N/A" : emp.getBasicSalary());
        basicSalaryValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(basicSalaryValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel riceSubsidyLabel = new JLabel("Rice Subsidy:");
        riceSubsidyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(riceSubsidyLabel, gbc);
        gbc.gridx = 1;
        JLabel riceSubsidyValue = new JLabel(emp.getRiceSubsidy().isEmpty() ? "N/A" : emp.getRiceSubsidy());
        riceSubsidyValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(riceSubsidyValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel phoneAllowanceLabel = new JLabel("Phone Allowance:");
        phoneAllowanceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(phoneAllowanceLabel, gbc);
        gbc.gridx = 1;
        JLabel phoneAllowanceValue = new JLabel(emp.getPhoneAllowance().isEmpty() ? "N/A" : emp.getPhoneAllowance());
        phoneAllowanceValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(phoneAllowanceValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel clothingAllowanceLabel = new JLabel("Clothing Allowance:");
        clothingAllowanceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(clothingAllowanceLabel, gbc);
        gbc.gridx = 1;
        JLabel clothingAllowanceValue = new JLabel(emp.getClothingAllowance().isEmpty() ? "N/A" : emp.getClothingAllowance());
        clothingAllowanceValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(clothingAllowanceValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel grossSemiMonthlyRateLabel = new JLabel("Gross Semi-monthly Rate:");
        grossSemiMonthlyRateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(grossSemiMonthlyRateLabel, gbc);
        gbc.gridx = 1;
        JLabel grossSemiMonthlyRateValue = new JLabel(emp.getGrossSemiMonthlyRate().isEmpty() ? "N/A" : emp.getGrossSemiMonthlyRate());
        grossSemiMonthlyRateValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(grossSemiMonthlyRateValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        JLabel hourlyRateLabel = new JLabel("Hourly Rate:");
        hourlyRateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(hourlyRateLabel, gbc);
        gbc.gridx = 1;
        JLabel hourlyRateValue = new JLabel(emp.getHourlyRate().isEmpty() ? "N/A" : emp.getHourlyRate());
        hourlyRateValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(hourlyRateValue, gbc);

        payrollPanel.add(contentPanel, BorderLayout.CENTER);
        JOptionPane.showMessageDialog(frame, payrollPanel, "Payroll Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewEmployeeSalary() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(245, 245, 245));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel empLabel = new JLabel("Select Employee:");
        empLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JComboBox<String> empComboBox = createEmployeeComboBox();
        empComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        empComboBox.setPreferredSize(new Dimension(300, 35));

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(empLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(empComboBox, gbc);

        int result = JOptionPane.showConfirmDialog(frame, inputPanel, "View Employee Salary", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String selectedEmp = (String) empComboBox.getSelectedItem();
            if (selectedEmp == null) {
                JOptionPane.showMessageDialog(frame, "Please select an employee.");
                return;
            }
            String empID = selectedEmp.split(" - ")[0];
            viewSalaryByID(empID);
        }
    }

    private void viewSalaryByID(String empID) {
        Salary salary = salaries.get(empID);
        if (salary == null) {
            JOptionPane.showMessageDialog(frame, "Salary information not found for ID: " + empID, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel salaryPanel = new JPanel(new BorderLayout(15, 15));
        salaryPanel.setBackground(new Color(245, 245, 245));
        salaryPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Salary Details - ID: " + empID);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(60, 141, 188));
        salaryPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel salaryHeader = new JLabel("=== Salary Details ===");
        salaryHeader.setFont(new AliasedLabelFont("Segoe UI", Font.BOLD, 16));
        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(salaryHeader, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        JLabel nameValue = new JLabel(salary.getEmployeeName());
        nameValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(nameValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel idLabel = new JLabel("ID:");
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        JLabel idValue = new JLabel(salary.getEmployeeID());
        idValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(idValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel positionLabel = new JLabel("Position:");
        positionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(positionLabel, gbc);
        gbc.gridx = 1;
        JLabel positionValue = new JLabel(salary.getPosition());
        positionValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(positionValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel cutoffLabel = new JLabel("Cutoff Period:");
        cutoffLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(cutoffLabel, gbc);
        gbc.gridx = 1;
        JLabel cutoffValue = new JLabel(salary.getCutoffPeriod());
        cutoffValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(cutoffValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel grossPayLabel = new JLabel("Gross Pay:");
        grossPayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(grossPayLabel, gbc);
        gbc.gridx = 1;
        JLabel grossPayValue = new JLabel(String.format("%.2f", salary.getGrossPay()));
        grossPayValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(grossPayValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel totalDeductionsLabel = new JLabel("Total Deductions:");
        totalDeductionsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(totalDeductionsLabel, gbc);
        gbc.gridx = 1;
        JLabel totalDeductionsValue = new JLabel(String.format("%.2f", salary.getTotalDeductions()));
        totalDeductionsValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(totalDeductionsValue, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        JLabel netPayLabel = new JLabel("Net Pay:");
        netPayLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(netPayLabel, gbc);
        gbc.gridx = 1;
        JLabel netPayValue = new JLabel(String.format("%.2f", salary.getNetPay()));
        netPayValue.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        contentPanel.add(netPayValue, gbc);

        salaryPanel.add(contentPanel, BorderLayout.CENTER);
        JOptionPane.showMessageDialog(frame, salaryPanel, "Salary Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateEmployee() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(245, 245, 245));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel empLabel = new JLabel("Select Employee:");
        empLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JComboBox<String> empComboBox = createEmployeeComboBox();
        empComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        empComboBox.setPreferredSize(new Dimension(300, 35));

        JLabel firstNameLabel = new JLabel("New First Name (optional):");
        firstNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JTextField firstNameField = new JTextField(20);
        firstNameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        firstNameField.setPreferredSize(new Dimension(250, 35));

        JLabel lastNameLabel = new JLabel("New Last Name (optional):");
        lastNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JTextField lastNameField = new JTextField(20);
        lastNameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lastNameField.setPreferredSize(new Dimension(250, 35));

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(empLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(empComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(firstNameLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(firstNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(lastNameLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(lastNameField, gbc);

        int result = JOptionPane.showConfirmDialog(frame, inputPanel, "Update Employee", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String selectedEmp = (String) empComboBox.getSelectedItem();
            if (selectedEmp == null) {
                JOptionPane.showMessageDialog(frame, "Please select an employee.");
                return;
            }
            String empID = selectedEmp.split(" - ")[0];
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();

            Employee emp = employees.get(empID);
            if (emp == null) {
                JOptionPane.showMessageDialog(frame, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Administrator admin = new Administrator();
            admin.modifyEmployee(emp, lastName, firstName);

            JOptionPane.showMessageDialog(frame, "Employee updated successfully: " + emp.getFirstName() + " " + emp.getLastName());
        }
    }

    private void deleteEmployee() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(245, 245, 245));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel empLabel = new JLabel("Select Employee:");
        empLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        JComboBox<String> empComboBox = createEmployeeComboBox();
        empComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        empComboBox.setPreferredSize(new Dimension(300, 35));

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(empLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(empComboBox, gbc);

        int result = JOptionPane.showConfirmDialog(frame, inputPanel, "Delete Employee", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String selectedEmp = (String) empComboBox.getSelectedItem();
            if (selectedEmp == null) {
                JOptionPane.showMessageDialog(frame, "Please select an employee.");
                return;
            }
            String empID = selectedEmp.split(" - ")[0];
            if (empID.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Employee ID cannot be empty.");
                return;
            }

            if (!employees.containsKey(empID)) {
                JOptionPane.showMessageDialog(frame, "Employee not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Administrator admin = new Administrator();
            admin.removeEmployee(employees, empID);

            // Remove from accounts.csv
            try {
                String accountsPath = getResourceFilePath(ACCOUNTS_CSV);
                java.util.List<String[]> accountsData = Files.readAllLines(Paths.get(accountsPath))
                    .stream()
                    .map(line -> line.split(","))
                    .filter(row -> !row[0].equals(empID)) // Filter out the deleted employee
                    .collect(Collectors.toList());
                try (CSVWriter writer = new CSVWriter(new FileWriter(accountsPath))) {
                    writer.writeAll(accountsData);
                }
            } catch (IOException e) {
                System.err.println("Error updating accounts.csv: " + e.getMessage());
            }

            // Remove from MotorPH_Employee_Data.csv
            try {
                String employeeDataPath = getResourceFilePath(EMPLOYEE_DATA_CSV);
                java.util.List<String[]> employeeData = Files.readAllLines(Paths.get(employeeDataPath))
                    .stream()
                    .map(line -> line.split(","))
                    .filter(row -> !row[0].equals(empID)) // Filter out the deleted employee
                    .collect(Collectors.toList());
                try (CSVWriter writer = new CSVWriter(new FileWriter(employeeDataPath))) {
                    writer.writeAll(employeeData);
                }
            } catch (IOException e) {
                System.err.println("Error updating MotorPH_Employee_Data.csv: " + e.getMessage());
            }

            // Remove from hourlyrate_allowances.csv
            try {
                String allowancesPath = getResourceFilePath(HOURLY_RATE_ALLOWANCES_CSV);
                java.util.List<String[]> allowancesData = Files.readAllLines(Paths.get(allowancesPath))
                    .stream()
                    .map(line -> line.split(","))
                    .filter(row -> !row[0].equals(empID)) // Filter out the deleted employee
                    .collect(Collectors.toList());
                try (CSVWriter writer = new CSVWriter(new FileWriter(allowancesPath))) {
                    writer.writeAll(allowancesData);
                }
            } catch (IOException e) {
                System.err.println("Error updating hourlyrate_allowances.csv: " + e.getMessage());
            }

            // Remove from in-memory maps
            employees.remove(empID);
            employeeMapRates.remove(empID);

            JOptionPane.showMessageDialog(frame, "Employee deleted successfully.");
        }
    }

    private void calculatePayrollForCurrentUser() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(frame, "No user logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String empID = currentUser.getEmployeeID();
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(245, 245, 245));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Start Date Panel
        JPanel startDatePanel = new JPanel(new GridBagLayout());
        startDatePanel.setBackground(new Color(245, 245, 245));
        startDatePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "Start Date"));
        GridBagConstraints gbcDate = new GridBagConstraints();
        gbcDate.insets = new Insets(5, 5, 5, 5);
        gbcDate.anchor = GridBagConstraints.WEST;

        LocalDate initialDate = LocalDate.now().withDayOfMonth(1);
        JComboBox<Integer> startDayComboBox = createDayComboBox();
        startDayComboBox.setSelectedItem(initialDate.getDayOfMonth());
        startDayComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        startDayComboBox.setPreferredSize(new Dimension(70, 30));
        startDayComboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JComboBox<Integer> startMonthComboBox = createMonthComboBox();
        startMonthComboBox.setSelectedItem(initialDate.getMonthValue());
        startMonthComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        startMonthComboBox.setPreferredSize(new Dimension(70, 30));
        startMonthComboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JComboBox<Integer> startYearComboBox = createYearComboBox(initialDate);
        startYearComboBox.setSelectedItem(initialDate.getYear());
        startYearComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        startYearComboBox.setPreferredSize(new Dimension(100, 30));
        startYearComboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        gbcDate.gridx = 0;
        gbcDate.gridy = 0;
        startDatePanel.add(new JLabel("Day:"), gbcDate);
        gbcDate.gridx = 1;
        startDatePanel.add(startDayComboBox, gbcDate);
        gbcDate.gridx = 2;
        startDatePanel.add(new JLabel("Month:"), gbcDate);
        gbcDate.gridx = 3;
        startDatePanel.add(startMonthComboBox, gbcDate);
        gbcDate.gridx = 4;
        startDatePanel.add(new JLabel("Year:"), gbcDate);
        gbcDate.gridx = 5;
        startDatePanel.add(startYearComboBox, gbcDate);

        // End Date Panel
        JPanel endDatePanel = new JPanel(new GridBagLayout());
        endDatePanel.setBackground(new Color(245, 245, 245));
        endDatePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "End Date"));
        JComboBox<Integer> endDayComboBox = createDayComboBox();
        endDayComboBox.setSelectedItem(initialDate.getDayOfMonth());
        endDayComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        endDayComboBox.setPreferredSize(new Dimension(70, 30));
        endDayComboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JComboBox<Integer> endMonthComboBox = createMonthComboBox();
        endMonthComboBox.setSelectedItem(initialDate.getMonthValue());
        endMonthComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        endMonthComboBox.setPreferredSize(new Dimension(70, 30));
        endMonthComboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JComboBox<Integer> endYearComboBox = createYearComboBox(initialDate);
        endYearComboBox.setSelectedItem(initialDate.getYear());
        endYearComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        endYearComboBox.setPreferredSize(new Dimension(100, 30));
        endYearComboBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        gbcDate.gridx = 0;
        gbcDate.gridy = 0;
        endDatePanel.add(new JLabel("Day:"), gbcDate);
        gbcDate.gridx = 1;
        endDatePanel.add(endDayComboBox, gbcDate);
        gbcDate.gridx = 2;
        endDatePanel.add(new JLabel("Month:"), gbcDate);
        gbcDate.gridx = 3;
        endDatePanel.add(endMonthComboBox, gbcDate);
        gbcDate.gridx = 4;
        endDatePanel.add(new JLabel("Year:"), gbcDate);
        gbcDate.gridx = 5;
        endDatePanel.add(endYearComboBox, gbcDate);

        gbc.gridwidth = 2;
        gbc.gridy = 0;
        inputPanel.add(startDatePanel, gbc);
        gbc.gridy = 1;
        inputPanel.add(endDatePanel, gbc);

        int result = JOptionPane.showConfirmDialog(frame, inputPanel, "Calculate My Payroll", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            int startDay = (Integer) startDayComboBox.getSelectedItem();
            int startMonth = (Integer) startMonthComboBox.getSelectedItem();
            int startYear = (Integer) startYearComboBox.getSelectedItem();
            int endDay = (Integer) endDayComboBox.getSelectedItem();
            int endMonth = (Integer) endMonthComboBox.getSelectedItem();
            int endYear = (Integer) endYearComboBox.getSelectedItem();

            LocalDate startDate = constructDateFromComponents(startDay, startMonth, startYear);
            LocalDate endDate = constructDateFromComponents(endDay, endMonth, endYear);
            if (startDate == null || endDate == null) {
                JOptionPane.showMessageDialog(frame, "Invalid date combination.");
                return;
            }
            if (endDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(frame, "End date cannot be before start date.");
                return;
            }
            displayPayrollResults(empID, startDate, endDate);
        }
    }
}
