package services;

import entities.Employee;
import entities.enums.EmployeeType;
import exceptions.*;
import lombok.extern.log4j.Log4j2;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

@Log4j2
public class ApplicationService {

    private final Scanner scanner = new Scanner(System.in);
    private final EmployeeService employeeService = new EmployeeService();
    private final Path PATH = Path.of("src/main/resources/data.xml");

    public void showListOfAllEmployees() {
        List<Employee> employees;

        try {
            employees = employeeService.getAllEmployees(PATH);
        } catch (IncorrectContentException | DamagedFileException e) {
            write("File with list is damaged");
            return;
        } catch (PathIsNullException | FileNotFoundException e) {
            write("File is not found");
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
            employeeService.addNewEmployers(Path.of(source), PATH);
        } catch (PathIsNullException | FileNotFoundException e) {
            write("File is not found");
            return;
        } catch (FileIsEmptyException e) {
            write("File is empty");
            return;
        } catch (IncorrectContentException | DamagedFileException e) {
            write("File with list is damaged");
            return;
        }

        write("");
        showListOfAllEmployees();
    }

    public void processChangeEmployeeType() {

        String id = requestDate("Enter the employee id:");
        String type = requestDate("""
                Enter the new type for Employee
                Available:
                    MANAGER
                    EMPLOYEE
                    OTHER_EMPLOYEE""");

        try {
            employeeService.changeEmployeeType(PATH, id,
                    EmployeeType.valueOf(EmployeeType.class, type));
        } catch (PathIsNullException | FileNotFoundException e) {
            write("File is not found");
            return;
        } catch (FileIsEmptyException e) {
            write("File is empty");
            return;
        } catch (IncorrectContentException | DamagedFileException e) {
            write("File with list is damaged");
            return;
        } catch (InvalidTypeException e) {
            write("Employee with such id is already has one type");
            return;
        }

        write("The employee type has been successfully changed");
    }

    public void processAssignEmployeeToManager() {
        String managerId = requestDate("Enter the manager id:");
        String employeeId = requestDate("Enter the employee id:");

        try {
            employeeService.assignEmployeeToManager(PATH, managerId, employeeId);
        } catch (PathIsNullException | FileNotFoundException e) {
            write("File is not found");
            return;
        } catch (FileIsEmptyException e) {
            write("File is empty");
            return;
        } catch (IncorrectContentException | DamagedFileException e) {
            write("File with list is damaged");
            return;
        } catch (InvalidTypeException e) {
            write("Employee with such id is not a Manager");
            return;
        }

        write("The employee has been successfully assigned to the manager");
    }

    public void processSortListByFullName() {
        try {
            employeeService.sortByFullName(PATH);
        } catch (PathIsNullException | FileNotFoundException e) {
            write("File is not found");
            return;
        } catch (FileIsEmptyException e) {
            write("File is empty");
            return;
        } catch (IncorrectContentException | DamagedFileException e) {
            write("File with list is damaged");
            return;
        }

        write("");
        showListOfAllEmployees();
    }

    public void processSortListByHiringDate() {
        try {
            employeeService.sortByHiringDate(PATH);
        } catch (PathIsNullException | FileNotFoundException e) {
            write("File is not found");
            return;
        } catch (FileIsEmptyException e) {
            write("File is empty");
            return;
        } catch (IncorrectContentException | DamagedFileException e) {
            write("File with list is damaged");
            return;
        }

        write("");
        showListOfAllEmployees();
    }

    public void processRemoveEmployeeById() {
        String id = requestDate("Enter the employee id:");

        try {
            employeeService.removeEmployerById(PATH, id);
        } catch (PathIsNullException | FileNotFoundException e) {
            write("File is not found");
            return;
        } catch (FileIsEmptyException e) {
            write("File is empty");
            return;
        } catch (IncorrectContentException | DamagedFileException e) {
            write("File with list is damaged");
            return;
        } catch (IllegalArgumentException e) {
            write("Id is not valid");
            return;
        }

        write("The employee has been successfully removed");
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
}
