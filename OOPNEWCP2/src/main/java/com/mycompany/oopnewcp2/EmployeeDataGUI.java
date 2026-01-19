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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;

public class EmployeeDataGUI extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private final MotorPHGui dataSource;

    public EmployeeDataGUI(MotorPHGui dataSource) {
        this.dataSource = dataSource;
        initComponents();
        populateTable();
    }

    private void initComponents() {
        setTitle("MotorPH Employee Data Overview");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 650));
        getContentPane().setBackground(new Color(245, 245, 245));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header
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

        JLabel searchLabel = new JLabel("Search by ID or Name:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField = new JTextField(30);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        searchField.setPreferredSize(new Dimension(350, 35));

        JButton searchButton = new CustomButton("Search", null);
        JButton resetButton = new CustomButton("Reset", null);

        searchButton.addActionListener(e -> searchEmployees());
        resetButton.addActionListener(e -> resetTable());

        gbc.gridx = 0; gbc.gridy = 0; searchPanel.add(searchLabel, gbc);
        gbc.gridx = 1; searchPanel.add(searchField, gbc);
        gbc.gridx = 2; searchPanel.add(searchButton, gbc);
        gbc.gridx = 3; searchPanel.add(resetButton, gbc);

        mainPanel.add(searchPanel, BorderLayout.CENTER);

        // Table
        String[] columns = {"Employee ID", "Name", "Position", "Hourly Rate", "Rice Subsidy", "Phone Allowance", "Clothing Allowance"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        styleTable();

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = table.rowAtPoint(evt.getPoint());
                if (row >= 0 && evt.getClickCount() == 2) {
                    String empID = (String) tableModel.getValueAt(row, 0);
                    showEmployeeDetails(empID);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void styleTable() {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(60, 141, 188));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setRowHeight(35);
        table.setGridColor(new Color(220, 220, 220));
        table.setShowGrid(true);
        table.setBackground(Color.WHITE);

        TableColumnModel colModel = table.getColumnModel();
        colModel.getColumn(0).setPreferredWidth(100);
        colModel.getColumn(1).setPreferredWidth(220);
        colModel.getColumn(2).setPreferredWidth(200);
        colModel.getColumn(3).setPreferredWidth(100);
        colModel.getColumn(4).setPreferredWidth(120);
        colModel.getColumn(5).setPreferredWidth(130);
        colModel.getColumn(6).setPreferredWidth(140);
    }

    private void populateTable() {
        tableModel.setRowCount(0);
        for (PayCalculator pc : dataSource.getPayCalculators().values()) {
            tableModel.addRow(new Object[]{
                pc.getEmployeeID(),
                pc.getEmployeeName(),
                pc.getPosition(),
                String.format("%.2f", pc.getHourlyRate()),
                String.format("%.2f", pc.getRiceSubsidy()),
                String.format("%.2f", pc.getPhoneAllowance()),
                String.format("%.2f", pc.getClothingAllowance())
            });
        }
    }

    private void searchEmployees() {
        String query = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        for (PayCalculator pc : dataSource.getPayCalculators().values()) {
            if (pc.getEmployeeID().toLowerCase().contains(query) || pc.getEmployeeName().toLowerCase().contains(query)) {
                tableModel.addRow(new Object[]{
                    pc.getEmployeeID(), pc.getEmployeeName(), pc.getPosition(),
                    String.format("%.2f", pc.getHourlyRate()),
                    String.format("%.2f", pc.getRiceSubsidy()),
                    String.format("%.2f", pc.getPhoneAllowance()),
                    String.format("%.2f", pc.getClothingAllowance())
                });
            }
        }
    }

    private void resetTable() {
        searchField.setText("");
        populateTable();
    }

    private void showEmployeeDetails(String empID) {
        Employee emp = dataSource.getEmployees().get(empID);
        PayCalculator pc = dataSource.getPayCalculators().get(empID);
        if (emp == null || pc == null) {
            JOptionPane.showMessageDialog(this, "Details not found for ID: " + empID);
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== Employee Details ===\n\n");
        sb.append("ID: ").append(empID).append("\n");
        sb.append("Name: ").append(pc.getEmployeeName()).append("\n");
        sb.append("Position: ").append(pc.getPosition()).append("\n");
        sb.append("Birthday: ").append(emp.getBirthday()).append("\n");
        sb.append("Address: ").append(emp.getAddress()).append("\n");
        sb.append("Phone: ").append(emp.getPhoneNumber()).append("\n");
        sb.append("SSS: ").append(emp.getSss()).append("\n");
        sb.append("PhilHealth: ").append(emp.getPhilHealth()).append("\n");
        sb.append("TIN: ").append(emp.getTin()).append("\n");
        sb.append("Pag-IBIG: ").append(emp.getPagIbig()).append("\n");
        sb.append("Status: ").append(emp.getStatus()).append("\n");
        sb.append("Supervisor: ").append(emp.getImmediateSupervisor()).append("\n\n");
        sb.append("Hourly Rate: P").append(String.format("%.2f", pc.getHourlyRate())).append("\n");
        sb.append("Allowances: Rice P").append(pc.getRiceSubsidy())
          .append(", Phone P").append(pc.getPhoneAllowance())
          .append(", Clothing P").append(pc.getClothingAllowance()).append("\n");

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(600, 500));

        JOptionPane.showMessageDialog(this, scroll, "Details - " + empID, JOptionPane.INFORMATION_MESSAGE);
    }

    private static class CustomButton extends JButton {
        public CustomButton(String text, Icon icon) {
            super(text, icon);
            setFont(new Font("Segoe UI", Font.PLAIN, 16));
            setBackground(new Color(60, 141, 188));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { setBackground(new Color(52, 122, 163)); }
                public void mouseExited(java.awt.event.MouseEvent e) { setBackground(new Color(60, 141, 188)); }
            });
        }
    }
}
