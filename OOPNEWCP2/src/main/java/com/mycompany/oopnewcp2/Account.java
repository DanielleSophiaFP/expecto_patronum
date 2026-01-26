package com.mycompany.oopnewcp2;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Ej
 */
public class Account {
    private final String username;
    private final String password;
    private final String role;
    private final String employeeID;

    public Account(String username, String password, String role, String employeeID) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.employeeID = "0".equals(employeeID) ? null : employeeID;
    }

    public String getUsername() { return username; }
    public String getRole() { return role; }
    public String getEmployeeID() { return employeeID; }

    public boolean authenticate(String inputPassword) {
        return this.password.equals(inputPassword);
    }
}
