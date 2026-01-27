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
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.util.List;

public class EmployeeDataGUI extends JFrame {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private final MotorPHGui dataSource;

    // Modern Color Palette
    private static final Color PRIMARY_COLOR = new Color(45, 118, 168);
    private static final Color LIGHT_BG = new Color(249, 250, 251);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(31, 41, 55);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);
    private static final Color HOVER_COLOR = new Color(240, 245, 250);

    public EmployeeDataGUI(MotorPHGui dataSource) {
        this.dataSource = dataSource;
        initComponents();
        populateTable();
    }

    private void initComponents() {
        setTitle("MotorPH Employee Data Overview");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 650));
        getContentPane().setBackground(LIGHT_BG);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        mainPanel.setBackground(LIGHT_BG);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(LIGHT_BG);
        JLabel titleLabel = new JLabel("Employee Data Overview");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_COLOR);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JLabel infoLabel = new JLabel("Total Employees: " + dataSource.getPayCalculators().size());
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoLabel.setForeground(TEXT_SECONDARY);
        headerPanel.add(infoLabel, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        searchPanel.setBackground(CARD_BG);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(TEXT_PRIMARY);
        searchField = new JTextField(35);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.setBackground(LIGHT_BG);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        JButton searchButton = new ModernButton("Search", PRIMARY_COLOR, new Color(35, 92, 138), Color.WHITE);
        JButton resetButton = new ModernButton("Reset", new Color(107, 114, 128), new Color(87, 94, 108), Color.WHITE);

        searchButton.addActionListener(e -> searchEmployees());
        resetButton.addActionListener(e -> resetTable());

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(resetButton);

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
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(CARD_BG);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void styleTable() {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        // Fixed: Use setPreferredSize for header height instead of setHeight
        table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 40));
        table.setRowHeight(35);
        table.setGridColor(BORDER_COLOR);
        table.setShowGrid(true);
        table.setBackground(CARD_BG);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setDefaultRenderer(Object.class, new ModernTableCellRenderer());

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
        sb.append("═══════════════════════════════════════════════════════��═══════\n");
        sb.append("                     EMPLOYEE DETAILS\n");
        sb.append("═══════════════════════════════════════════════════════════════\n\n");
        sb.append("PERSONAL INFORMATION\n");
        sb.append("───────────────────────────────────────────────────────────────\n");
        sb.append(String.format("%-25s: %s\n", "ID", empID));
        sb.append(String.format("%-25s: %s\n", "Name", pc.getEmployeeName()));
        sb.append(String.format("%-25s: %s\n", "Position", pc.getPosition()));
        sb.append(String.format("%-25s: %s\n", "Birthday", emp.getBirthday()));
        sb.append(String.format("%-25s: %s\n", "Address", emp.getAddress()));
        sb.append(String.format("%-25s: %s\n", "Phone", emp.getPhoneNumber()));
        sb.append(String.format("%-25s: %s\n", "Status", emp.getStatus()));
        sb.append(String.format("%-25s: %s\n\n", "Supervisor", emp.getImmediateSupervisor()));
        
        sb.append("GOVERNMENT IDs\n");
        sb.append("───────────────────────────────────────────────────────────────\n");
        sb.append(String.format("%-25s: %s\n", "SSS", emp.getSss()));
        sb.append(String.format("%-25s: %s\n", "PhilHealth", emp.getPhilHealth()));
        sb.append(String.format("%-25s: %s\n", "TIN", emp.getTin()));
        sb.append(String.format("%-25s: %s\n\n", "Pag-IBIG", emp.getPagIbig()));
        
        sb.append("COMPENSATION\n");
        sb.append("───────────────────────────────────────────────────────────────\n");
        sb.append(String.format("%-25s: P%.2f\n", "Hourly Rate", pc.getHourlyRate()));
        sb.append(String.format("%-25s: P%.2f\n", "Rice Subsidy", pc.getRiceSubsidy()));
        sb.append(String.format("%-25s: P%.2f\n", "Phone Allowance", pc.getPhoneAllowance()));
        sb.append(String.format("%-25s: P%.2f\n", "Clothing Allowance", pc.getClothingAllowance()));
        sb.append("═══════════════════════════════════════════════════════════════\n");

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(LIGHT_BG);
        textArea.setForeground(TEXT_PRIMARY);
        textArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scroll.setPreferredSize(new Dimension(700, 500));

        JOptionPane.showMessageDialog(this, scroll, "Employee Details - " + empID, JOptionPane.INFORMATION_MESSAGE);
    }

    // Modern Table Cell Renderer
    private static class ModernTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? CARD_BG : LIGHT_BG);
                c.setForeground(TEXT_PRIMARY);
            }
            return c;
        }
    }

    private static class ModernButton extends JButton {
        private boolean isHovered = false;
        private final Color baseColor;
        private final Color hoverColor;

        public ModernButton(String text, Color baseColor, Color hoverColor, Color textColor) {
            super(text);
            this.baseColor = baseColor;
            this.hoverColor = hoverColor;
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setForeground(textColor);
            setBackground(baseColor);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(100, 40));

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    isHovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(isHovered ? hoverColor : baseColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            super.paintComponent(g);
        }
    }
}