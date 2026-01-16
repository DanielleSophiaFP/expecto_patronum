/*
 * Click nbproject://nbproject/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.motorphappgui;

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

/**
 * @author basil
 */

public class MotorPHGui {
    // File paths for CSV data files
    static final String DIR = "src/main/java/motorphappguidatabase/";
    static final String EMPLOYEE_DATA_CSV = DIR + "MotorPH_Employee_Data.csv";
    static final String EMPLOYEE_TIMEKEEPING_CSV = DIR + "employeeinfo_timekeeping.csv";
    static final String ACCOUNTS_CSV = DIR + "accounts.csv";
    static final String SALARY_CSV = DIR + "MotorPHPayslip.csv";
    static final String HOURLY_RATE_ALLOWANCES_CSV = DIR + "hourlyrate_allowances.csv";

    // Data structures
    protected Map<String, Employee> employees = new LinkedHashMap<>();
    protected Map<String, Salary> salaries = new LinkedHashMap<>();
    protected java.util.List<Account> accounts = new ArrayList<>();
    protected Map<String, PayCalculator> employeeMapRates = new HashMap<>();

    // Constants for payroll calculations
    private static final int GRACE_PERIOD_MINUTES = 10;

    // Inner classes
    public static class Salary {
        private String employeeID;
        private String employeeName;
        private String position;
        private String cutoffPeriod;
        private double grossPay;
        private double netPay;
        private double totalDeductions;

        public Salary(String employeeID, String employeeName, String position, String cutoffPeriod,
                      double grossPay, double netPay, double totalDeductions) {
            this.employeeID = employeeID;
            this.employeeName = employeeName;
            this.position = position;
            this.cutoffPeriod = cutoffPeriod;
            this.grossPay = grossPay;
            this.netPay = netPay;
            this.totalDeductions = totalDeductions;
        }

        public String getEmployeeID() { return employeeID; }
        public String getEmployeeName() { return employeeName; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
        public String getCutoffPeriod() { return cutoffPeriod; }
        public double getGrossPay() { return grossPay; }
        public double getNetPay() { return netPay; }
        public double getTotalDeductions() { return totalDeductions; }

        @Override
        public String toString() {
            return "Payslip for " + employeeName + " (" + employeeID + ")\n"
                    + "Position: " + position + "\n"
                    + "Cut-off Period: " + cutoffPeriod + "\n"
                    + "Gross Pay: " + grossPay + "\n"
                    + "Total Deductions: " + totalDeductions + "\n"
                    + "Net Pay: " + netPay;
        }
    }

    public static class Employee {
        private String employeeID;
        private String lastName;
        private String firstName;
        private java.util.List<Attendance> attendanceList = new ArrayList<>();
        private Salary salary;
        private String sss = "";
        private String philHealth = "";
        private String tin = "";
        private String pagIbig = "";
        private String basicSalary = "";
        private String riceSubsidy = "";
        private String phoneAllowance = "";
        private String clothingAllowance = "";
        private String grossSemiMonthlyRate = "";
        private String hourlyRate = "";

        public Employee(String employeeID, String lastName, String firstName) {
            this.employeeID = employeeID;
            this.lastName = lastName;
            this.firstName = firstName;
        }

        public String getEmployeeID() { return employeeID; }
        public String getLastName() { return lastName; }
        public String getFirstName() { return firstName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public java.util.List<Attendance> getAttendanceList() { return attendanceList; }
        public void addAttendance(Attendance att) { attendanceList.add(att); }
        public Salary getSalary() { return salary; }
        public void setSalary(Salary salary) { this.salary = salary; }
        public String getSSS() { return sss; }
        public void setSSS(String sss) { this.sss = sss; }
        public String getPhilHealth() { return philHealth; }
        public void setPhilHealth(String philHealth) { this.philHealth = philHealth; }
        public String getTIN() { return tin; }
        public void setTIN(String tin) { this.tin = tin; }
        public String getPagIbig() { return pagIbig; }
        public void setPagIbig(String pagIbig) { this.pagIbig = pagIbig; }
        public String getBasicSalary() { return basicSalary; }
        public void setBasicSalary(String basicSalary) { this.basicSalary = basicSalary; }
        public String getRiceSubsidy() { return riceSubsidy; }
        public void setRiceSubsidy(String riceSubsidy) { this.riceSubsidy = riceSubsidy; }
        public String getPhoneAllowance() { return phoneAllowance; }
        public void setPhoneAllowance(String phoneAllowance) { this.phoneAllowance = phoneAllowance; }
        public String getClothingAllowance() { return clothingAllowance; }
        public void setClothingAllowance(String clothingAllowance) { this.clothingAllowance = clothingAllowance; }
        public String getGrossSemiMonthlyRate() { return grossSemiMonthlyRate; }
        public void setGrossSemiMonthlyRate(String grossSemiMonthlyRate) { this.grossSemiMonthlyRate = grossSemiMonthlyRate; }
        public String getHourlyRate() { return hourlyRate; }
        public void setHourlyRate(String hourlyRate) { this.hourlyRate = hourlyRate; }

        @Override
        public String toString() {
            return employeeID + " | " + firstName + " " + lastName;
        }
    }

    public static class Attendance {
        private String date;
        private String logIn;
        private String logOut;

        public Attendance(String date, String logIn, String logOut) {
            this.date = date;
            this.logIn = logIn;
            this.logOut = logOut;
        }

        public String getDate() { return date; }
        public String getLogIn() { return logIn; }
        public String getLogOut() { return logOut; }

        @Override
        public String toString() {
            return date + " | Log In: " + logIn + " | Log Out: " + logOut;
        }
    }

    public static class Manager {
        public void addSalary(Map<String, Salary> salaries, Salary salary) {
            salaries.put(salary.getEmployeeID(), salary);
        }

        public void modifySalary(Salary salary, String newPosition) {
            if (newPosition != null && !newPosition.isEmpty()) {
                salary.setPosition(newPosition);
            }
        }
    }

    public static class Administrator {
        public Employee generateEmployee(String employeeID, String lastName, String firstName) {
            return new Employee(employeeID, lastName, firstName);
        }

        public void removeEmployee(Map<String, Employee> employees, String employeeID) {
            employees.remove(employeeID);
        }

        public void setAttendance(Employee emp, Attendance att) {
            emp.addAttendance(att);
        }

        public void modifyEmployee(Employee emp, String newLastName, String newFirstName) {
            if (newLastName != null && !newLastName.isEmpty()) {
                emp.setLastName(newLastName);
            }
            if (newFirstName != null && !newFirstName.isEmpty()) {
                emp.setFirstName(newFirstName);
            }
        }
    }

    public static class Account {
        private String username;
        private String password;
        private String role;
        private String employeeID;

        public Account(String username, String password, String role, String employeeID) {
            this.username = username;
            this.password = password;
            this.role = role;
            this.employeeID = employeeID;
        }

        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getRole() { return role; }
        public String getEmployeeID() { return employeeID; }

        public boolean authenticate(String password) {
            return this.password.equals(password);
        }
    }

    public static class PayCalculator {
        private int riceSubsidy;
        private int phoneAllowance;
        private int clothingAllowance;
        private double hourlyRate;
        private String name;
        private String position;

        public PayCalculator(int riceSubsidy, int phoneAllowance, int clothingAllowance, double hourlyRate) {
            this.riceSubsidy = riceSubsidy;
            this.phoneAllowance = phoneAllowance;
            this.clothingAllowance = clothingAllowance;
            this.hourlyRate = hourlyRate;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }
    }

    protected void loadAllData() {
        loadEmployees(EMPLOYEE_TIMEKEEPING_CSV);
        loadAdditionalEmployeeInfo(EMPLOYEE_DATA_CSV);
        loadSalaries();
        loadAccounts();
        loadEmployeeMapRates();

        for (Employee emp : employees.values()) {
            Salary sal = salaries.get(emp.getEmployeeID());
            if (sal != null) {
                emp.setSalary(sal);
            }
        }
    }

    protected String cleanField(String field) {
        if (field == null) return "";
        field = field.trim().replace("\uFEFF", "");
        if (field.startsWith("\"") && field.endsWith("\"") && field.length() > 1) {
            field = field.substring(1, field.length() - 1).trim();
        }
        return field;
    }

    protected String normalizeEmpID(String empID) {
        empID = cleanField(empID);
        empID = empID.replaceAll("\\D", "");
        empID = empID.replaceFirst("^0+(?!$)", "");
        return empID;
    }

    protected void loadEmployees(String filePath) {
        employees.clear();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            java.util.List<String[]> rows = reader.readAll();
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length < 3) continue;

                String empID = normalizeEmpID(row[0]);
                String lastName = cleanField(row[1]);
                String firstName = cleanField(row[2]);

                Employee emp = employees.get(empID);
                if (emp == null) {
                    emp = new Employee(empID, lastName, firstName);
                    employees.put(empID, emp);
                }

                if (row.length >= 6) {
                    String date = cleanField(row[3]);
                    String logIn = cleanField(row[4]);
                    String logOut = cleanField(row[5]);
                    if (!date.isEmpty() && !logIn.isEmpty() && !logOut.isEmpty()) {
                        emp.addAttendance(new Attendance(date, logIn, logOut));
                    }
                }
            }
        } catch (IOException | CsvException e) {
            System.err.println("Error loading employees: " + e.getMessage());
        }
    }

    protected void loadAdditionalEmployeeInfo(String filePath) {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            java.util.List<String[]> rows = reader.readAll();
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length < 19) continue;

                String empID = normalizeEmpID(row[0]);
                Employee emp = employees.get(empID);
                if (emp != null) {
                    emp.setSSS(cleanField(row[6]));
                    emp.setPhilHealth(cleanField(row[7]));
                    emp.setTIN(cleanField(row[8]));
                    emp.setPagIbig(cleanField(row[9]));
                    emp.setBasicSalary(cleanField(row[13]));
                    emp.setRiceSubsidy(cleanField(row[14]));
                    emp.setPhoneAllowance(cleanField(row[15]));
                    emp.setClothingAllowance(cleanField(row[16]));
                    emp.setGrossSemiMonthlyRate(cleanField(row[17]));
                    emp.setHourlyRate(cleanField(row[18]));
                }
            }
        } catch (IOException | CsvException e) {
            System.err.println("Error loading additional employee info: " + e.getMessage());
        }
    }

    protected void loadSalaries() {
        salaries.clear();
        try (CSVReader reader = new CSVReader(new FileReader(SALARY_CSV))) {
            java.util.List<String[]> rows = reader.readAll();
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length < 22) continue;

                String empID = cleanField(row[0]);
                String name = cleanField(row[1]);
                String position = cleanField(row[2]);
                String cutoff = cleanField(row[3]);
                double grossPay = parseDoubleSafe(row[15]);
                double totalDeductions = parseDoubleSafe(row[20]);
                double netPay = parseDoubleSafe(row[21]);

                Salary salary = new Salary(empID, name, position, cutoff, grossPay, netPay, totalDeductions);
                salaries.put(empID, salary);
            }
        } catch (IOException | CsvException e) {
            System.err.println("Error loading salaries: " + e.getMessage());
        }
    }

    protected void loadAccounts() {
        accounts.clear();
        try (CSVReader reader = new CSVReader(new FileReader(ACCOUNTS_CSV))) {
            java.util.List<String[]> rows = reader.readAll();
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length < 4) continue;

                Account acc = new Account(cleanField(row[0]), cleanField(row[1]), cleanField(row[2]), cleanField(row[3]));
                accounts.add(acc);
            }
        } catch (IOException | CsvException e) {
            System.err.println("Error loading accounts: " + e.getMessage());
        }
    }

    protected void loadEmployeeMapRates() {
        try (CSVReader reader = new CSVReader(new FileReader(HOURLY_RATE_ALLOWANCES_CSV))) {
            java.util.List<String[]> rows = reader.readAll();
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                if (row.length < 7) continue;

                String empID = normalizeEmpID(row[0]);
                String employeeName = cleanField(row[1]);

                try {
                    int riceSubsidy = Integer.parseInt(cleanField(row[2]));
                    int phoneAllowance = Integer.parseInt(cleanField(row[3]));
                    int clothingAllowance = Integer.parseInt(cleanField(row[4]));
                    double hourlyRate = Double.parseDouble(cleanField(row[5]));
                    String position = cleanField(row[6]);

                    PayCalculator empInfo = new PayCalculator(riceSubsidy, phoneAllowance, clothingAllowance, hourlyRate);
                    empInfo.setName(employeeName);
                    empInfo.setPosition(position);
                    employeeMapRates.put(empID, empInfo);
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing number in row: " + Arrays.toString(row));
                }
            }
        } catch (IOException | CsvException e) {
            System.err.println("Error loading employee rates: " + e.getMessage());
        }
    }

    protected double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(cleanField(value));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    protected double calculateWithholdingTax(double taxableIncome) {
        if (taxableIncome <= 20832) {
            return 0;
        } else if (taxableIncome <= 33333) {
            return (taxableIncome - 20833) * 0.20;
        } else if (taxableIncome <= 66667) {
            return 2500 + (taxableIncome - 33333) * 0.25;
        } else if (taxableIncome <= 166667) {
            return 10833 + (taxableIncome - 66667) * 0.30;
        } else if (taxableIncome <= 666667) {
            return 40833.33 + (taxableIncome - 166667) * 0.32;
        } else {
            return 200833.33 + (taxableIncome - 666667) * 0.35;
        }
    }

    protected double calculatePagibig(double grossPay) {
        double rate = (grossPay > 1500) ? 0.02 : 0.01;
        return Math.min(grossPay * rate, 100);
    }

    protected double calculatePhilhealth(double grossPay) {
        double rate = 0.03;
        double contribution = grossPay * rate;
        return Math.min(contribution / 2, 900);
    }

    protected double calculateSSS(double grossPay) {
        if (grossPay < 3250) {
            return 135.0;
        } else if (grossPay <= 3749.99) {
            return 157.5;
        } else if (grossPay <= 4249.99) {
            return 180.0;
        } else if (grossPay <= 4749.99) {
            return 202.5;
        } else if (grossPay <= 5249.99) {
            return 225.0;
        } else if (grossPay <= 5749.99) {
            return 247.5;
        } else if (grossPay <= 6249.99) {
            return 270.0;
        } else if (grossPay <= 6749.99) {
            return 292.5;
        } else if (grossPay <= 7249.99) {
            return 315.0;
        } else if (grossPay <= 7749.99) {
            return 337.5;
        } else if (grossPay <= 8249.99) {
            return 360.0;
        } else if (grossPay <= 8749.99) {
            return 382.5;
        } else if (grossPay <= 9249.99) {
            return 405.0;
        } else if (grossPay <= 9749.99) {
            return 427.5;
        } else if (grossPay <= 10249.99) {
            return 450.0;
        } else if (grossPay <= 10749.99) {
            return 472.5;
        } else if (grossPay <= 11249.99) {
            return 495.0;
        } else if (grossPay <= 11749.99) {
            return 517.5;
        } else if (grossPay <= 12249.99) {
            return 540.0;
        } else if (grossPay <= 12749.99) {
            return 562.5;
        } else if (grossPay <= 13249.99) {
            return 585.0;
        } else if (grossPay <= 13749.99) {
            return 607.5;
        } else if (grossPay <= 14249.99) {
            return 630.0;
        } else if (grossPay <= 14749.99) {
            return 652.5;
        } else if (grossPay <= 15249.99) {
            return 675.0;
        } else if (grossPay <= 15749.99) {
            return 697.5;
        } else if (grossPay <= 16249.99) {
            return 720.0;
        } else if (grossPay <= 16749.99) {
            return 742.5;
        } else if (grossPay <= 17249.99) {
            return 765.0;
        } else if (grossPay <= 17749.99) {
            return 787.5;
        } else if (grossPay <= 18249.99) {
            return 810.0;
        } else if (grossPay <= 18749.99) {
            return 832.5;
        } else if (grossPay <= 19249.99) {
            return 855.0;
        } else if (grossPay <= 19749.99) {
            return 877.5;
        } else if (grossPay <= 20249.99) {
            return 900.0;
        } else if (grossPay <= 20749.99) {
            return 922.5;
        } else if (grossPay <= 21249.99) {
            return 945.0;
        } else if (grossPay <= 21749.99) {
            return 967.5;
        } else if (grossPay <= 22249.99) {
            return 990.0;
        } else if (grossPay <= 22749.99) {
            return 1012.5;
        } else if (grossPay <= 23249.99) {
            return 1035.0;
        } else if (grossPay <= 23749.99) {
            return 1057.5;
        } else if (grossPay <= 24249.99) {
            return 1080.0;
        } else if (grossPay <= 24749.99) {
            return 1102.5;
        } else {
            return 1125.0;
        }
    }

    protected void computeAndDisplayPayroll(String employeeID, LocalDate startDate, LocalDate endDate, JTextArea outputArea) {
        PayCalculator empInfo = employeeMapRates.get(employeeID);
        if (empInfo == null) {
            outputArea.setText("Employee ID not found in employee data.");
            return;
        }

        double hourlyRate = empInfo.hourlyRate;
        int riceSubsidy = empInfo.riceSubsidy;
        int phoneAllowance = empInfo.phoneAllowance;
        int clothingAllowance = empInfo.clothingAllowance;
        String employeeName = empInfo.getName();
        String position = empInfo.getPosition();
        double totalWorkedHours = 0;
        double totalOvertimeHours = 0;
        double totalTardinessHours = 0;
        int daysLate = 0;

        try (CSVReader reader = new CSVReader(new FileReader(EMPLOYEE_TIMEKEEPING_CSV))) {
            java.util.List<String[]> rows = reader.readAll();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (int i = 1; i < rows.size(); i++) {
                String[] data = rows.get(i);
                if (data.length < 6) continue;

                String id = normalizeEmpID(data[0]);
                LocalDate recordDate = LocalDate.parse(cleanField(data[3]), dateFormatter);

                if (id.equals(employeeID) && !recordDate.isBefore(startDate) && !recordDate.isAfter(endDate)) {
                    LocalTime scheduledTimeIn = LocalTime.parse("08:00", timeFormatter);
                    LocalTime timeIn = LocalTime.parse(cleanField(data[4]), timeFormatter);
                    LocalTime timeOut = LocalTime.parse(cleanField(data[5]), timeFormatter);

                    Duration workDuration = Duration.between(timeIn, timeOut);
                    double dailyWorkedHours = workDuration.toMinutes() / 60.0;

                    Duration tardiness = Duration.between(scheduledTimeIn, timeIn);
                    if (tardiness.toMinutes() > GRACE_PERIOD_MINUTES && timeIn.isAfter(scheduledTimeIn)) {
                        totalTardinessHours += (tardiness.toMinutes() - GRACE_PERIOD_MINUTES) / 60.0;
                        daysLate++;
                    }

                    double regularHours = Math.min(dailyWorkedHours, 8);
                    double overtimeHours = Math.max(dailyWorkedHours - 8, 0);

                    totalWorkedHours += regularHours;
                    totalOvertimeHours += overtimeHours;
                }
            }
        } catch (IOException | CsvException e) {
            outputArea.setText("Error reading timekeeping file: " + e.getMessage());
            return;
        }

        double overtimeRate = 1.25;
        double regularPay = totalWorkedHours * hourlyRate;
        double overtimePay = totalOvertimeHours * hourlyRate * overtimeRate;
        double totalAllowances = riceSubsidy + phoneAllowance + clothingAllowance;
        double grossPay = regularPay + overtimePay + totalAllowances;

        boolean isFirstCutoff = startDate.getDayOfMonth() <= 15;
        double sssDeduction = isFirstCutoff ? calculateSSS(grossPay) : 0;
        double philhealthDeduction = isFirstCutoff ? calculatePhilhealth(grossPay) : 0;
        double pagibigDeduction = isFirstCutoff ? calculatePagibig(grossPay) : 0;

        double taxableIncome = grossPay - (sssDeduction + philhealthDeduction + pagibigDeduction);
        double withholdingTax = calculateWithholdingTax(taxableIncome);
        double adjustedWithholdingTax = withholdingTax / 2.0;
        double tardinessDeduction = totalTardinessHours * hourlyRate;
        double totalDeductions = sssDeduction + philhealthDeduction + pagibigDeduction + adjustedWithholdingTax + tardinessDeduction;
        double netPay = grossPay - totalDeductions;

        String payslip = String.format(
                "\n======================================================\n"
                        + "               MOTORPH PAYROLL STATEMENT              \n"
                        + "======================================================\n"
                        + "  Employee: %-36s\n"
                        + "  ID: %-41s\n"
                        + "  Position: %-37s\n"
                        + "  Period: %-38s\n"
                        + "  Hourly Rate: P%.2f\n"
                        + "  Days Late: %d\n"
                        + "------------------------------------------------------\n"
                        + "  Description             Hours          Amount\n"
                        + "------------------------------------------------------\n"
                        + "  Basic Pay               %7.2f       P%11.2f\n"
                        + "  Overtime (1.25x rate)   %7.2f       P%11.2f\n"
                        + "  Tardiness               -%6.2f      -P%11.2f\n"
                        + "  Rice Subsidy            %7s       P%11.2f\n"
                        + "  Phone Allowance         %7s       P%11.2f\n"
                        + "  Clothing Allowance      %7s       P%11.2f\n"
                        + "------------------------------------------------------\n"
                        + "  GROSS PAY                            P%12.2f\n"
                        + "------------------------------------------------------\n"
                        + "  SSS Deduction                        -P%11.2f\n"
                        + "  PhilHealth                           -P%11.2f\n"
                        + "  Pag-IBIG                             -P%11.2f\n"
                        + "  Withholding Tax                      -P%11.2f\n"
                        + "------------------------------------------------------\n"
                        + "  TOTAL DEDUCTIONS                   -P%13.2f\n"
                        + "======================================================\n"
                        + "  NET PAY:                            P%13.2f\n"
                        + "======================================================\n",
                employeeName,
                employeeID,
                position,
                startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " to " + endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                hourlyRate,
                daysLate,
                totalWorkedHours, regularPay,
                totalOvertimeHours, overtimePay,
                totalTardinessHours, tardinessDeduction,
                "", (double) riceSubsidy,
                "", (double) phoneAllowance,
                "", (double) clothingAllowance,
                grossPay,
                sssDeduction,
                philhealthDeduction,
                pagibigDeduction,
                adjustedWithholdingTax,
                totalDeductions,
                netPay
        );

        outputArea.setText(payslip);

        java.util.List<String> csvData = Arrays.asList(
                employeeID, employeeName, position,
                startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " to " + endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                String.valueOf(totalWorkedHours), String.valueOf(totalTardinessHours), String.valueOf(-tardinessDeduction), String.valueOf(daysLate),
                String.valueOf(hourlyRate), String.valueOf(regularPay), String.valueOf(overtimePay),
                String.valueOf(riceSubsidy), String.valueOf(phoneAllowance), String.valueOf(clothingAllowance), String.valueOf(totalAllowances),
                String.valueOf(grossPay), String.valueOf(sssDeduction), String.valueOf(philhealthDeduction), String.valueOf(pagibigDeduction), String.valueOf(withholdingTax),
                String.valueOf(totalDeductions), String.valueOf(netPay)
        );

        try (CSVWriter writer = new CSVWriter(new FileWriter(SALARY_CSV, true))) {
            writer.writeNext(csvData.toArray(new String[0]));
        } catch (IOException e) {
            outputArea.setText("Error writing to CSV file: " + e.getMessage());
        }
    }
}
