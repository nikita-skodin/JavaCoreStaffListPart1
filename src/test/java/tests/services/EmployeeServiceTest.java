package tests.services;

import entities.Employee;
import entities.Manager;
import entities.enums.EmployeeType;
import exceptions.DamagedFileException;
import exceptions.IncorrectContentException;
import exceptions.InvalidTypeException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import services.EmployeeService;
import tests.MainXMLTest;
import util.EmployeeReader;
import util.EmployeeWriter;

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
    void addNewEmployers_addNewEmployeeFromNonExistentFile_throwsNoSuchFileException() {
        Path nonExistentPath = Path.of("non/existent/file");

        assertThrows(NoSuchFileException.class,
                () -> employeeService.addNewEmployers(nonExistentPath, TEMP_FILE_PATH));
    }

    @Test
    void addNewEmployers_addNewEmployeeFromEmptyFile_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> employeeService.addNewEmployers(TEMP_FILE_PATH, null));
    }

    @Test
    @SneakyThrows
    void addNewEmployers_addNewEmployeeFromDamagedFile_throwsDamagedFileException() {
        Path path = Path.of("non/existent/path");
        Files.copy(EMPLOYEE_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);

        Files.writeString(TEMP_FILE_PATH, "invalid text", StandardOpenOption.APPEND);

        assertThrows(DamagedFileException.class,
                () -> employeeService.addNewEmployers(TEMP_FILE_PATH, path));
    }

    @Test
    @SneakyThrows
    void addNewEmployers_addNewEmployeeFromIncorrectContentFile_throwsIncorrectContentException() {

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
    void addNewEmployers_addNewEmployeeFromNullFile_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> employeeService.addNewEmployers(null, null));
    }

    @Test
    @SneakyThrows
    void addNewEmployers_addNewEmployeeToNonExistentFile_createXMLFileAndAddNewEmployee() {
        Files.deleteIfExists(TEMP_FILE_PATH);

        employeeService.addNewEmployers(EMPLOYEES_PATH, TEMP_FILE_PATH);

        String expected = Files.readString(EMPLOYEES_PATH);
        String actual = Files.readString(TEMP_FILE_PATH);

        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void addNewEmployers_addNewEmployeeToEmptyFile_addNewEmployee() {
        employeeService.addNewEmployers(EMPLOYEES_PATH, TEMP_FILE_PATH);

        String expected = Files.readString(EMPLOYEES_PATH);
        String actual = Files.readString(TEMP_FILE_PATH);

        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void addNewEmployers_addNewEmployeeToDamagedFile_throwsDamagedFileException() {
        Files.copy(EMPLOYEES_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);

        Files.writeString(TEMP_FILE_PATH, "invalid text", StandardOpenOption.APPEND);

        assertThrows(DamagedFileException.class,
                () -> employeeService.addNewEmployers(EMPLOYEES_PATH, TEMP_FILE_PATH));
    }

    @Test
    @SneakyThrows
    void addNewEmployers_addNewEmployeeToIncorrectContentFile_throwsIncorrectContentException() {
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
    void addNewEmployers_addNewEmployeeToNullFile_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> employeeService.addNewEmployers(EMPLOYEE_PATH, null));
    }

    @Test
    @SneakyThrows
    void addNewEmployers_addNewEmployeeFromNormalFileToNormalFile_addOneNewEmployee() {
        Files.copy(EMPLOYEE_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);

        employeeService.addNewEmployers(EMPLOYEE_PATH, TEMP_FILE_PATH);

        List<Employee> list = employeeReader.readXML(TEMP_FILE_PATH);

        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals(EMPLOYEE, list.get(0));
        assertEquals(EMPLOYEE, list.get(1));

    }

    @Test
    @SneakyThrows
    void addNewEmployers_addNewEmployeesFromNormalFileToNormalFile_addSomeNewEmployees() {
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
    void removeEmployerById_listHasEmployeeWithSuchId_removeEmployeeWithId() {
        Files.copy(EMPLOYEE_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);

        boolean removed = employeeService.removeEmployerById(TEMP_FILE_PATH, ID.toString());
        String readString = Files.readString(TEMP_FILE_PATH);

        assertTrue(removed);
        assertEquals(EMPTY_XML_FILE, readString);
    }

    @Test
    @SneakyThrows
    void removeEmployerById_listHasNotEmployeeWithSuchId_doNothing() {
        Files.copy(EMPLOYEE_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
        String expected = Files.readString(EMPLOYEE_PATH);

        boolean removed = employeeService.removeEmployerById(TEMP_FILE_PATH, UUID.randomUUID().toString());

        String readString = Files.readString(TEMP_FILE_PATH);

        assertFalse(removed);
        assertEquals(expected, readString);
    }

    @Test
    @SneakyThrows
    void removeEmployerByFullName_listHasEmployeeWithSuchName_removeEmployeeWithName() {
        Files.copy(EMPLOYEE_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);

        boolean removed = employeeService.removeEmployerByFullName(TEMP_FILE_PATH, EMPLOYEE.getFullName());
        String readString = Files.readString(TEMP_FILE_PATH);

        assertTrue(removed);
        assertEquals(EMPTY_XML_FILE, readString);
    }

    @Test
    @SneakyThrows
    void removeEmployerByFullName_listHasNotEmployeeWithSuchName_doNothing() {
        Files.copy(EMPLOYEE_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
        String expected = Files.readString(EMPLOYEE_PATH);

        boolean removed = employeeService.removeEmployerByFullName(TEMP_FILE_PATH, "NoName");

        String readString = Files.readString(TEMP_FILE_PATH);

        assertFalse(removed);
        assertEquals(expected, readString);
    }

    @Test
    @SneakyThrows
    void changeEmployeeType_NormalFile_changeEmployeeTypeFromEmployeeToManager() {
        Files.copy(EMPLOYEE_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
        String expected = Files.readString(MANAGER_PATH);

        boolean isChanged = employeeService.changeEmployeeType(TEMP_FILE_PATH, STRING_ID, EmployeeType.MANAGER);

        String actual = Files.readString(TEMP_FILE_PATH);

        assertTrue(isChanged);
        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void changeEmployeeType_NormalFile_changeEmployeeTypeFromManagerToOtherEmployee() {
        Files.copy(MANAGER_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
        String expected = Files.readString(OTHER_EMPLOYEE_PATH);

        boolean isChanged = employeeService.changeEmployeeType(TEMP_FILE_PATH, STRING_ID, EmployeeType.OTHER_EMPLOYEE);

        String actual = Files.readString(TEMP_FILE_PATH);

        assertTrue(isChanged);
        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void changeEmployeeType_NormalFile_changeEmployeeTypeFromOtherEmployeeToEmployee() {
        Files.copy(OTHER_EMPLOYEE_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
        String expected = Files.readString(EMPLOYEE_PATH);

        boolean isChanged = employeeService.changeEmployeeType(TEMP_FILE_PATH, STRING_ID, EmployeeType.EMPLOYEE);

        String actual = Files.readString(TEMP_FILE_PATH);

        assertTrue(isChanged);
        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void changeEmployeeType_NormalFileAndSimilarTypes_throwsInvalidTypeException() {
        Files.copy(EMPLOYEE_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);

        assertThrows(InvalidTypeException.class,
                () -> employeeService.changeEmployeeType(TEMP_FILE_PATH, STRING_ID, EmployeeType.EMPLOYEE));

    }

    @Test
    @SneakyThrows
    void changeEmployeeType_NormalFileAndNonExistentId_returnsFalseAndDoNothing() {
        Files.copy(OTHER_EMPLOYEE_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);
        String expected = Files.readString(EMPLOYEE_PATH);

        boolean isChanged = employeeService.changeEmployeeType(TEMP_FILE_PATH, UUID.randomUUID().toString(), EmployeeType.EMPLOYEE);

        String actual = Files.readString(EMPLOYEE_PATH);

        assertFalse(isChanged);
        assertEquals(expected, actual);
    }


    @Test
    @SneakyThrows
    void linkEmployeeToManager() {

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
    void sortByFullName_NormalFile_rewritesSortedList() {
        Files.copy(EMPLOYEES_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);

        employeeService.sortByFullName(TEMP_FILE_PATH);

        List<Employee> actual = employeeReader.readXML(EMPLOYEES_PATH);
        List<Employee> expected = employeeReader.readXML(EMPLOYEES_PATH);
        expected.sort(Comparator.comparing(Employee::getFullName));

        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void sortByHiringDate_NormalFile_rewritesSortedList() {
        Files.copy(EMPLOYEES_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);

        employeeService.sortByHiringDate(TEMP_FILE_PATH);

        List<Employee> actual = employeeReader.readXML(EMPLOYEES_PATH);
        List<Employee> expected = employeeReader.readXML(EMPLOYEES_PATH);
        expected.sort(Comparator.comparing(Employee::getHiringDate));

        assertEquals(expected, actual);
    }
}