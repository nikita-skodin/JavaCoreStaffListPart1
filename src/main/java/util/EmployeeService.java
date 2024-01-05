package util;

import entities.Employee;
import entities.Manager;
import entities.OtherEmployee;
import entities.enums.EmployeeType;
import exceptions.InvalidTypeException;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class EmployeeService {

    private final EmployeeReader employeeReader = new EmployeeReader();
    private final EmployeeWriter employeeWriter = new EmployeeWriter();

    @SneakyThrows
    public void addNewEmployers(Path source, Path target) {

        if (target == null) {
            throw new IllegalArgumentException("target file cannot be null");
        }

        List<Employee> sourceList = employeeReader.readXML(source);

        if (!Files.exists(target)) {
            Files.createFile(target);
        }

        if (Files.size(target) == 0L) {
            Files.writeString(target, """
                    <?xml version="1.0" encoding="UTF-8" standalone="no"?>
                    <employees>
                    </employees>
                    """);
        }

        List<Employee> targetList = employeeReader.readXML(target);

        targetList.addAll(sourceList);

        employeeWriter.writeXML(target, targetList);
    }

    public boolean removeEmployerById(Path source, UUID uuid) {

        pathValidate(source);

        List<Employee> sourceList = employeeReader.readXML(source);

        boolean isRemoved = sourceList.removeIf(employee -> employee.getId().equals(uuid));

        if (!isRemoved) {
            return false;
        }

        employeeWriter.writeXML(source, sourceList);

        return true;
    }

    public boolean removeEmployerByFullName(Path source, String fullName) {

        pathValidate(source);

        List<Employee> sourceList = employeeReader.readXML(source);

        boolean isRemoved = sourceList.removeIf(employee -> employee.getFullName().equals(fullName));

        if (!isRemoved) {
            return false;
        }

        employeeWriter.writeXML(source, sourceList);

        return true;
    }

    // удаляешь старого и получаешь его, на основе его создаешь нового и добавляешь в список
    public boolean changeEmployeeType(Path source, UUID uuid, EmployeeType employeeType) {

        pathValidate(source);

        List<Employee> sourceList = employeeReader.readXML(source);

        Employee employee = sourceList.stream().filter(e -> e.getId().equals(uuid)).findFirst().orElse(null);

        if (employee == null) {
            return false;
        }

        switch (employeeType) {
            case EMPLOYEE -> {
                if (employee.getClass().equals(Employee.class)) {
                    throw new InvalidTypeException("Employee type is already Employee");
                }

                sourceList.remove(employee);
                sourceList.add(new Employee(
                        employee.getId(),
                        employee.getFullName(),
                        employee.getBirthdayDate(),
                        employee.getHiringDate()
                ));

            }
            case OTHER_EMPLOYEE -> {
                if (employee.getClass().equals(OtherEmployee.class)) {
                    throw new InvalidTypeException("Employee type is already OtherEmployee");
                }
                // TODO change description
                sourceList.remove(employee);
                sourceList.add(new OtherEmployee(
                        employee, "DESCRIPTION"
                ));
            }
            case MANAGER -> {
                if (employee.getClass().equals(Manager.class)) {
                    throw new InvalidTypeException("Employee type is already Manager");
                }
                sourceList.remove(employee);
                sourceList.add(new Manager(employee, new ArrayList<>()));
            }

        }
        employeeWriter.writeXML(source, sourceList);
        return true;
    }

    public boolean linkEmployeeToManager(Path source, UUID managerId, UUID employeeId) {

        pathValidate(source);

        List<Employee> list = employeeReader.readXML(source);

        Employee manager = list.stream().filter(e -> e.getId().equals(managerId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("the manager does not exist"));
        Employee employee = list.stream().filter(e -> e.getId().equals(employeeId))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("the employee does not exist"));

        if (!manager.getClass().equals(Manager.class)){
            throw new InvalidTypeException("Employee type is not a manager");
        }

        ((Manager) manager).getSubordinates().add(employee);

        employeeWriter.writeXML(source, list);

        return true;
    }

    public void sortByFullName(Path source) {
        pathValidate(source);
        List<Employee> sourceList = employeeReader.readXML(source);
        sourceList.sort(Comparator.comparing(Employee::getFullName));
        employeeWriter.writeXML(source, sourceList);
    }

    public void sortByHiringDate(Path source) {
        pathValidate(source);
        List<Employee> sourceList = employeeReader.readXML(source);
        sourceList.sort(Comparator.comparing(Employee::getHiringDate));
        employeeWriter.writeXML(source, sourceList);
    }

    // public for tests
    @SneakyThrows
    public void pathValidate(Path path) {

        if (path == null) {
            throw new IllegalArgumentException("path cannot be null");
        }

        if (!Files.exists(path)) {
            throw new NoSuchFileException(path.toString());
        }

        if (Files.size(path) == 0L) {
            throw new IllegalArgumentException("File cannot be null");
        }

        employeeReader.checkIsFileDamaged(path);

    }

}
