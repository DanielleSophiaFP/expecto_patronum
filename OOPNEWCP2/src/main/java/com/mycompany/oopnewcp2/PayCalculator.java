/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopnewcp2;

/**
 *
 * @author Ej
 */
public class PayCalculator {
    private final String employeeID;
    private final String employeeName;
    private final String position;
    private final double hourlyRate;
    private final double riceSubsidy;
    private final double phoneAllowance;
    private final double clothingAllowance;

    public PayCalculator(String employeeID, String employeeName, String position,
                         double hourlyRate, double riceSubsidy, double phoneAllowance, double clothingAllowance) {
        this.employeeID = employeeID;
        this.employeeName = employeeName;
        this.position = position;
        this.hourlyRate = hourlyRate;
        this.riceSubsidy = riceSubsidy;
        this.phoneAllowance = phoneAllowance;
        this.clothingAllowance = clothingAllowance;
    }

    public String getEmployeeID() { return employeeID; }
    public String getEmployeeName() { return employeeName; }
    public String getPosition() { return position; }
    public double getHourlyRate() { return hourlyRate; }
    public double getRiceSubsidy() { return riceSubsidy; }
    public double getPhoneAllowance() { return phoneAllowance; }
    public double getClothingAllowance() { return clothingAllowance; }
    public double getTotalAllowances() { return riceSubsidy + phoneAllowance + clothingAllowance; }
}
