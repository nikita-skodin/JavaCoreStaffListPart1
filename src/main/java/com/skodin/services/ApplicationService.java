package com.skodin.services;

import com.skodin.entities.Employee;
import com.skodin.entities.enums.EmployeeType;
import com.skodin.exceptions.InvalidInputDataExceptions;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

@Log4j2
public class ApplicationService {

    private final Scanner scanner = new Scanner(System.in);
    private final EmployeeService employeeService = new EmployeeService();
    private final Path PATH = Path.of("src/main/resources/data.xml");

    {
        if (!Files.exists(PATH)) {
            try {
                Files.createFile(PATH);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void showListOfAllEmployees() {
        List<Employee> employees;

        try {
            employees = employeeService.getAllEmployees(PATH);
        } catch (InvalidInputDataExceptions e) {
            write(e.getMessage());
            return;
        }

        if (employees.isEmpty()) {
            write("List is empty");
            return;
        }

        write(employees);
    }

    public void processAddNewEmployee() {
        String source = requestDate("Enter the path to the employee file:");

        try {
            boolean isSuccessfully = employeeService.addNewEmployers(Path.of(source), PATH);
            write("");
            write("Employee has been added",
                    "Employee is already exist",
                    isSuccessfully);
        } catch (InvalidInputDataExceptions e) {
            write(e.getMessage());
        }
    }

    public void processChangeEmployeeType() {

        String id = requestDate("Enter the employee id:");
        String type = requestDate("""
                Enter the new type for Employee
                Available:
                %s""".formatted(EmployeeType.getPretty()));

        String description = "";
        if (type.equals(EmployeeType.OTHER_EMPLOYEE.toString())) {
            description = requestDate("Enter the employee description:");
        }

        try {
            boolean isChanged = employeeService.changeEmployeeType(PATH, id,
                    type, description);
            write("The employee type has been successfully changed",
                    "There is no such employee",
                    isChanged);
        } catch (InvalidInputDataExceptions e) {
            write(e.getMessage());
        }

    }

    public void processAssignEmployeeToManager() {
        String managerId = requestDate("Enter the manager id:");
        String employeeId = requestDate("Enter the employee id:");

        try {
            boolean isAssigned = employeeService.assignEmployeeToManager(PATH, managerId, employeeId);
            write("The employee has been successfully assigned to the manager",
                    "The employee is already assigned to the manager",
                    isAssigned);
        } catch (InvalidInputDataExceptions e) {
            write(e.getMessage());
        }
    }

    public void processRemoveEmployeeById() {
        String id = requestDate("Enter the employee id:");

        try {
            boolean isRemoved = employeeService.removeEmployerById(PATH, id);
            write("The employee has been successfully removed",
                    "There is no employee with such id",
                    isRemoved);
        } catch (InvalidInputDataExceptions e) {
            write(e.getMessage());
        }

    }

    public void processSortListByFullName() {
        try {
            employeeService.sortByFullName(PATH);
        } catch (InvalidInputDataExceptions e) {
            write(e.getMessage());
            return;
        }

        write("");
        showListOfAllEmployees();
    }

    public void processSortListByHiringDate() {
        try {
            employeeService.sortByHiringDate(PATH);
        } catch (InvalidInputDataExceptions e) {
            write(e.getMessage());
            return;
        }

        write("");
        showListOfAllEmployees();
    }

    private String requestDate(String message) {
        write(message);
        return scan();
    }

    private String scan() {
        return scanner.nextLine();
    }

    private void write(Object o) {
        System.out.println(o);
    }

    private void write(List<Employee> list) {
        for (Employee employee : list) {
            System.out.println(employee.toString() + "\n");
        }
    }

    private void write(String trueMessage, String falseMessage, boolean condition) {
        if (condition) {
            write(trueMessage);
        } else {
            write(falseMessage);
        }
    }
}
