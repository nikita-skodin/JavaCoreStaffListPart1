package tests.util;

import entities.Employee;
import exceptions.DamagedFileException;
import exceptions.IncorrectContentException;
import exceptions.InvalidTypeException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import tests.MainXMLTest;
import util.EmployeeReader;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeReaderTest extends MainXMLTest {

    private final EmployeeReader employeeReader = new EmployeeReader();

    @Test
    void readXML_readFromCorrectFile_returnsListOfEmployees() {
        List<Employee> list = employeeReader.readXML(BIG_LIST_OF_EMPLOYEES_PATH);

        assertNotNull(list);
        assertEquals(getEmployees().size(), list.size());
        assertEquals(getEmployees(), list);
    }

    @Test
    void readXML_readFromNonExistentFile_throwsNoSuchFileException() {
        Path nonExistentPath = Path.of("non/existent/path");

        NoSuchFileException exception = assertThrows(NoSuchFileException.class,
                () -> employeeReader.readXML(nonExistentPath));
        assertEquals(nonExistentPath.toString(), exception.getFile());
    }

    @Test
    void readXML_readFromEmptyFile_returnsEmptyList() {
        List<Employee> list = employeeReader.readXML(TEMP_FILE_PATH);

        assertTrue(list.isEmpty());
    }

    @Test
    @SneakyThrows
    void readXML_readFromDamagedFile_throwsDamagedFileException() {
        Files.deleteIfExists(TEMP_FILE_PATH);
        Files.copy(EMPLOYEES_PATH, TEMP_FILE_PATH);

        Files.writeString(TEMP_FILE_PATH, "invalid text", StandardOpenOption.APPEND);

        assertThrows(DamagedFileException.class,
                () -> employeeReader.readXML(TEMP_FILE_PATH));
    }

    @Test
    @SneakyThrows
    void readXML_readFromIncorrectContentFile_throwsIncorrectContentException() {
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
                () -> employeeReader.readXML(TEMP_FILE_PATH));
    }
}