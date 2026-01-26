/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopnewcp2;

/**
 *
 * @author Ej
 */
public abstract class Person {
    private final String employeeID;
    private String lastName;
    private String firstName;

    public Person(String employeeID, String lastName, String firstName) {
        this.employeeID = employeeID;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public String getEmployeeID() { return employeeID; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getFullName() { return firstName + " " + lastName; }
}
