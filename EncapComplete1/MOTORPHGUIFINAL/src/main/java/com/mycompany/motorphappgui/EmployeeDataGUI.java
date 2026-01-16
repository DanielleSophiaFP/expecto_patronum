/*
 * Click nbproject://nbproject/Templates/Licenses/license-default.txt to edit this template
 */
package com.mycompany.motorphappgui;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author basil
 */
public class EmployeeDataGUI extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private List<String[]> employeeData;
    private List<String[]> payslipData;
    private Map<String, String[]> employeeDetailsMap;

    public EmployeeDataGUI() {
        // Initialize data
        employeeData = new ArrayList<>();
        payslipData = new ArrayList<>();
        employeeDetailsMap = new HashMap<>();
        loadCSVData();

        // Set up the frame
        setTitle("MotorPH Employee Data");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 650));
        getContentPane().setBackground(new Color(245, 245, 245));

        // Create main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(245, 245, 245));
        JLabel titleLabel = new JLabel("Employee Data Overview");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(60, 141, 188));
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Search panel
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel searchLabel = new JLabel("Search by ID or Name:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setPreferredSize(new Dimension(300, 30));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JButton searchButton = new CustomButton("Search", null);
        JButton resetButton = new CustomButton("Reset", null);

        gbc.gridx = 0;
        gbc.gridy = 0;
        searchPanel.add(searchLabel, gbc);
        gbc.gridx = 1;
        searchPanel.add(searchField, gbc);
        gbc.gridx = 2;
        searchPanel.add(searchButton, gbc);
        gbc.gridx = 3;
        searchPanel.add(resetButton, gbc);

        // Set up table
        String[] columnNames = {"Employee ID", "Name", "Position", "Hourly Rate", "Rice Subsidy", "Phone Allowance", "Clothing Allowance"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(60, 141, 188));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setRowHeight(35);
        table.setGridColor(new Color(220, 220, 220));
        table.setShowGrid(true);
        table.setBackground(Color.WHITE);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Set preferred column widths to prevent truncation
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100); // Employee ID
        columnModel.getColumn(1).setPreferredWidth(200); // Name
        columnModel.getColumn(2).setPreferredWidth(150); // Position
        columnModel.getColumn(3).setPreferredWidth(100); // Hourly Rate
        columnModel.getColumn(4).setPreferredWidth(100); // Rice Subsidy
        columnModel.getColumn(5).setPreferredWidth(120); // Phone Allowance
        columnModel.getColumn(6).setPreferredWidth(120); // Clothing Allowance

        populateTable(employeeData);

        // Customize scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Add components to main panel
        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add main panel to frame
        add(mainPanel);

        // Action listeners
        searchButton.addActionListener((ActionEvent e) -> searchEmployees());
        resetButton.addActionListener((ActionEvent e) -> resetTable());

        // Double-click to show payslip details
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        String empId = (String) table.getValueAt(row, 0);
                        showPayslipDetails(empId);
                    }
                }
            }
        });
    }

    private void loadCSVData() {
        // Use relative path based on project structure
        String basePath = "src/main/java/motorphappguidatabase/";

        // Load hourlyrate_allowances.csv
        try (CSVReader reader = new CSVReader(new FileReader(basePath + "hourlyrate_allowances.csv"))) {
            String[] header = reader.readNext(); // Skip header
            if (header != null) {
                String[] line;
                while ((line = reader.readNext()) != null) {
                    employeeData.add(line);
                }
            }
        } catch (IOException | CsvValidationException e) {
            JOptionPane.showMessageDialog(this, "Error loading hourlyrate_allowances.csv: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Load MotorPH_Employee_Data.csv for additional details
        try (CSVReader reader = new CSVReader(new FileReader(basePath + "MotorPH_Employee_Data.csv"))) {
            String[] header = reader.readNext(); // Skip header
            if (header != null) {
                String[] line;
                while ((line = reader.readNext()) != null) {
                    employeeDetailsMap.put(line[0], line); // Key: Employee #
                }
            }
        } catch (IOException | CsvValidationException e) {
            JOptionPane.showMessageDialog(this, "Error loading MotorPH_Employee_Data.csv: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Load MotorPHPayslip.csv
        try (CSVReader reader = new CSVReader(new FileReader(basePath + "MotorPHPayslip.csv"))) {
            String[] header = reader.readNext(); // Skip header
            if (header != null) {
                String[] line;
                while ((line = reader.readNext()) != null) {
                    payslipData.add(line);
                }
            }
        } catch (IOException | CsvValidationException e) {
            JOptionPane.showMessageDialog(this, "Error loading MotorPHPayslip.csv: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTable(List<String[]> data) {
        tableModel.setRowCount(0); // Clear table
        for (String[] row : data) {
            if (row.length < 7) { // Ensure at least 7 fields
                // Use defaults for missing fields
                String[] paddedRow = new String[7];
                System.arraycopy(row, 0, paddedRow, 0, Math.min(row.length, 7));
                for (int i = row.length; i < 7; i++) {
                    paddedRow[i] = "N/A";
                }
                row = paddedRow;
            }
            tableModel.addRow(new Object[]{
                row[0], // Employee #
                row[1], // Employee Name
                row[6], // Position
                row[5], // Hourly Rate
                row[2], // Rice Subsidy
                row[3], // Phone Allowance
                row[4]  // Clothing Allowance
            });
        }
    }

    private void searchEmployees() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            populateTable(employeeData);
            return;
        }

        List<String[]> filteredData = new ArrayList<>();
        for (String[] row : employeeData) {
            if (row[0].toLowerCase().contains(query) || row[1].toLowerCase().contains(query)) {
                filteredData.add(row);
            }
        }
        populateTable(filteredData);
    }

    private void resetTable() {
        searchField.setText("");
        populateTable(employeeData);
    }

    private void showPayslipDetails(String empId) {
        JPanel detailsPanel = new JPanel(new BorderLayout(10, 10));
        detailsPanel.setBackground(new Color(245, 245, 245));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(Color.WHITE);
        textArea.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        StringBuilder details = new StringBuilder();
        boolean payslipFound = false;

        // Payslip section
        details.append("=== Payslip Details ===\n\n");
        for (String[] payslip : payslipData) {
            if (payslip[0].equals(empId)) {
                payslipFound = true;
                details.append("Employee: ").append(payslip[1]).append("\n")
                       .append("Position: ").append(payslip[2]).append("\n")
                       .append("Cut-off Period: ").append(payslip[3]).append("\n")
                       .append("Total Worked Hours: ").append(payslip[4]).append("\n")
                       .append("Gross Pay: ").append(payslip[16]).append("\n")
                       .append("Total Deductions: ").append(payslip[21]).append("\n")
                       .append("Net Pay: ").append(payslip[22]).append("\n");
                break;
            }
        }

        if (!payslipFound) {
            details.append("No payslip data found for Employee ID: ").append(empId).append("\n");
        }

        // Additional details section
        if (employeeDetailsMap.containsKey(empId)) {
            String[] empDetails = employeeDetailsMap.get(empId);
            details.append("\n=== Additional Details ===\n\n")
                   .append("Employee ID: ").append(empId).append("\n")
                   .append("Birthday: ").append(empDetails[3]).append("\n")
                   .append("Address: ").append(empDetails[4]).append("\n")
                   .append("Phone Number: ").append(empDetails[5]).append("\n")
                   .append("SSS #: ").append(empDetails[6]).append("\n")
                   .append("PhilHealth #: ").append(empDetails[7]).append("\n")
                   .append("TIN #: ").append(empDetails[8]).append("\n")
                   .append("Pag-IBIG #: ").append(empDetails[9]).append("\n")
                   .append("Status: ").append(empDetails[10]).append("\n")
                   .append("Immediate Supervisor: ").append(empDetails[12]).append("\n");
        }

        textArea.setText(details.toString());
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        detailsPanel.add(scrollPane, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, detailsPanel, "Employee Details - ID: " + empId, JOptionPane.INFORMATION_MESSAGE);
    }

    // Custom button class for styled buttons
    private static class CustomButton extends JButton {
        public CustomButton(String text, ImageIcon icon) {
            super(text);
            if (icon != null) setIcon(icon);
            setFont(new Font("Segoe UI", Font.PLAIN, 16));
            setBackground(new Color(60, 141, 188));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            setOpaque(true);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(120, 40));

            // Hover effect
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(new Color(52, 122, 163));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(new Color(60, 141, 188));
                }
            });
        }
    }
}
