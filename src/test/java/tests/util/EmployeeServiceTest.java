package tests.util;

import entities.Employee;
import exceptions.DamagedFileException;
import exceptions.IncorrectContentException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import tests.MainXMLTest;
import util.EmployeeReader;
import util.EmployeeService;
import util.EmployeeWriter;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest extends MainXMLTest {


    private final EmployeeService employeeService = new EmployeeService();
    private final EmployeeWriter employeeWriter = new EmployeeWriter();
    private final EmployeeReader employeeReader = new EmployeeReader();

    // добавить если ресурс не существует
    // добавить если ресурс пустой
    // добавить если ресурс поврежден
    // добавить если ресурс записан с неверными данными
    // добавить если ресурс null
    // добавить если целевой не существует
    // добавить если целевой пустой
    // добавить если целевой поврежден
    // добавить если целевой записан с неверными данными
    // добавить если целевой null
    // добавить если целевой и ресурс нормальный, в ресурсе один элемент
    // добавить если целевой и ресурс нормальный, в ресурсе не один элемент

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
        assertEquals(employee, list.get(0));
        assertEquals(employee, list.get(1));

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
    void removeEmployerById() {

    }

    @Test
    void removeEmployerByFullName() {
    }

    @Test
    void changeEmployeeType() {
    }

    @Test
    void linkEmployeeToManager() {
    }

    @Test
    void sortByFullName() {
    }

    @Test
    void sortByHiringDate() {
    }

    // передан null
    // не существует
    // пустой
    // поврежден
    // записан с неверными данными
    @Test
    void pathValidate_throwsExceptionIfFileDoesNotExist_throwsNoSuchFileException() {
        Path nonExistentFile = Path.of("non/existent/path");
        assertThrows(NoSuchFileException.class,
                () -> employeeService.pathValidate(nonExistentFile));
    }

    @Test
    void pathValidate_throwsExceptionIfFileIsEmpty_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> employeeService.pathValidate(TEMP_FILE_PATH));
    }

    @Test
    @SneakyThrows
    void pathValidate_throwsExceptionIfFileIsDamaged_throwsDamagedFileException() {
        Files.copy(EMPLOYEES_PATH, TEMP_FILE_PATH, StandardCopyOption.REPLACE_EXISTING);

        Files.writeString(TEMP_FILE_PATH, "invalid text", StandardOpenOption.APPEND);

        assertThrows(DamagedFileException.class,
                () -> employeeService.pathValidate(TEMP_FILE_PATH));
    }

    @Test
    @SneakyThrows
    void pathValidate_throwsExceptionIfPathToFileIsNull_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class,
                () -> employeeService.pathValidate(null));
    }
}