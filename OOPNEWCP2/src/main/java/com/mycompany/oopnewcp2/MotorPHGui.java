/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.oopnewcp2;

/**
 *
 * @author Ej
 */
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.swing.JTextArea;

public class MotorPHGui {
    protected static final String DIR = "src/main/java/motorphappguidatabase/";
    protected static final String EMPLOYEE_DATA_CSV = DIR + "MotorPH_Employee_Data.csv";
    protected static final String EMPLOYEE_TIMEKEEPING_CSV = DIR + "employeeinfo_timekeeping.csv";
    protected static final String ACCOUNTS_CSV = DIR + "accounts.csv";
    protected static final String SALARY_CSV = DIR + "MotorPHPayslip.csv";
    protected static final String HOURLY_RATE_ALLOWANCES_CSV = DIR + "hourlyrate_allowances.csv";

    protected final Map<String, Employee> employees = new LinkedHashMap<>();
    protected final List<Account> accounts = new ArrayList<>();
    protected final Map<String, PayCalculator> payCalculators = new HashMap<>();

    private static final LocalTime SCHEDULED_IN = LocalTime.of(9, 0);
    private static final int GRACE_PERIOD_MINUTES = 10;
    private static final double OVERTIME_RATE = 1.25;
    private static final int REGULAR_HOURS_PER_DAY = 8;

    // Deduction implementations (placeholders)
    private static class SSSDeduction implements Deduction {
        @Override
        public double calculate(double grossPay, boolean isFirstCutoff) {
            return isFirstCutoff ? grossPay * 0.045 : 0;
        }
    }

    private static class PhilHealthDeduction implements Deduction {
        @Override
        public double calculate(double grossPay, boolean isFirstCutoff) {
            return isFirstCutoff ? grossPay * 0.045 / 2 : 0;
        }
    }

    private static class PagIBIGDeduction implements Deduction {
        @Override
        public double calculate(double grossPay, boolean isFirstCutoff) {
            return isFirstCutoff ? 100.0 : 0;
        }
    }

    private static class WithholdingTaxDeduction implements Deduction {
        @Override
        public double calculate(double grossPay, boolean isFirstCutoff) {
            return 0;
        }
    }

    private final List<Deduction> deductions = List.of(
        new SSSDeduction(),
        new PhilHealthDeduction(),
        new PagIBIGDeduction(),
        new WithholdingTaxDeduction()
    );

    protected void loadAllData() {
        loadAccounts();
        loadEmployeeDetails();
        loadPayRatesAndAllowances();
        loadTimekeeping();
    }

    private void loadAccounts() {
        try (CSVReader reader = new CSVReader(new FileReader(ACCOUNTS_CSV))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                String employeeID = line.length > 3 ? line[3] : "0";
                accounts.add(new Account(line[0], line[1], line[2], employeeID));
            }
        } catch (IOException | CsvException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
    }

    private void loadEmployeeDetails() {
        try (CSVReader reader = new CSVReader(new FileReader(EMPLOYEE_DATA_CSV))) {
            reader.readNext(); // skip header
            String[] line;
            while ((line = reader.readNext()) != null) {
                String empID = line[0].trim();
                final String lastName = line[1].trim();
                final String firstName = line[2].trim();

                Employee emp = employees.computeIfAbsent(empID, id -> new Employee(id, lastName, firstName));
                emp.setBirthday(line[3]);
                emp.setAddress(line[4]);
                emp.setPhoneNumber(line[5]);
                emp.setSss(line[6]);
                emp.setPhilHealth(line[7]);
                emp.setTin(line[8]);
                emp.setPagIbig(line[9]);
                emp.setStatus(line[10]);
                emp.setImmediateSupervisor(line[12]);
            }
        } catch (IOException | CsvException e) {
            System.err.println("Error loading employee details: " + e.getMessage());
        }
    }

    private void loadPayRatesAndAllowances() {
        try (CSVReader reader = new CSVReader(new FileReader(HOURLY_RATE_ALLOWANCES_CSV))) {
            reader.readNext(); // skip header
            String[] line;
            while ((line = reader.readNext()) != null) {
                String empID = line[0].trim();
                String name = line[1].trim();
                double rice = Double.parseDouble(line[2]);
                double phone = Double.parseDouble(line[3]);
                double clothing = Double.parseDouble(line[4]);
                double hourly = Double.parseDouble(line[5]);
                String position = line[6].trim();
                payCalculators.put(empID, new PayCalculator(empID, name, position, hourly, rice, phone, clothing));
            }
        } catch (IOException | CsvException e) {
            System.err.println("Error loading rates: " + e.getMessage());
        }
    }

    private void loadTimekeeping() {
        try (CSVReader reader = new CSVReader(new FileReader(EMPLOYEE_TIMEKEEPING_CSV))) {
            reader.readNext(); // skip header
            String[] line;
            while ((line = reader.readNext()) != null) {
                String empID = line[0].replace("\"", "").trim();
                final String lastName = line[1].replace("\"", "").trim();
                final String firstName = line[2].replace("\"", "").trim();

                Employee emp = employees.computeIfAbsent(empID, id -> new Employee(id, lastName, firstName));
                String date = line[3].replace("\"", "").trim();
                String logIn = line[4].replace("\"", "").trim();
                String logOut = line[5].replace("\"", "").trim();
                emp.addAttendance(new Attendance(date, logIn, logOut));
            }
        } catch (IOException | CsvException e) {
            System.err.println("Error loading timekeeping: " + e.getMessage());
        }
    }

    protected void calculateAndDisplayPayroll(String empID, LocalDate startDate, LocalDate endDate, JTextArea outputArea) {
        PayCalculator pc = payCalculators.get(empID);
        Employee emp = employees.get(empID);
        if (pc == null || emp == null) {
            outputArea.setText("Employee data not found for ID: " + empID);
            return;
        }

        double totalRegularHours = 0;
        double totalOvertimeHours = 0;
        double totalTardinessHours = 0;
        int daysLate = 0;

        for (Attendance att : emp.getAttendanceList()) {
            LocalDate date = att.getDate();
            if (!date.isBefore(startDate) && !date.isAfter(endDate)) {
                double dailyHours = att.getWorkedHours();
                totalRegularHours += Math.min(dailyHours, REGULAR_HOURS_PER_DAY);
                totalOvertimeHours += Math.max(dailyHours - REGULAR_HOURS_PER_DAY, 0);

                LocalTime logIn = att.getLogIn();
                if (logIn.isAfter(SCHEDULED_IN)) {
                    long tardyMinutes = Duration.between(SCHEDULED_IN, logIn).toMinutes();
                    if (tardyMinutes > GRACE_PERIOD_MINUTES) {
                        totalTardinessHours += (tardyMinutes - GRACE_PERIOD_MINUTES) / 60.0;
                        daysLate++;
                    }
                }
            }
        }

        double regularPay = totalRegularHours * pc.getHourlyRate();
        double overtimePay = totalOvertimeHours * pc.getHourlyRate() * OVERTIME_RATE;
        double allowances = pc.getTotalAllowances();
        double grossPay = regularPay + overtimePay + allowances;

        boolean isFirstCutoff = startDate.getDayOfMonth() <= 15;

        double statutoryDeductions = 0;
        for (Deduction d : deductions) {
            statutoryDeductions += d.calculate(grossPay, isFirstCutoff);
        }

        double tardinessDeduction = totalTardinessHours * pc.getHourlyRate();
        double totalDeductions = statutoryDeductions + tardinessDeduction;
        double netPay = grossPay - totalDeductions;

        String payslip = String.format(
            "\n======================================================\n" +
            "               MOTORPH PAYROLL STATEMENT              \n" +
            "======================================================\n" +
            "  Employee: %-36s ID: %s\n" +
            "  Position: %-37s\n" +
            "  Period: %s to %s\n" +
            "  Hourly Rate: P%.2f    Days Late: %d\n" +
            "------------------------------------------------------\n" +
            "  Description             Hours          Amount\n" +
            "------------------------------------------------------\n" +
            "  Basic Pay               %7.2f       P%11.2f\n" +
            "  Overtime                %7.2f       P%11.2f\n" +
            "  Tardiness              -%7.2f      -P%11.2f\n" +
            "  Rice Subsidy                          P%11.2f\n" +
            "  Phone Allowance                       P%11.2f\n" +
            "  Clothing Allowance                    P%11.2f\n" +
            "------------------------------------------------------\n" +
            "  GROSS PAY                            P%12.2f\n" +
            "  TOTAL DEDUCTIONS                    -P%12.2f\n" +
            "======================================================\n" +
            "  NET PAY                             P%13.2f\n" +
            "======================================================\n",
            pc.getEmployeeName(), empID, pc.getPosition(),
            startDate, endDate, pc.getHourlyRate(), daysLate,
            totalRegularHours, regularPay,
            totalOvertimeHours, overtimePay,
            totalTardinessHours, tardinessDeduction,
            pc.getRiceSubsidy(), pc.getPhoneAllowance(), pc.getClothingAllowance(),
            grossPay, totalDeductions, netPay
        );

        outputArea.setText(payslip);

        try (CSVWriter writer = new CSVWriter(new FileWriter(SALARY_CSV, true))) {
            String[] record = new String[]{
                empID, pc.getEmployeeName(), pc.getPosition(),
                startDate + " to " + endDate,
                String.valueOf(totalRegularHours + totalOvertimeHours),
                String.valueOf(totalTardinessHours),
                String.valueOf(-tardinessDeduction),
                String.valueOf(daysLate),
                String.valueOf(pc.getHourlyRate()),
                String.valueOf(regularPay),
                String.valueOf(overtimePay),
                String.valueOf(pc.getRiceSubsidy()),
                String.valueOf(pc.getPhoneAllowance()),
                String.valueOf(pc.getClothingAllowance()),
                String.valueOf(allowances),
                String.valueOf(grossPay),
                "0","0","0","0",
                String.valueOf(totalDeductions),
                String.valueOf(netPay)
            };
            writer.writeNext(record);
        } catch (IOException e) {
            outputArea.append("\nError saving payslip: " + e.getMessage());
        }
    }

    // === GETTERS (these were missing in the previous version - now added back) ===
    public Map<String, PayCalculator> getPayCalculators() {
        return payCalculators;
    }

    public Map<String, Employee> getEmployees() {
        return employees;
    }

    public List<Account> getAccounts() {
        return accounts;
    }
}