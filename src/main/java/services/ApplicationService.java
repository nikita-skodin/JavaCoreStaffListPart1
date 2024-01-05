package services;

import entities.enums.EmployeeType;

import java.nio.file.Path;
import java.util.Scanner;
import java.util.UUID;

public class ApplicationService {

    private final Scanner scanner = new Scanner(System.in);
    private final EmployeeService employeeService = new EmployeeService();

    private final Path PATH = Path.of("src/test/resources/xml/employees.xml");

    public void showListOfAllEmployees() {
        write(employeeService.getAllEmployees(PATH).toString());
    }

    public void processAddNewEmployee() {
        String source = requestDate("Введите путь до файла с сотрудниками");

        String target = requestDate("Введите путь до файла с сотрудника (target)");

        employeeService.addNewEmployers(Path.of(source), Path.of(target));

        write("");
        showListOfAllEmployees();
    }

    public void processChangeEmployeeType() {
        String source = requestDate("Введите путь до файла с сотрудниками");

        String id = requestDate("Введите id целевого сотрудника (target)");

        String type = requestDate("Введите новый тип целевого сотрудника (target)");

        employeeService.changeEmployeeType(Path.of(source), UUID.fromString(id),
                EmployeeType.valueOf(EmployeeType.class, type));

        write("Тип сотрудника успешно изменен");
    }

    public void processAssignEmployeeToManager() {
        String source = requestDate("Введите путь до файла с сотрудниками");

        String managerId = requestDate("Введите id менеджера");

        String employeeId = requestDate("Введите id сотрудника");

        employeeService.assignEmployeeToManager(Path.of(source), UUID.fromString(managerId), UUID.fromString(employeeId));

        write("операция выполнена успешно");
    }

    public void processSortListByFullName() {
        String source = requestDate("Введите путь до файла с сотрудниками");

        employeeService.sortByFullName(Path.of(source));

        write("");
        showListOfAllEmployees();
    }

    public void processSortListByHiringDate() {
        String source = requestDate("Введите путь до файла с сотрудниками");

        employeeService.sortByHiringDate(Path.of(source));

        write("");
        showListOfAllEmployees();
    }

    private String requestDate(String message) {
        write(message);
        return scan();
    }

    private void write(Object o) {
        System.out.println(o);
    }

    private String scan() {
        return scanner.nextLine();
    }
}
