package tests.util;

import entities.Employee;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import tests.MainXMLTest;
import util.EmployeeWriter;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmployeeWriterTest extends MainXMLTest {
    private final EmployeeWriter employeeWriter = new EmployeeWriter();
    @Test
    void writeXML_writeXMLtoNonExistentFile_throwsNoSuchFileException() {
        Path nonExistentPath = Path.of("non/existent/path");

        NoSuchFileException exception = assertThrows(NoSuchFileException.class,
                () -> employeeWriter.writeXML(nonExistentPath, List.of(employee)));

        assertEquals(exception.getFile(), nonExistentPath.toString());
    }

    @Test
    @SneakyThrows
    void writeXML_writeXMLToNormalFile_write() {
        List<Employee> employees = getEmployees();

        employeeWriter.writeXML(TEMP_FILE_PATH, employees);

        String expected = Files.readString(BIG_LIST_OF_EMPLOYEES_PATH);
        String actual = Files.readString(TEMP_FILE_PATH);

        assertEquals(expected, actual);
    }

    @Test
    @SneakyThrows
    void writeXML_writeXMLToNormalFileWithNullEmployees_write() {
        assertThrows(IllegalArgumentException.class,
                () -> employeeWriter.writeXML(TEMP_FILE_PATH, null));
    }

    @Test
    @SneakyThrows
    void writeXML_writeXMLToNormalFileWithEmptyEmployees_write() {
        assertThrows(IllegalArgumentException.class,
                () -> employeeWriter.writeXML(TEMP_FILE_PATH, new ArrayList<>()));
    }
}