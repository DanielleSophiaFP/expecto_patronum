/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopnewcp2;

/**
 *
 * @author Ej
 */
import java.util.ArrayList;
import java.util.List;

public class Employee extends Person {
    private final List<Attendance> attendanceList = new ArrayList<>();
    private String birthday;
    private String address;
    private String phoneNumber;
    private String sss;
    private String philHealth;
    private String tin;
    private String pagIbig;
    private String status;
    private String immediateSupervisor;

    public Employee(String employeeID, String lastName, String firstName) {
        super(employeeID, lastName, firstName);
    }

    public List<Attendance> getAttendanceList() { return attendanceList; }
    public void addAttendance(Attendance attendance) { attendanceList.add(attendance); }

    // Additional details from MotorPH_Employee_Data.csv
    public String getBirthday() { return birthday; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getSss() { return sss; }
    public void setSss(String sss) { this.sss = sss; }
    public String getPhilHealth() { return philHealth; }
    public void setPhilHealth(String philHealth) { this.philHealth = philHealth; }
    public String getTin() { return tin; }
    public void setTin(String tin) { this.tin = tin; }
    public String getPagIbig() { return pagIbig; }
    public void setPagIbig(String pagIbig) { this.pagIbig = pagIbig; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getImmediateSupervisor() { return immediateSupervisor; }
    public void setImmediateSupervisor(String immediateSupervisor) { this.immediateSupervisor = immediateSupervisor; }
}
