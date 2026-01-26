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

public class AdminDashboard implements RoleDashboard {
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
        sidebar.add(app.createSidebarBtn("Add New Employee", e -> JOptionPane.showMessageDialog(null, "Add New Employee - Feature not implemented yet")));
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(app.createSidebarBtn("Update Employee", e -> JOptionPane.showMessageDialog(null, "Update Employee - Feature not implemented yet")));
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(app.createSidebarBtn("Delete Employee", e -> JOptionPane.showMessageDialog(null, "Delete Employee - Feature not implemented yet")));
    }
}
