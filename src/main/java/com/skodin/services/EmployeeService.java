package com.skodin.services;

import com.skodin.entities.Employee;
import com.skodin.entities.Manager;
import com.skodin.entities.OtherEmployee;
import com.skodin.entities.enums.EmployeeType;
import com.skodin.exceptions.*;
import lombok.SneakyThrows;
import com.skodin.util.EmployeeReader;
import com.skodin.util.EmployeeWriter;
import lombok.extern.log4j.Log4j2;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Log4j2
public class EmployeeService {

    private final EmployeeReader employeeReader = new EmployeeReader();
    private final EmployeeWriter employeeWriter = new EmployeeWriter();

    /**
     * @throws PathIsNullException       if path is null
     * @throws FileNotFoundException     if file not found
     * @throws DamagedFileException      if file is not .xml or damaged
     * @throws IncorrectContentException if file has incorrect tag
     */
    @SneakyThrows
    public List<Employee> getAllEmployees(Path source) {
        try {
            return employeeReader.readXML(source);
        } catch (FileIsEmptyException e) {
            return new ArrayList<>();
        }
    }

    /**
     * @throws PathIsNullException       if at least one path is null
     * @throws FileNotFoundException     if at least one file not found
     * @throws FileIsEmptyException      if source file is empty
     * @throws DamagedFileException      if at least one file is not .xml or damaged
     * @throws IncorrectContentException if at least one file has incorrect tag
     */
    @SneakyThrows
    public boolean addNewEmployers(Path source, Path target) {

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

        for (Employee employee : sourceList) {
            if (targetList.contains(employee)) {
                return false;
            }
        }

        targetList.addAll(sourceList);

        employeeWriter.writeXML(target, targetList);
        return true;
    }


    /**
     * @throws InvalidIdException        if id is not valid
     * @throws PathIsNullException       if path is null
     * @throws FileNotFoundException     if file not found
     * @throws FileIsEmptyException      if file is empty
     * @throws DamagedFileException      if file is not .xml or damaged
     * @throws IncorrectContentException if file has incorrect tag
     */
    public boolean removeEmployerById(Path source, String id) {

        UUID uuid = getUUIDFromStringOrThrowException(id);

        employeeReader.pathValidate(source);

        List<Employee> sourceList = employeeReader.readXML(source);

        boolean isRemoved = sourceList.removeIf(employee -> employee.getId().equals(uuid));

        if (!isRemoved) {
            return false;
        }

        employeeWriter.writeXML(source, sourceList);

        return true;
    }


    /**
     * @throws PathIsNullException       if path is null
     * @throws FileNotFoundException     if file not found
     * @throws FileIsEmptyException      if file is empty
     * @throws DamagedFileException      if file is not .xml or damaged
     * @throws IncorrectContentException if file has incorrect tag
     */
    public boolean removeEmployerByFullName(Path source, String fullName) {

        employeeReader.pathValidate(source);

        List<Employee> sourceList = employeeReader.readXML(source);

        boolean isRemoved = sourceList.removeIf(employee -> employee.getFullName().equals(fullName));

        if (!isRemoved) {
            return false;
        }

        employeeWriter.writeXML(source, sourceList);

        return true;
    }


    /**
     * @throws InvalidIdException        if id is not valid
     * @throws PathIsNullException       if path is null
     * @throws FileNotFoundException     if file not found
     * @throws FileIsEmptyException      if file is empty
     * @throws DamagedFileException      if file is not .xml or damaged
     * @throws IncorrectContentException if file has incorrect tag
     * @throws InvalidTypeException      if employeeById type is already employeeType
     */
    public boolean changeEmployeeType(Path source, String id, String type, String description) {

        EmployeeType employeeType;
        try {
            employeeType = EmployeeType.valueOf(type.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputDataExceptions("No such type of employee");
        }

        UUID uuid = getUUIDFromStringOrThrowException(id);

        employeeReader.pathValidate(source);

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
                sourceList.remove(employee);
                sourceList.add(new OtherEmployee(
                        employee, description
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


    /**
     * @throws PathIsNullException       if path is null
     * @throws FileNotFoundException     if file not found
     * @throws FileIsEmptyException      if file is empty
     * @throws DamagedFileException      if file is not .xml or damaged
     * @throws IncorrectContentException if file has incorrect tag
     * @throws InvalidIdException        if employee with such id is not found
     * @throws InvalidTypeException      if id is invalid or managerById type is not a manager
     */
    public boolean assignEmployeeToManager(Path source, String managerId, String employeeId) {

        UUID managerUUIDId = getUUIDFromStringOrThrowException(managerId);
        UUID employeeUUIDId = getUUIDFromStringOrThrowException(employeeId);

        employeeReader.pathValidate(source);

        List<Employee> list = employeeReader.readXML(source);

        Employee manager = list.stream().filter(e -> e.getId().equals(managerUUIDId))
                .findFirst().orElseThrow(() -> new InvalidIdException("The manager does not exist"));
        Employee employee = list.stream().filter(e -> e.getId().equals(employeeUUIDId))
                .findFirst().orElseThrow(() -> new InvalidIdException("The employee does not exist"));

        if (!manager.getClass().equals(Manager.class)) {
            throw new InvalidTypeException("Employee type is not a manager");
        }

        List<Employee> subordinates = ((Manager) manager).getSubordinates();

        if (subordinates.contains(employee)) {
            return false;
        }
        subordinates.add(employee);

        employeeWriter.writeXML(source, list);

        return true;
    }


    /**
     * @throws PathIsNullException       if path is null
     * @throws FileNotFoundException     if file not found
     * @throws FileIsEmptyException      if file is empty
     * @throws DamagedFileException      if file is not .xml or damaged
     * @throws IncorrectContentException if file has incorrect tag
     */
    public void sortByFullName(Path source) {
        employeeReader.pathValidate(source);
        List<Employee> sourceList = employeeReader.readXML(source);
        sourceList.sort(Comparator.comparing(Employee::getFullName));
        employeeWriter.writeXML(source, sourceList);
    }

    /**
     * @throws PathIsNullException       if path is null
     * @throws FileNotFoundException     if file not found
     * @throws FileIsEmptyException      if file is empty
     * @throws DamagedFileException      if file is not .xml or damaged
     * @throws IncorrectContentException if file has incorrect tag
     */
    public void sortByHiringDate(Path source) {
        employeeReader.pathValidate(source);
        List<Employee> sourceList = employeeReader.readXML(source);
        sourceList.sort(Comparator.comparing(Employee::getHiringDate));
        employeeWriter.writeXML(source, sourceList);
    }

    private static UUID getUUIDFromStringOrThrowException(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            log.warn("Message: " + e);
            throw new InvalidIdException("Id is not valid");
        }
    }
}
