package com.skodin.services;

import com.skodin.entities.Employee;
import com.skodin.entities.Manager;
import com.skodin.entities.enums.EmployeeType;
import com.skodin.exceptions.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import com.skodin.MainXMLTest;
import com.skodin.util.EmployeeReader;
import com.skodin.util.EmployeeWriter;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest extends MainXMLTest {
    private final EmployeeService employeeService = new EmployeeService();
    private final EmployeeWriter employeeWriter = new EmployeeWriter();
    private final EmployeeReader employeeReader = new EmployeeReader();

    @Test
    void addNewEmployers_nonExistentSourceFile_throwsFileNotFoundException() {
        Path nonExistentPath = Path.of("non/existent/file");

        assertThrows(FileNotFoundException.class,
                () -> employeeService.addNewEmployers(nonExistentPath, TEMP_FILE_PATH));
    }

    @Test
    void addNewEmployers_emptySourceFile_throwsFileIsEmptyException() {
        assertThrows(FileIsEmptyException.class,
                () -> employeeService.addNewEmployers(TEMP_FILE_PATH, null));
    }

    @Test
    @SneakyThrows
    void addNewEmployers_damagedSourceFileAndNotNullTargetPath_throwsDamagedFileException() {
        Path path = Path.of("non/existent/path");
        Files.copy(EMPLOYEE_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);

        Files.writeString(TEMP_FILE_PATH, "invalid text", StandardOpenOption.APPEND);

        assertThrows(DamagedFileException.class,
                () -> employeeService.addNewEmployers(TEMP_FILE_PATH, path));
    }

    @Test
    @SneakyThrows
    void addNewEmployers_incorrectSourceContentFile_throwsIncorrectContentException() {

        Path path = Path.of("non/existent/path");

        String text = """
                <?xml version="1.0" encoding="UTF-8" standalone="no"?>
                <employees>
                    <employee id="c3be8edf-3ca8-44f5-a25c-57389066c455">
                        <name>Skodin Nikita Dm</name>
                        <birthdayDate>2024-01-02</birthdayDate>
                        <hiringDate>2024-01-02</hiringDate>
                    </employee>
                </employees>
                """;
        Files.writeString(TEMP_FILE_PATH, text);

        assertThrows(IncorrectContentException.class,
                () -> employeeService.addNewEmployers(TEMP_FILE_PATH, path));
    }

    @Test
    @SneakyThrows
    void addNewEmployers_nullSourceFile_throwsPathIsNullException() {
        assertThrows(PathIsNullException.class,
                () -> employeeService.addNewEmployers(null, null));
    }

    @Test
    @SneakyThrows
    void addNewEmployers_nonExistentTargetFile_createXMLFileAndAddNewEmployee() {
        Files.deleteIfExists(TEMP_FILE_PATH);

        employeeService.addNewEmployers(EMPLOYEES_PATH, TEMP_FILE_PATH);

        String expected = Files.readString(EMPLOYEES_PATH);
        String actual = Files.readString(TEMP_FILE_PATH);

        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void addNewEmployers_emptySourceFileAndValidSourceFile_addNewEmployeeToEmptyFile() {
        employeeService.addNewEmployers(EMPLOYEES_PATH, TEMP_FILE_PATH);

        String expected = Files.readString(EMPLOYEES_PATH);
        String actual = Files.readString(TEMP_FILE_PATH);

        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void addNewEmployers_damagedTargetFile_throwsDamagedFileException() {
        Files.copy(EMPLOYEES_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);

        Files.writeString(TEMP_FILE_PATH, "invalid text", StandardOpenOption.APPEND);

        assertThrows(DamagedFileException.class,
                () -> employeeService.addNewEmployers(EMPLOYEES_PATH, TEMP_FILE_PATH));
    }

    @Test
    @SneakyThrows
    void addNewEmployers_incorrectTargetContentFile_throwsIncorrectContentException() {
        String text = """
                <?xml version="1.0" encoding="UTF-8" standalone="no"?>
                <employees>
                    <employee id="c3be8edf-3ca8-44f5-a25c-57389066c455">
                        <name>Skodin Nikita Dm</name>
                        <birthdayDate>2024-01-02</birthdayDate>
                        <hiringDate>2024-01-02</hiringDate>
                    </employee>
                </employees>
                """;
        Files.writeString(TEMP_FILE_PATH, text);

        assertThrows(IncorrectContentException.class,
                () -> employeeService.addNewEmployers(EMPLOYEE_PATH, TEMP_FILE_PATH));
    }

    @Test
    @SneakyThrows
    void addNewEmployers_nullTargetFile_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> employeeService.addNewEmployers(EMPLOYEE_PATH, null));
    }

    @Test
    @SneakyThrows
    void addNewEmployers_validSourceFileAndValidTargetFile_addOneNewEmployee() {
        Files.copy(EMPLOYEE_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);

        Path tempFile = Files.createTempFile("temp", ".xml");
        Employee newEmployee = new Employee(UUID.randomUUID(), "name", DATE, DATE);
        employeeWriter.writeXML(tempFile, List.of(newEmployee));
        employeeService.addNewEmployers(tempFile, TEMP_FILE_PATH);

        List<Employee> list = employeeReader.readXML(TEMP_FILE_PATH);

        assertNotNull(list);
        assertEquals(2, list.size());
    }

    @Test
    @SneakyThrows
    void addNewEmployers_bothValidFiles_addSomeNewEmployees() {
        Path tempFile = Files.createTempFile("tempTestFile", ".xml");
        Files.copy(EMPLOYEE_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);

        ArrayList<Employee> employees = getEmployees();
        employees.remove(0);

        employeeWriter.writeXML(tempFile, employees);

        employeeService.addNewEmployers(tempFile, TEMP_FILE_PATH);

        String expected = Files.readString(BIG_LIST_OF_EMPLOYEES_PATH);
        String actual = Files.readString(TEMP_FILE_PATH);

        assertEquals(expected, actual);

    }


    @Test
    @SneakyThrows
    void removeEmployerById_listContainsEmployeeWithSuchId_removeEmployeeWithId() {
        Files.copy(EMPLOYEE_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);

        boolean removed = employeeService.removeEmployerById(TEMP_FILE_PATH, ID.toString());
        String readString = Files.readString(TEMP_FILE_PATH);

        assertTrue(removed);
        assertEquals(EMPTY_XML_FILE, readString);
    }

    @Test
    @SneakyThrows
    void removeEmployerById_listDontContainsEmployeeWithSuchId_returnsFalse() {
        Files.copy(EMPLOYEE_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
        String expected = Files.readString(EMPLOYEE_PATH);

        boolean removed = employeeService.removeEmployerById(TEMP_FILE_PATH, UUID.randomUUID().toString());

        String readString = Files.readString(TEMP_FILE_PATH);

        assertFalse(removed);
        assertEquals(expected, readString);
    }

    @Test
    @SneakyThrows
    void removeEmployerByFullName_listContainsEmployeeWithSuchName_removeEmployeeWithName() {
        Files.copy(EMPLOYEE_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);

        boolean removed = employeeService.removeEmployerByFullName(TEMP_FILE_PATH, EMPLOYEE.getFullName());
        String readString = Files.readString(TEMP_FILE_PATH);

        assertTrue(removed);
        assertEquals(EMPTY_XML_FILE, readString);
    }

    @Test
    @SneakyThrows
    void removeEmployerByFullName_listDontContainsEmployeeWithSuchName_returnsFalse() {
        Files.copy(EMPLOYEE_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
        String expected = Files.readString(EMPLOYEE_PATH);

        boolean removed = employeeService.removeEmployerByFullName(TEMP_FILE_PATH, "NoName");

        String readString = Files.readString(TEMP_FILE_PATH);

        assertFalse(removed);
        assertEquals(expected, readString);
    }

    @Test
    @SneakyThrows
    void changeEmployeeType_ValidFile_changeEmployeeTypeFromEmployeeToManager() {
        Files.copy(EMPLOYEE_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
        String expected = Files.readString(MANAGER_PATH);

        boolean isChanged = employeeService.changeEmployeeType(TEMP_FILE_PATH, STRING_ID, EmployeeType.MANAGER.toString(), "");

        String actual = Files.readString(TEMP_FILE_PATH);

        assertTrue(isChanged);
        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void changeEmployeeType_ValidFile_changeEmployeeTypeFromManagerToOtherEmployee() {
        Files.copy(MANAGER_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
        String expected = Files.readString(OTHER_EMPLOYEE_PATH);

        boolean isChanged = employeeService.changeEmployeeType(TEMP_FILE_PATH, STRING_ID, EmployeeType.OTHER_EMPLOYEE.toString(), "DESCRIPTION");

        String actual = Files.readString(TEMP_FILE_PATH);

        assertTrue(isChanged);
        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void changeEmployeeType_ValidFile_changeEmployeeTypeFromOtherEmployeeToEmployee() {
        Files.copy(OTHER_EMPLOYEE_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
        String expected = Files.readString(EMPLOYEE_PATH);

        boolean isChanged = employeeService.changeEmployeeType(TEMP_FILE_PATH, STRING_ID, EmployeeType.EMPLOYEE.toString(), "");

        String actual = Files.readString(TEMP_FILE_PATH);

        assertTrue(isChanged);
        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void changeEmployeeType_ValidFileAndSimilarTypes_throwsInvalidTypeException() {
        Files.copy(EMPLOYEE_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);

        assertThrows(InvalidTypeException.class,
                () -> employeeService.changeEmployeeType(TEMP_FILE_PATH, STRING_ID, EmployeeType.EMPLOYEE.toString(), ""));

    }

    @Test
    @SneakyThrows
    void changeEmployeeType_ValidFileAndNonExistentId_returnsFalse() {
        Files.copy(OTHER_EMPLOYEE_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
        String expected = Files.readString(EMPLOYEE_PATH);

        boolean isChanged = employeeService.changeEmployeeType(TEMP_FILE_PATH, UUID.randomUUID().toString(), EmployeeType.EMPLOYEE.toString(), "");

        String actual = Files.readString(EMPLOYEE_PATH);

        assertFalse(isChanged);
        assertEquals(expected, actual);
    }


    @Test
    @SneakyThrows
    void linkEmployeeToManager_ValidInputData_ReturnsTrue() {

        String managerStringId = "dd82a20d-a11f-4610-86a1-c8bfc585eb79";
        String employeeStringId = "49486d44-a487-4d62-aac7-0171917a3386";

        UUID managerUuidId = UUID.fromString(managerStringId);
        UUID employeeUuidId = UUID.fromString(employeeStringId);

        Files.copy(EMPLOYEES_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);

        boolean isLinked = employeeService.assignEmployeeToManager(TEMP_FILE_PATH, managerStringId, employeeStringId);

        List<Employee> list = employeeReader.readXML(TEMP_FILE_PATH);

        Employee employee = list.stream().filter(e -> e.getId().equals(managerUuidId)).findFirst().orElse(null);

        Employee employee1 = null;
        if (employee != null && employee.getClass().equals(Manager.class)) {
            employee1 = ((Manager) employee).getSubordinates().stream()
                    .filter(e -> e.getId().equals(employeeUuidId)).findFirst().orElse(null);
        }

        assertNotNull(employee1);
        assertTrue(isLinked);
    }

    @Test
    @SneakyThrows
    void sortByFullName_ValidFile_rewritesSortedList() {
        Files.copy(EMPLOYEES_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);

        employeeService.sortByFullName(TEMP_FILE_PATH);

        List<Employee> actual = employeeReader.readXML(EMPLOYEES_PATH);
        List<Employee> expected = employeeReader.readXML(EMPLOYEES_PATH);
        expected.sort(Comparator.comparing(Employee::getFullName));

        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void sortByHiringDate_ValidFile_rewritesSortedList() {
        Files.copy(EMPLOYEES_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);

        employeeService.sortByHiringDate(TEMP_FILE_PATH);

        List<Employee> actual = employeeReader.readXML(EMPLOYEES_PATH);
        List<Employee> expected = employeeReader.readXML(EMPLOYEES_PATH);
        expected.sort(Comparator.comparing(Employee::getHiringDate));

        assertEquals(expected, actual);
    }
}