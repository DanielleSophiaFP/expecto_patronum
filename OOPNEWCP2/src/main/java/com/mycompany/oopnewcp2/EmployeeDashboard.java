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

public class EmployeeDashboard implements RoleDashboard {
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
