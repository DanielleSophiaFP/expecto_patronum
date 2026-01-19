/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopnewcp2;

/**
 *
 * @author Ej
 */
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Attendance {
    private final LocalDate date;
    private final LocalTime logIn;
    private final LocalTime logOut;

    public Attendance(String dateStr, String logInStr, String logOutStr) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        this.date = LocalDate.parse(dateStr, dateFormatter);
        this.logIn = LocalTime.parse(logInStr, timeFormatter);
        this.logOut = LocalTime.parse(logOutStr, timeFormatter);
    }

    public LocalDate getDate() { return date; }
    public LocalTime getLogIn() { return logIn; }
    public LocalTime getLogOut() { return logOut; }

    public double getWorkedHours() {
        return java.time.Duration.between(logIn, logOut).toMinutes() / 60.0;
    }
}