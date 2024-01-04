package util;

import entities.Employee;
import entities.Manager;
import entities.enums.EmployeeType;
import exceptions.InvalidTypeException;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.*;

public class EmployeeService {

    private final EmployeeReader employeeReader = new EmployeeReader();
    private final EmployeeWriter employeeWriter = new EmployeeWriter();

    @SneakyThrows
    public void addNewEmployers(Path source, Path target) {

        if (target == null){
            throw new IllegalArgumentException("target file cannot be null");
        }

        List<Employee> sourceList = employeeReader.readXML(source);

        if (!Files.exists(target)){
            Files.createFile(target);
        }

        if (Files.size(target) == 0L){
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

    public void removeEmployerById(Path source, UUID uuid) {
        List<Employee> sourceList = employeeReader.readXML(source);

        sourceList.removeIf(employee -> employee.getId().equals(uuid));

        employeeWriter.writeXML(source, sourceList);
    }

    public void removeEmployerByFullName(Path source, String fullName) {
        List<Employee> sourceList = employeeReader.readXML(source);

        sourceList.removeIf(employee -> employee.getFullName().equals(fullName));

        employeeWriter.writeXML(source, sourceList);
    }

    // удаляешь старого и получаешь его, на основе его создаешь нового и добавляешь в список
    public void changeEmployeeType(Path source, UUID uuid, EmployeeType employeeType) {

        List<Employee> sourceList = employeeReader.readXML(source);

        for (Employee employee : sourceList) {
            if (employee.getId().equals(uuid)) {

                switch (employeeType) {
                    case EMPLOYEE -> {
                        if (employee.getClass().equals(Employee.class)) {
                            throw new InvalidTypeException("employee type is already employee");
                        }

                        sourceList.remove(employee);
                        employee = new Employee(employee.getId(), employee.getFullName(), employee.getBirthdayDate(), employee.getHiringDate());
                        sourceList.add(employee);
                        // TODO finish it

                    }
                    case OTHER_EMPLOYEE -> {
                    }
                    case MANAGER -> {
                    }
                }
            }
        }
    }

    public void linkEmployeeToManager(Employee employee, Manager manager) {
        // TODO добавить путь возможно а возможно и не надо
        manager.getSubordinates().add(employee);
    }

    public void sortByFullName(Path source) {
        List<Employee> sourceList = employeeReader.readXML(source);
        sourceList.sort(Comparator.comparing(Employee::getFullName));
        employeeWriter.writeXML(source, sourceList);
    }

    public void sortByHiringDate(Path source) {
        List<Employee> sourceList = employeeReader.readXML(source);
        sourceList.sort(Comparator.comparing(Employee::getHiringDate));
        employeeWriter.writeXML(source, sourceList);
    }

    // public for tests
    @SneakyThrows
    public void pathValidate(Path path){

        if (path == null){
            throw new IllegalArgumentException("path cannot be null");
        }

        if (!Files.exists(path)){
            throw new NoSuchFileException(path.toString());
        }

        if (Files.size(path) == 0L){
            throw new IllegalArgumentException("File cannot be null");
        }

        employeeReader.checkIsFileDamaged(path);

    }

}
